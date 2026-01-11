package me.filip_jakubowski.bytelab.view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import me.filip_jakubowski.bytelab.NavigationManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class CommandSearchView extends VBox {

    private final List<String> instructions = List.of("ADD", "SUB", "AND", "NAND", "OR", "NOR", "XOR", "NOT", "MOV", "IN", "OUT", "JUMP", "JZ", "NOP");
    private final Map<String, List<String>> dataRegisters = new HashMap<>();
    private final Map<String, List<String>> targetRegisters = new HashMap<>();

    private final TextField inputField = new TextField();
    private final TextField addressField = new TextField();
    private final ListView<String> suggestionsList = new ListView<>();
    private final ListView<String> completedCommands = new ListView<>();

    private final Button saveButton = new Button("Zapisz");
    private final Button loadButton = new Button("Wczytaj");
    private final Button clearButton = new Button("Wyczyść");
    private final Button schemaButton = new Button("Schemat");
    private final Button menuButton = new Button("Menu"); // Nowy przycisk
    private final Button deleteSelectedButton = new Button("Usuń");
    private final Button insertAtButton = new Button("Wstaw (HEX)");

    private enum Stage { INSTRUCTION, DATA, TARGET }
    private Stage currentStage = Stage.INSTRUCTION;
    private String selectedInstruction, selectedData, selectedTarget;
    private final NavigationManager navigationManager;

    public CommandSearchView(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
        setSpacing(10);
        setPadding(new Insets(15));
        setupInstructionMaps();

        inputField.setPromptText("Wpisz instrukcję...");
        addressField.setPromptText("Adres HEX");
        addressField.setPrefWidth(100);

        suggestionsList.setPrefHeight(120);
        suggestionsList.setItems(FXCollections.observableArrayList(instructions));

        VBox.setVgrow(completedCommands, Priority.ALWAYS);

        HBox addressRow = new HBox(5, new Label("Adres:"), addressField, insertAtButton);
        // Dodano menuButton do listy poniżej:
        HBox actionButtons = new HBox(5, saveButton, loadButton, clearButton, deleteSelectedButton, schemaButton, menuButton);

        VBox bottomContainer = new VBox(10, addressRow, actionButtons);

        getChildren().addAll(
                new Label("Kreator instrukcji:"),
                inputField,
                suggestionsList,
                new Label("Pamięć programu:"),
                completedCommands,
                bottomContainer
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
                case "ADD", "SUB", "AND", "NAND", "OR", "NOR", "XOR", "NOT", "MOV", "IN" -> regList;
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
            if (e.getClickCount() == 2) handleSelection(suggestionsList.getSelectionModel().getSelectedItem());
        });

        saveButton.setOnAction(e -> saveToFile());
        loadButton.setOnAction(e -> loadFromFile());
        clearButton.setOnAction(e -> { completedCommands.getItems().clear(); clearSelection(); });
        deleteSelectedButton.setOnAction(e -> {
            int i = completedCommands.getSelectionModel().getSelectedIndex();
            if(i >= 0) { completedCommands.getItems().remove(i); reindexCommands(); }
        });

        // Logika przycisków nawigacji
        schemaButton.setOnAction(e -> navigationManager.openDiagramWindow());
        menuButton.setOnAction(e -> navigationManager.showStartScreen());

        insertAtButton.setOnAction(e -> {
            if (currentStage != Stage.INSTRUCTION) {
                handleSelection(suggestionsList.getSelectionModel().getSelectedItem());
            }
            if (selectedInstruction != null) finalizeCommand();
        });
    }

    private void handleSelection(String selection) {
        String manualInput = inputField.getText().trim();
        if (selection == null && manualInput.isEmpty()) return;

        if (currentStage == Stage.INSTRUCTION) {
            selectedInstruction = selection;
            if ("NOP".equals(selectedInstruction)) {
                selectedData = "-----"; selectedTarget = "-----";
                finalizeCommand();
                return;
            }
            currentStage = Stage.DATA;
            prepareNextStage();
        } else if (currentStage == Stage.DATA) {
            if ("IN".equals(selectedInstruction)) {
                if (validateHex(manualInput)) {
                    selectedData = (manualInput.startsWith("0x") ? "" : "0x") + manualInput.toUpperCase();
                    currentStage = Stage.TARGET;
                    prepareNextStage();
                }
            } else {
                selectedData = selection;
                currentStage = Stage.TARGET;
                prepareNextStage();
            }
        } else {
            if ("JUMP".equals(selectedInstruction) || "JZ".equals(selectedInstruction)) {
                if (validateHex(manualInput)) {
                    selectedTarget = (manualInput.startsWith("0x") ? "" : "0x") + manualInput.toUpperCase();
                    finalizeCommand();
                }
            } else {
                selectedTarget = selection;
                finalizeCommand();
            }
        }
    }

    private void prepareNextStage() {
        inputField.clear();
        List<String> nextItems = (currentStage == Stage.DATA) ? dataRegisters.get(selectedInstruction) : targetRegisters.get(selectedInstruction);
        suggestionsList.setItems(FXCollections.observableArrayList(nextItems));
        suggestionsList.getSelectionModel().selectFirst();
        inputField.setPromptText(currentStage == Stage.DATA ? "Wybierz dane..." : "Wybierz cel...");
        inputField.requestFocus();
    }

    private void finalizeCommand() {
        String cmdBody = String.format("%s %s -> %s", selectedInstruction, selectedData, selectedTarget);
        String addrRaw = addressField.getText().trim();

        if (!addrRaw.isEmpty() && validateHex(addrRaw)) {
            int targetIdx = Integer.parseInt(addrRaw.replaceFirst("(?i)0x", ""), 16);

            while (completedCommands.getItems().size() < targetIdx) {
                completedCommands.getItems().add("NOP ----- -> -----");
            }

            if (targetIdx <= completedCommands.getItems().size()) {
                completedCommands.getItems().add(targetIdx, cmdBody);
            }
        } else {
            completedCommands.getItems().add(cmdBody);
        }

        reindexCommands();
        addressField.clear();
        clearSelection();
    }

    private void reindexCommands() {
        List<String> reindexed = new ArrayList<>();
        for (int i = 0; i < completedCommands.getItems().size(); i++) {
            String line = completedCommands.getItems().get(i);
            String content = line.contains(":") ? line.substring(line.indexOf(":") + 1).trim() : line;
            reindexed.add(String.format("0x%04X: %s", i, content));
        }
        completedCommands.getItems().setAll(reindexed);
    }

    private void clearSelection() {
        currentStage = Stage.INSTRUCTION;
        inputField.clear();
        inputField.setPromptText("Wpisz instrukcję...");
        suggestionsList.setItems(FXCollections.observableArrayList(instructions));
        suggestionsList.getSelectionModel().selectFirst();
        selectedInstruction = selectedData = selectedTarget = null;
    }

    private void saveToFile() {
        FileChooser fc = new FileChooser();
        File f = fc.showSaveDialog(getScene().getWindow());
        if (f != null) {
            try {
                List<String> toSave = completedCommands.getItems().stream()
                        .map(s -> s.contains(":") ? s.substring(s.indexOf(":") + 1).trim() : s)
                        .collect(Collectors.toList());
                Files.write(f.toPath(), toSave);
            } catch (IOException ignored) {}
        }
    }

    private void loadFromFile() {
        FileChooser fc = new FileChooser();
        File f = fc.showOpenDialog(getScene().getWindow());
        if (f != null) {
            try {
                List<String> lines = Files.readAllLines(f.toPath());
                completedCommands.getItems().setAll(lines);
                reindexCommands();
            } catch (IOException ignored) {}
        }
    }

    private boolean validateHex(String hex) { return hex != null && hex.matches("(?i)^(0x)?[0-9A-F]{1,4}$"); }

    private void updateSuggestions(String text) {
        List<String> filtered = instructions.stream()
                .filter(i -> i.startsWith(text.toUpperCase()))
                .collect(Collectors.toList());
        suggestionsList.setItems(FXCollections.observableArrayList(filtered));
        if (!filtered.isEmpty()) suggestionsList.getSelectionModel().selectFirst();
    }

    public ListView<String> getCompletedCommandsListView() { return completedCommands; }
}

