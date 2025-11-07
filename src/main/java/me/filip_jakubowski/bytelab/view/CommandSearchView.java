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
            "ADD", "SUB", "AND", "OR", "NOT", "MOV", "IN", "OUT", "JUMP", "JZ", "NOP"
    );

    private final Map<String, List<String>> dataRegisters = new HashMap<>();
    private final Map<String, List<String>> targetRegisters = new HashMap<>();

    private final TextField inputField = new TextField();
    private final ListView<String> suggestionsList = new ListView<>();
    private final ListView<String> completedCommands = new ListView<>();

    private final Button clearButton = new Button("Wyczyść");
    private final Button deleteLastButton = new Button("Usuń ostatni");
    private final Button deleteSelectedButton = new Button("Usuń zaznaczony");

    private enum Stage {INSTRUCTION, DATA, TARGET}
    private Stage currentStage = Stage.INSTRUCTION;

    private String selectedInstruction;
    private String selectedData;
    private String selectedTarget;

    private int commandCounter = 0;

    public CommandSearchView() {
        setSpacing(5);
        setPadding(new Insets(5));

        setupInstructionMaps();

        inputField.setPromptText("Wpisz instrukcję...");
        inputField.setFocusTraversable(true);

        suggestionsList.setPrefHeight(120);
        completedCommands.setPrefHeight(150);

        suggestionsList.setItems(FXCollections.observableArrayList(instructions));

        HBox buttonBox = new HBox(5, clearButton, deleteLastButton, deleteSelectedButton);

        getChildren().addAll(inputField, suggestionsList, completedCommands, buttonBox);

        setupListeners();
    }

    private void setupInstructionMaps() {
        for (String instr : instructions) {
            switch (instr) {
                case "NOT", "MOV", "OUT" -> dataRegisters.put(instr, List.of("REG A", "REG B", "REG C", "REG D"));
                case "NOP" -> dataRegisters.put(instr, List.of("-----"));
                case "IN" -> dataRegisters.put(instr, List.of("WPROWADŹ HEX"));
                case "JUMP", "JZ" -> dataRegisters.put(instr, List.of("-----"));
                default -> dataRegisters.put(instr, List.of("-----"));
            }
        }

        for (String instr : instructions) {
            switch (instr) {
                case "ADD", "SUB", "AND", "OR", "NOT", "MOV", "IN":
                    targetRegisters.put(instr, List.of("REG A", "REG B", "REG C", "REG D"));
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
                if (currentStage == Stage.INSTRUCTION) {
                    String sel = suggestionsList.getSelectionModel().getSelectedItem();
                    if (sel != null) {
                        handleSelection(sel);
                        return;
                    }
                } else if (currentStage == Stage.DATA) {
                    if ("IN".equals(selectedInstruction)) {
                        String hex = inputField.getText().trim();
                        if (validateHex(hex)) {
                            selectedData = hex.toUpperCase();
                            currentStage = Stage.TARGET;
                            inputField.clear();
                            inputField.setPromptText("Wybierz rejestr docelowy lub wciśnij Enter aby zatwierdzić wybrany");
                            suggestionsList.setItems(FXCollections.observableArrayList(targetRegisters.getOrDefault(selectedInstruction, List.of("-----"))));
                            suggestionsList.getSelectionModel().selectFirst();
                            return;
                        } else {
                            inputField.clear();
                            inputField.setPromptText("BŁĄD: wprowadź HEX 0-FFFF");
                            return;
                        }
                    }
                    String selData = suggestionsList.getSelectionModel().getSelectedItem();
                    if (selData != null) {
                        handleSelection(selData);
                        return;
                    }
                } else if (currentStage == Stage.TARGET) {
                    String selTarget = suggestionsList.getSelectionModel().getSelectedItem();
                    if (selTarget != null && !selTarget.isEmpty()) {
                        if ("WPROWADŹ ADRES".equals(selTarget) || "WPROWADŹ HEX".equals(selTarget) || "-----".equals(selTarget)) {
                            if ("JUMP".equals(selectedInstruction) || "JZ".equals(selectedInstruction)) {
                                String addr = inputField.getText().trim();
                                if (validateHex(addr)) {
                                    selectedTarget = addr.toUpperCase();
                                    finalizeCommand();
                                    return;
                                } else {
                                    inputField.clear();
                                    inputField.setPromptText("BŁĄD: wprowadź ADRES w HEX (0-FFFF)");
                                    return;
                                }
                            } else {
                                selectedTarget = selTarget;
                                finalizeCommand();
                                return;
                            }
                        } else {
                            selectedTarget = selTarget;
                            finalizeCommand();
                            return;
                        }
                    } else {
                        if ("JUMP".equals(selectedInstruction) || "JZ".equals(selectedInstruction)) {
                            String addr = inputField.getText().trim();
                            if (validateHex(addr)) {
                                selectedTarget = addr.toUpperCase();
                                finalizeCommand();
                                return;
                            } else {
                                inputField.clear();
                                inputField.setPromptText("BŁĄD: wprowadź ADRES w HEX (0-FFFF)");
                                return;
                            }
                        }
                    }
                }
            }
        });

        suggestionsList.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSelection(suggestionsList.getSelectionModel().getSelectedItem());
            } else if (e.getCode() == KeyCode.UP && suggestionsList.getSelectionModel().getSelectedIndex() == 0) {
                inputField.requestFocus();
            }
        });

        suggestionsList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                handleSelection(suggestionsList.getSelectionModel().getSelectedItem());
            }
        });

        clearButton.setOnAction(e -> clearAll());

        deleteLastButton.setOnAction(e -> {
            ObservableList<String> items = completedCommands.getItems();
            if (!items.isEmpty()) {
                items.remove(items.size() - 1);
                // after removal reindex everything and update counter
                reindexCompletedCommands();
                commandCounter = completedCommands.getItems().size();
            }
        });

        deleteSelectedButton.setOnAction(e -> {
            int index = completedCommands.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                completedCommands.getItems().remove(index);
                reindexCompletedCommands();
                commandCounter = completedCommands.getItems().size();
            }
        });
    }

    private boolean validateHex(String hex) {
        if (hex == null) return false;
        String s = hex.trim();
        if (s.startsWith("0x") || s.startsWith("0X")) s = s.substring(2);
        return s.matches("(?i)^[0-9A-F]{1,4}$");
    }

    private void updateSuggestions(String text) {
        ObservableList<String> filtered = FXCollections.observableArrayList();
        for (String instr : instructions) {
            if (instr.toUpperCase().startsWith(text.toUpperCase())) filtered.add(instr);
        }
        suggestionsList.setItems(filtered.isEmpty() ? FXCollections.observableArrayList() : filtered);
    }

    private void handleSelection(String selection) {
        if (selection == null) return;

        switch (currentStage) {
            case INSTRUCTION -> {
                selectedInstruction = selection;
                currentStage = Stage.DATA;

                if (selectedInstruction.equals("NOP")) {
                    selectedData = "-----";
                    selectedTarget = "-----";
                    finalizeCommand();
                    return;
                }

                if (selectedInstruction.equals("IN")) {
                    inputField.clear();
                    inputField.setPromptText("Wprowadź dane HEX (maks 4 znaki) i naciśnij Enter");
                    suggestionsList.setItems(FXCollections.observableArrayList());
                    return;
                }

                if (selectedInstruction.equals("JUMP") || selectedInstruction.equals("JZ")) {
                    selectedData = "-----";
                    currentStage = Stage.TARGET;
                    inputField.clear();
                    inputField.setPromptText("Wprowadź ADRES w HEX (np. 0x00FF) i naciśnij Enter");
                    suggestionsList.setItems(FXCollections.observableArrayList());
                    return;
                }

                inputField.clear();
                inputField.setPromptText("Wybierz rejestr danych i zatwierdź Enterem...");
                suggestionsList.setItems(FXCollections.observableArrayList(
                        dataRegisters.getOrDefault(selectedInstruction, List.of("-----"))));
                suggestionsList.getSelectionModel().selectFirst();
            }

            case DATA -> {
                if (!"IN".equals(selectedInstruction)) {
                    selectedData = selection;
                }
                currentStage = Stage.TARGET;
                inputField.clear();
                inputField.setPromptText("Wybierz rejestr docelowy lub wpisz adres (dla JUMP/JZ) i naciśnij Enter");
                suggestionsList.setItems(FXCollections.observableArrayList(
                        targetRegisters.getOrDefault(selectedInstruction, List.of("-----"))));
                suggestionsList.getSelectionModel().selectFirst();
            }

            case TARGET -> {
                selectedTarget = selection;
                inputField.clear();
                inputField.setPromptText("Instrukcja gotowa! Wciśnij Enter, aby zapisać");
                finalizeCommand();
            }
        }
    }

    private void finalizeCommand() {
        if (selectedInstruction == null) return;
        if ("IN".equals(selectedInstruction)) {
            if (selectedData == null || selectedData.isEmpty()) return;
        } else if (selectedData == null) return;

        if ("JUMP".equals(selectedInstruction) || "JZ".equals(selectedInstruction)) {
            if (selectedTarget == null || selectedTarget.isEmpty()) return;
        } else if (selectedTarget == null) return;

        String hexNumber = String.format("0x%04X", commandCounter++);
        String command = hexNumber + ": " + selectedInstruction + " " + selectedData + " -> " + selectedTarget;
        completedCommands.getItems().add(command);
        completedCommands.scrollTo(completedCommands.getItems().size() - 1);

        clearSelection();
    }

    /**
     * Reindexuje wpisy w completedCommands tak, aby prefiks adresu (0xNNNN) był kolejnym indeksem od 0.
     * Zachowuje treść instrukcji po separatorze ": ".
     */
    private void reindexCompletedCommands() {
        ObservableList<String> items = completedCommands.getItems();
        if (items.isEmpty()) return;

        List<String> newItems = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            String line = items.get(i);
            String after;
            int colon = line.indexOf(":");
            if (colon >= 0 && colon + 2 < line.length()) {
                // zachowaj wszystko po ": " (jeśli obecne)
                after = line.substring(Math.min(colon + 2, line.length()));
            } else {
                // jeśli format nieznany, użyj całej linii (bez numeru)
                // albo w najgorszym wypadku traktuj jako całość
                // spróbuj usunąć ewentualny prefiks do pierwszego spacji
                int firstSpace = line.indexOf(' ');
                if (firstSpace >= 0 && firstSpace + 1 < line.length()) {
                    after = line.substring(firstSpace + 1);
                } else {
                    after = line;
                }
            }
            String newLine = String.format("0x%04X: %s", i, after);
            newItems.add(newLine);
        }
        items.setAll(newItems);
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

    private void clearAll() {
        clearSelection();
        completedCommands.getItems().clear();
        commandCounter = 0;
    }

    public ListView<String> getCompletedCommandsListView() {
        return completedCommands;
    }
}
