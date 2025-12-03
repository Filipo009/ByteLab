package me.filip_jakubowski.bytelab.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.*;

public class CommandSearchView extends VBox {

    private final List<String> instructions = List.of(
            "ADD", "SUB", "AND", "OR", "XOR", "NOT", "MOV", "IN", "OUT", "JUMP", "JZ", "NOP"
    );

    private final Map<String, List<String>> dataRegisters = new HashMap<>();
    private final Map<String, List<String>> targetRegisters = new HashMap<>();

    private final TextField inputField = new TextField();
    private final ListView<String> suggestionsList = new ListView<>();
    private final ListView<String> completedCommands = new ListView<>();

    private final Button clearButton = new Button("Wyczyść");
    private final Button deleteLastButton = new Button("Usuń ostatni");
    private final Button deleteSelectedButton = new Button("Usuń zaznaczony");
    private final Button insertAtButton = new Button("Dodaj w wybranym");
    private final Button undoButton = new Button("Cofnij");
    private final Button redoButton = new Button("Ponów");

    private enum Stage {INSTRUCTION, DATA, TARGET}
    private Stage currentStage = Stage.INSTRUCTION;

    private boolean insertMode = false;
    private int insertTargetAddress = -1;

    private String selectedInstruction;
    private String selectedData;
    private String selectedTarget;

    private int commandCounter = 0;

    // Undo/Redo stacks
    private final Stack<Runnable> undoStack = new Stack<>();
    private final Stack<Runnable> redoStack = new Stack<>();

    public CommandSearchView() {
        setSpacing(5);
        setPadding(new Insets(5));

        setupInstructionMaps();

        inputField.setPromptText("Wpisz instrukcję...");
        inputField.setFocusTraversable(true);

        suggestionsList.setPrefHeight(120);
        completedCommands.setPrefHeight(150);

        suggestionsList.setItems(FXCollections.observableArrayList(instructions));

        HBox buttonBox = new HBox(5, insertAtButton, undoButton, redoButton, clearButton, deleteLastButton, deleteSelectedButton);

        getChildren().addAll(inputField, suggestionsList, completedCommands, buttonBox);

        setupListeners();
        updateUndoRedoButtons();
    }

    private void setupInstructionMaps() {

        List<String> regList = List.of("REG 0", "REG A", "REG B", "REG C", "REG D", "REG E");

        for (String instr : instructions) {
            switch (instr) {
                case "NOT", "MOV", "OUT" -> dataRegisters.put(instr, regList);
                case "NOP" -> dataRegisters.put(instr, List.of("-----"));
                case "IN" -> dataRegisters.put(instr, List.of("WPROWADŹ HEX"));
                case "JUMP", "JZ" -> dataRegisters.put(instr, List.of("-----"));
                default -> dataRegisters.put(instr, List.of("-----"));
            }
        }

        for (String instr : instructions) {
            switch (instr) {
                case "ADD", "SUB", "AND", "OR", "XOR", "NOT", "MOV", "IN":
                    targetRegisters.put(instr, regList);
                    break;
                case "OUT", "NOP":
                    targetRegisters.put(instr, List.of("-----"));
                    break;
                case "JUMP", "JZ":
                    targetRegisters.put(instr, List.of("WPROWADŹ ADRES"));
                    break;
            }
        }
    }

