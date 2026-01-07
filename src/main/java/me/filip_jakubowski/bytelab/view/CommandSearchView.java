package me.filip_jakubowski.bytelab.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class CommandSearchView extends VBox {

    private final List<String> instructions = List.of(
            "ADD", "SUB", "AND", "OR", "XOR", "NOT", "MOV", "IN", "OUT", "JUMP", "JZ", "NOP"
    );

    private final Map<String, List<String>> dataRegisters = new HashMap<>();
    private final Map<String, List<String>> targetRegisters = new HashMap<>();

    private final TextField inputField = new TextField();
    private final TextField addressField = new TextField();
    private final ListView<String> suggestionsList = new ListView<>();
    private final ListView<String> completedCommands = new ListView<>();

    private final Button saveButton = new Button("Zapisz");
    private final Button loadButton = new Button("Wczytaj");
    private final Button clearButton = new Button("Wyczyść");
    private final Button deleteSelectedButton = new Button("Usuń wybrane");
    private final Button insertAtButton = new Button("Wstaw pod adres (HEX)");

    private enum Stage { INSTRUCTION, DATA, TARGET }
    private Stage currentStage = Stage.INSTRUCTION;

    private String selectedInstruction, selectedData, selectedTarget;

    public CommandSearchView() {
        setSpacing(8);
        setPadding(new Insets(10));
        setupInstructionMaps();

        inputField.setPromptText("Wpisz instrukcję...");
        addressField.setPromptText("Adres HEX (np. 0x05)");
        addressField.setPrefWidth(150);

        suggestionsList.setPrefHeight(120);
        completedCommands.setPrefHeight(200);
        suggestionsList.setItems(FXCollections.observableArrayList(instructions));

        HBox addressBox = new HBox(5, addressField, insertAtButton);
        HBox controlButtons = new HBox(5, saveButton, loadButton, deleteSelectedButton, clearButton);

        getChildren().addAll(
                new Label("Kreator instrukcji:"),
                inputField,
                suggestionsList,
                new Label("Opcje wstawiania:"),
                addressBox,
                new Label("Program:"),
                completedCommands,
                controlButtons
        );

        setupListeners();
    }

    private void setupInstructionMaps() {
        List<String> regList = List.of("REG 0", "REG A", "REG B", "REG C", "REG D", "REG E");
        for (String instr : instructions) {
            dataRegisters.put(instr, switch (instr) {
                case "NOT", "MOV", "OUT" -> regList;
                case "IN" -> List.of("WPROWADŹ HEX");
                default -> List.of("-----");
            });
            targetRegisters.put(instr, switch (instr) {
                case "ADD", "SUB", "AND", "OR", "XOR", "NOT", "MOV", "IN" -> regList;
                case "JUMP", "JZ" -> List.of("WPROWADŹ ADRES");
                default -> List.of("-----");
            });
        }
    }

    private void setupListeners() {
        inputField.textProperty().addListener((obs, old, text) -> {
            if (currentStage == Stage.INSTRUCTION) updateSuggestions(text);
        });

        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN) {
                suggestionsList.requestFocus();
                suggestionsList.getSelectionModel().selectFirst();
            } else if (e.getCode() == KeyCode.ENTER) {
                handleSelection(suggestionsList.getSelectionModel().getSelectedItem());
            }
        });

        suggestionsList.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSelection(suggestionsList.getSelectionModel().getSelectedItem());
            }
        });

        suggestionsList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                handleSelection(suggestionsList.getSelectionModel().getSelectedItem());
            }
        });

        saveButton.setOnAction(e -> saveToFile());
        loadButton.setOnAction(e -> loadFromFile());
        clearButton.setOnAction(e -> clearAll());
        deleteSelectedButton.setOnAction(e -> deleteSelected());

        insertAtButton.setOnAction(e -> {
            if (currentStage != Stage.INSTRUCTION) {
                handleSelection(suggestionsList.getSelectionModel().getSelectedItem());
            }
            if (selectedInstruction != null) {
                finalizeCommand();
            }
        });
    }

    private void handleSelection(String selection) {
        String manualInput = inputField.getText().trim();

        if (currentStage == Stage.INSTRUCTION) {
            if (selection == null) return;
            selectedInstruction = selection;
            if (selection.equals("NOP")) {
                selectedData = "-----"; selectedTarget = "-----";
                finalizeCommand();
                return;
            }
            currentStage = Stage.DATA;
            prepareNextStage();
        }
        else if (currentStage == Stage.DATA) {
            if ("IN".equals(selectedInstruction)) {
                if (validateHex(manualInput)) {
                    selectedData = manualInput.toUpperCase();
                    currentStage = Stage.TARGET;
                    prepareNextStage();
                }
            } else {
                if (selection == null) return;
                selectedData = selection;
                currentStage = Stage.TARGET;
                prepareNextStage();
            }
        }
        else if (currentStage == Stage.TARGET) {
            if ("JUMP".equals(selectedInstruction) || "JZ".equals(selectedInstruction)) {
                if (validateHex(manualInput)) {
                    selectedTarget = manualInput.toUpperCase();
                    finalizeCommand();
                }
            } else {
                if (selection == null) return;
                selectedTarget = selection;
                finalizeCommand();
            }
        }
    }

    private void prepareNextStage() {
        inputField.clear();
        List<String> nextItems = (currentStage == Stage.DATA) ?
                dataRegisters.get(selectedInstruction) : targetRegisters.get(selectedInstruction);
        suggestionsList.setItems(FXCollections.observableArrayList(nextItems));
        suggestionsList.getSelectionModel().selectFirst();
        inputField.setPromptText((currentStage == Stage.DATA) ? "Wybierz dane..." : "Wybierz cel...");
        inputField.requestFocus();
    }

    private void finalizeCommand() {
        String cmdBody = String.format("%s %s -> %s", selectedInstruction, selectedData, selectedTarget);
        String addrRaw = addressField.getText().trim();
        ObservableList<String> items = completedCommands.getItems();

        if (!addrRaw.isEmpty() && validateHex(addrRaw)) {
            int targetIdx = Integer.parseInt(addrRaw.replaceFirst("(?i)0x", ""), 16);
            if (targetIdx < items.size()) {
                items.add(targetIdx, cmdBody);
            } else {
                while (items.size() < targetIdx) {
                    items.add("NOP ----- -> -----");
                }
                items.add(cmdBody);
            }
        } else {
            items.add(cmdBody);
        }

        reindexCommands();
        addressField.clear();
        clearSelection();
    }

    private void reindexCommands() {
        ObservableList<String> items = completedCommands.getItems();
        List<String> reindexed = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            String line = items.get(i);
            String content = line.contains(":") ? line.substring(line.indexOf(":") + 1).trim() : line;
            reindexed.add(String.format("0x%04X: %s", i, content));
        }
        completedCommands.getItems().setAll(reindexed);
    }

    private void saveToFile() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("ByteLab (*.txt)", "*.txt"));
        File file = fc.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try {
                List<String> lines = completedCommands.getItems().stream()
                        .map(s -> s.substring(s.indexOf(":") + 1).trim())
                        .collect(Collectors.toList());
                Files.write(file.toPath(), lines);
            } catch (IOException ignored) {}
        }
    }

    private void loadFromFile() {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                completedCommands.getItems().setAll(lines);
                reindexCommands();
            } catch (IOException ignored) {}
        }
    }

    private void deleteSelected() {
        int idx = completedCommands.getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            completedCommands.getItems().remove(idx);
            reindexCommands();
        }
    }

    private void clearSelection() {
        currentStage = Stage.INSTRUCTION;
        inputField.clear();
        inputField.setPromptText("Wpisz instrukcję...");
        suggestionsList.setItems(FXCollections.observableArrayList(instructions));
        suggestionsList.getSelectionModel().selectFirst();
        selectedInstruction = selectedData = selectedTarget = null;
        inputField.requestFocus();
    }

    private void clearAll() {
        clearSelection();
        completedCommands.getItems().clear();
    }

    private boolean validateHex(String hex) {
        return hex != null && hex.matches("(?i)^(0x)?[0-9A-F]{1,4}$");
    }

    private void updateSuggestions(String text) {
        List<String> filtered = instructions.stream()
                .filter(i -> i.startsWith(text.toUpperCase()))
                .collect(Collectors.toList());
        suggestionsList.setItems(FXCollections.observableArrayList(filtered));
        suggestionsList.getSelectionModel().selectFirst();
    }

    public ListView<String> getCompletedCommandsListView() { return completedCommands; }
}