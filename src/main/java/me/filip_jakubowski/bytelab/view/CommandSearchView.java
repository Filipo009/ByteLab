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

    //lista instrukcji
    private final List<String> instructions = List.of(
            "ADD", "SUB", "AND", "OR", "NOT", "MOV", "IN", "OUT", "LOAD", "STORE", "NOP"
    );

    private final Map<String, List<String>> dataRegisters = new HashMap<>();
    private final Map<String, List<String>> targetRegisters = new HashMap<>();

    private final TextField inputField = new TextField();
    private final ListView<String> suggestionsList = new ListView<>();
    private final ListView<String> completedCommands = new ListView<>();

    private final Button clearButton = new Button("Clear");
    private final Button submitButton = new Button("Submit");
    private final Button deleteLastButton = new Button("Usuń ostatni");

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
        submitButton.setDisable(true);

        HBox buttonBox = new HBox(5, submitButton, clearButton, deleteLastButton);

        getChildren().addAll(inputField, suggestionsList, completedCommands, buttonBox);

        setupListeners();
    }

    private void setupInstructionMaps() {
        for (String instr : instructions) {
            switch (instr) {
                case "NOT", "MOV", "OUT", "STORE" -> dataRegisters.put(instr, List.of("REG A", "REG B", "REG C", "REG D"));
                case "NOP" -> dataRegisters.put(instr, List.of("---")); // NOP nie ma rejestrów
                default -> dataRegisters.put(instr, List.of("---"));
            }
        }

        for (String instr : instructions) {
            switch (instr) {
                case "ADD", "SUB", "AND", "OR", "NOT", "MOV", "IN", "LOAD":
                    targetRegisters.put(instr, List.of("REG A", "REG B", "REG C", "REG D"));
                    break;
                case "OUT", "STORE", "NOP":
                    targetRegisters.put(instr, List.of("---")); // NOP docelowy też ---
                    break;
            }
        }
    }

    private void setupListeners() {
        // Filtr podpowiedzi
        inputField.textProperty().addListener((obs, oldText, newText) -> {
            if (currentStage == Stage.INSTRUCTION) updateSuggestions(newText);
        });

        // Nawigacja i Enter
        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN) {
                suggestionsList.requestFocus();
                suggestionsList.getSelectionModel().selectFirst();
            } else if (e.getCode() == KeyCode.ENTER) {
                if (currentStage == Stage.TARGET && selectedTarget != null) {
                    finalizeCommand(); // Enter zatwierdza gotową instrukcję
                } else {
                    String sel = suggestionsList.getSelectionModel().getSelectedItem();
                    if (sel != null) handleSelection(sel);
                }
            }
        });

        suggestionsList.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleSelection(suggestionsList.getSelectionModel().getSelectedItem());
            else if (e.getCode() == KeyCode.UP && suggestionsList.getSelectionModel().getSelectedIndex() == 0) inputField.requestFocus();
        });

        suggestionsList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) handleSelection(suggestionsList.getSelectionModel().getSelectedItem());
        });

        submitButton.setOnAction(e -> finalizeCommand());
        clearButton.setOnAction(e -> clearAll());
        deleteLastButton.setOnAction(e -> {
            ObservableList<String> items = completedCommands.getItems();
            if (!items.isEmpty()) {
                items.remove(items.size() - 1);
                if (commandCounter > 0) commandCounter--;
            }
        });
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

                // NOP od razu pomija etapy danych i docelowych
                if (selectedInstruction.equals("NOP")) {
                    selectedData = "---";
                    selectedTarget = "---";
                    finalizeCommand();
                    return;
                }

                inputField.clear();
                inputField.setPromptText("Wybierz rejestr danych lub zatwierdź enterem...");
                suggestionsList.setItems(FXCollections.observableArrayList(dataRegisters.getOrDefault(selectedInstruction, List.of("---"))));
                suggestionsList.getSelectionModel().selectFirst();
            }
            case DATA -> {
                selectedData = selection;
                currentStage = Stage.TARGET;
                inputField.clear();
                inputField.setPromptText("Wybierz rejestr docelowy...");
                suggestionsList.setItems(FXCollections.observableArrayList(targetRegisters.getOrDefault(selectedInstruction, List.of("---"))));
                suggestionsList.getSelectionModel().selectFirst();
            }
            case TARGET -> {
                selectedTarget = selection;
                inputField.clear();
                inputField.setPromptText("Instrukcja gotowa! Wciśnij Submit lub Enter");
                submitButton.setDisable(false);
            }
        }
    }

    private void finalizeCommand() {
        if (selectedInstruction == null || selectedData == null || selectedTarget == null) return;

        String binaryNumber = Integer.toBinaryString(commandCounter++);
        String command = binaryNumber + ": " + selectedInstruction + " " + selectedData + " -> " + selectedTarget;
        completedCommands.getItems().add(command);
        completedCommands.scrollTo(completedCommands.getItems().size() - 1);
        clearSelection();
    }

    private void clearSelection() {
        currentStage = Stage.INSTRUCTION;
        inputField.clear();
        inputField.setPromptText("Wpisz instrukcję...");
        suggestionsList.setItems(FXCollections.observableArrayList(instructions));
        suggestionsList.getSelectionModel().clearSelection();
        submitButton.setDisable(true);
        selectedInstruction = null;
        selectedData = null;
        selectedTarget = null;
    }

    private void clearAll() {
        clearSelection();
        completedCommands.getItems().clear();
        commandCounter = 0;
    }
}