    private void setupListeners() {
        inputField.textProperty().addListener((obs, oldText, newText) -> {
            if (currentStage == Stage.INSTRUCTION) updateSuggestions(newText);
        });

        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN) {
                suggestionsList.requestFocus();
                suggestionsList.getSelectionModel().selectFirst();
            } else if (e.getCode() == KeyCode.ENTER) {
                processEnter();
            }
        });

        suggestionsList.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) processEnter();
            else if (e.getCode() == KeyCode.UP && suggestionsList.getSelectionModel().getSelectedIndex() == 0)
                inputField.requestFocus();
        });

        suggestionsList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) processEnter();
        });

        deleteLastButton.setOnAction(e -> deleteLast());
        deleteSelectedButton.setOnAction(e -> deleteSelected());
        clearButton.setOnAction(e -> clearAll());

        insertAtButton.setOnAction(e -> askInsertAddress());

        undoButton.setOnAction(e -> performUndo());
        redoButton.setOnAction(e -> performRedo());
    }

    private void askInsertAddress() {
        TextInputDialog dialog = new TextInputDialog("0x0000");
        dialog.setTitle("Wybór adresu");
        dialog.setHeaderText("Podaj adres HEX nowej instrukcji:");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String text = result.get().trim();
        if (!validateHex(text)) return;

        insertTargetAddress = Integer.parseInt(text.replace("0x", "").replace("0X", ""), 16);
        insertMode = true;

        if (insertTargetAddress > completedCommands.getItems().size()) {
            for (int i = completedCommands.getItems().size(); i < insertTargetAddress; i++) {
                forceAddAt(i, "NOP", "-----", "-----");
            }
        }

        reindexCompletedCommands();
        clearSelection();
    }

    private void updateSuggestions(String text) {
        ObservableList<String> filtered = FXCollections.observableArrayList();
        for (String instr : instructions)
            if (instr.toUpperCase().startsWith(text.toUpperCase()))
                filtered.add(instr);

        suggestionsList.setItems(filtered.isEmpty()
                ? FXCollections.observableArrayList()
                : filtered);
    }

    private void processEnter() {
        String sel = suggestionsList.getSelectionModel().getSelectedItem();
        if (currentStage == Stage.INSTRUCTION && sel != null) {
            handleSelection(sel);
            return;
        }

        if (currentStage == Stage.DATA) {
            if ("IN".equals(selectedInstruction)) {
                String hex = inputField.getText().trim();
                if (!validateHex(hex)) {
                    inputField.clear();
                    inputField.setPromptText("Błąd HEX!");
                    return;
                }
                selectedData = hex.toUpperCase();
                currentStage = Stage.TARGET;
                inputField.clear();
                showTargetOptions();
                return;
            }
            if (sel != null) {
                handleSelection(sel);
                return;
            }
        }

        if (currentStage == Stage.TARGET)
            finalizeCommand();
    }

    private void showTargetOptions() {
        suggestionsList.setItems(FXCollections.observableArrayList(
                targetRegisters.getOrDefault(selectedInstruction, List.of("-----"))
        ));
        suggestionsList.getSelectionModel().selectFirst();
    }

    private void handleSelection(String selection) {
        switch (currentStage) {
            case INSTRUCTION -> {
                selectedInstruction = selection;
                currentStage = Stage.DATA;

                if ("NOP".equals(selectedInstruction)) {
                    selectedData = "-----";
                    selectedTarget = "-----";
                    finalizeCommand();
                    return;
                }
                if ("IN".equals(selectedInstruction)) {
                    inputField.clear();
                    inputField.setPromptText("Podaj HEX i Enter");
                    suggestionsList.setItems(FXCollections.observableArrayList());
                    return;
                }
                if ("JUMP".equals(selectedInstruction) || "JZ".equals(selectedInstruction)) {
                    selectedData = "-----";
                    currentStage = Stage.TARGET;
                    inputField.setPromptText("Podaj HEX adresu");
                    suggestionsList.setItems(FXCollections.observableArrayList());
                    return;
                }

                inputField.clear();
                inputField.setPromptText("Wybierz rejestr danych");
                suggestionsList.setItems(FXCollections.observableArrayList(
                        dataRegisters.get(selectedInstruction)
                ));
                suggestionsList.getSelectionModel().selectFirst();
            }

            case DATA -> {
                selectedData = selection;
                currentStage = Stage.TARGET;
                inputField.clear();
                showTargetOptions();
            }

            case TARGET -> {
                selectedTarget = selection;
                finalizeCommand();
            }
        }
    }

    private void finalizeCommand() {
        if (selectedInstruction == null) return;

        int index = insertMode ? insertTargetAddress : commandCounter;

        String beforeInsert = completedCommands.getItems().toString();

        forceAddAt(index, selectedInstruction, selectedData, selectedTarget);
        reindexCompletedCommands();

        undoStack.push(() -> {
            completedCommands.getItems().clear();
            completedCommands.getItems().addAll(parseHistory(beforeInsert));
            reindexCompletedCommands();
        });

        redoStack.clear();
        updateUndoRedoButtons();

        insertMode = false;
        insertTargetAddress = -1;

        clearSelection();
    }

    private List<String> parseHistory(String text) {
        text = text.replace("[", "").replace("]", "");
        if (text.isBlank()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(text.split(", ")));
    }

    private void forceAddAt(int index, String instr, String data, String target) {
        String cmd = String.format("0x%04X: %s %s -> %s", index, instr, data, target);
        completedCommands.getItems().add(index, cmd);
    }

    private void reindexCompletedCommands() {
        ObservableList<String> list = completedCommands.getItems();
        for (int i = 0; i < list.size(); i++) {
            String old = list.get(i);
            int colon = old.indexOf(":");
            list.set(i, String.format("0x%04X", i) + old.substring(colon));
        }
        commandCounter = list.size();
    }

    private void deleteLast() {
        if (completedCommands.getItems().isEmpty()) return;

        String beforeDelete = completedCommands.getItems().toString();

        completedCommands.getItems().removeLast();
        reindexCompletedCommands();

        undoStack.push(() -> {
            completedCommands.getItems().clear();
            completedCommands.getItems().addAll(parseHistory(beforeDelete));
            reindexCompletedCommands();
        });

        redoStack.clear();
        updateUndoRedoButtons();
    }

    private void deleteSelected() {
        int index = completedCommands.getSelectionModel().getSelectedIndex();
        if (index < 0) return;

        String beforeDelete = completedCommands.getItems().toString();

        completedCommands.getItems().remove(index);
        reindexCompletedCommands();

        undoStack.push(() -> {
            completedCommands.getItems().clear();
            completedCommands.getItems().addAll(parseHistory(beforeDelete));
            reindexCompletedCommands();
        });

        redoStack.clear();
        updateUndoRedoButtons();
    }

    private void clearAll() {
        String beforeClear = completedCommands.getItems().toString();

        completedCommands.getItems().clear();
        commandCounter = 0;
        clearSelection();

        undoStack.push(() -> {
            completedCommands.getItems().clear();
            completedCommands.getItems().addAll(parseHistory(beforeClear));
            reindexCompletedCommands();
        });

        redoStack.clear();
        updateUndoRedoButtons();
    }

    private void clearSelection() {
        currentStage = Stage.INSTRUCTION;
        inputField.clear();
        inputField.setPromptText("Wpisz instrukcję...");
        suggestionsList.setItems(FXCollections.observableArrayList(instructions));
        suggestionsList.getSelectionModel().clearSelection();
        selectedInstruction = null;
        selectedData = null;
        selectedTarget = null;
    }

    private void updateUndoRedoButtons() {
        undoButton.setDisable(undoStack.isEmpty());
        redoButton.setDisable(redoStack.isEmpty());
    }

    private void performUndo() {
        if (undoStack.isEmpty()) return;
        Runnable action = undoStack.pop();
        action.run();
        redoStack.push(action);
        updateUndoRedoButtons();
    }

    private void performRedo() {
        if (redoStack.isEmpty()) return;
        Runnable action = redoStack.pop();
        action.run();
        undoStack.push(action);
        updateUndoRedoButtons();
    }

    private boolean validateHex(String hex) {
        if (hex == null) return false;
        hex = hex.trim();
        if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
        return hex.matches("(?i)^[0-9A-F]{1,4}$");
    }

    public ListView<String> getCompletedCommandsListView() {
        return completedCommands;
    }
}
