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

    // Instrukcje: usunięto LOAD i STORE, dodano JUMP i NOP
    private final List<String> instructions = List.of(
            "ADD", "SUB", "AND", "OR", "NOT", "MOV", "IN", "OUT", "JUMP", "NOP"
    );

    private final Map<String, List<String>> dataRegisters = new HashMap<>();
    private final Map<String, List<String>> targetRegisters = new HashMap<>();

    private final TextField inputField = new TextField();
    private final ListView<String> suggestionsList = new ListView<>();
    private final ListView<String> completedCommands = new ListView<>();

    private final Button clearButton = new Button("Wyczyść");
    private final Button submitButton = new Button("Zatwierdź");
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
        // dataRegisters: co trzeba wybrać jako dane (pierwsza lista)
        for (String instr : instructions) {
            switch (instr) {
                case "NOT", "MOV", "OUT" -> dataRegisters.put(instr, List.of("REG A", "REG B", "REG C", "REG D"));
                case "NOP" -> dataRegisters.put(instr, List.of("---"));
                case "IN" -> dataRegisters.put(instr, List.of("WPROWADŹ HEX")); // IN: dane wpisywane przez użytkownika
                case "JUMP" -> dataRegisters.put(instr, List.of("---")); // JUMP nie ma rejestrów danych
                default -> dataRegisters.put(instr, List.of("---"));
            }
        }

        // targetRegisters: co trzeba wybrać jako cel (druga lista)
        for (String instr : instructions) {
            switch (instr) {
                case "ADD", "SUB", "AND", "OR", "NOT", "MOV", "IN":
                    targetRegisters.put(instr, List.of("REG A", "REG B", "REG C", "REG D"));
                    break;
                case "OUT", "NOP":
                    targetRegisters.put(instr, List.of("---"));
                    break;
                case "JUMP":
                    targetRegisters.put(instr, List.of("WPROWADŹ ADRES")); // JUMP: wpisz adres (HEX)
                    break;
            }
        }
    }

    private void setupListeners() {
        // Filtr podpowiedzi podczas wpisywania instrukcji
        inputField.textProperty().addListener((obs, oldText, newText) -> {
            if (currentStage == Stage.INSTRUCTION) updateSuggestions(newText);
        });

        // Obsługa klawiszy w polu input
        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN) {
                // przejdź na listę sugestii
                suggestionsList.requestFocus();
                suggestionsList.getSelectionModel().selectFirst();
            } else if (e.getCode() == KeyCode.ENTER) {
                // LOGIKA ENTER
                if (currentStage == Stage.INSTRUCTION) {
                    String sel = suggestionsList.getSelectionModel().getSelectedItem();
                    if (sel != null) {
                        handleSelection(sel);
                        return;
                    }
                } else if (currentStage == Stage.DATA) {
                    // Dla IN: wpisujemy HEX w polu input -> zatwierdź dane i przejdź do TARGET
                    if ("IN".equals(selectedInstruction)) {
                        String hex = inputField.getText().trim();
                        if (validateHex(hex)) {
                            selectedData = hex.toUpperCase();
                            // przejdź do wyboru rejestru docelowego
                            currentStage = Stage.TARGET;
                            inputField.clear();
                            inputField.setPromptText("Wybierz rejestr docelowy lub wciśnij Enter aby zatwierdzić wybrany");
                            suggestionsList.setItems(FXCollections.observableArrayList(targetRegisters.getOrDefault(selectedInstruction, List.of("---"))));
                            suggestionsList.getSelectionModel().selectFirst();
                            return;
                        } else {
                            inputField.clear();
                            inputField.setPromptText("BŁĄD: wprowadź HEX 0-FFFF");
                            return;
                        }
                    }
                    // Inne instrukcje: jeśli lista na DATA ma elementy, wybierz aktualny i przejdź dalej
                    String selData = suggestionsList.getSelectionModel().getSelectedItem();
                    if (selData != null) {
                        handleSelection(selData);
                        return;
                    }
                } else if (currentStage == Stage.TARGET) {
                    // Jeśli suggestionsList ma zaznaczenie -> wybierz i sfinalizuj
                    String selTarget = suggestionsList.getSelectionModel().getSelectedItem();
                    if (selTarget != null && !selTarget.isEmpty()) {
                        // Jeśli to pola typu 'WPROWADŹ ADRES' lub '---' które są tylko placeholderami,
                        // a instrukcja wymaga wpisania wartości (JUMP), obsłuż to z pola input
                        if ("WPROWADŹ ADRES".equals(selTarget) || "WPROWADŹ HEX".equals(selTarget) || "---".equals(selTarget)) {
                            if ("JUMP".equals(selectedInstruction)) {
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
                                // jeśli target jest '---' (np. OUT) -> ustaw i finalize
                                selectedTarget = selTarget;
                                finalizeCommand();
                                return;
                            }
                        } else {
                            // normalny target wybierany z listy
                            selectedTarget = selTarget;
                            finalizeCommand();
                            return;
                        }
                    } else {
                        // jeśli lista jest pusta (np. JUMP) to odczytaj pole input jako adres
                        if ("JUMP".equals(selectedInstruction)) {
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

        // Obsługa klawiszy na liście sugestii
        suggestionsList.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSelection(suggestionsList.getSelectionModel().getSelectedItem());
            } else if (e.getCode() == KeyCode.UP && suggestionsList.getSelectionModel().getSelectedIndex() == 0) {
                inputField.requestFocus();
            }
        });

        // Kliknięcie myszką
        suggestionsList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                handleSelection(suggestionsList.getSelectionModel().getSelectedItem());
            }
        });

        // Przyciski
        submitButton.setOnAction(e -> {
            // jeśli użytkownik nie wybrał target z listy ale jest wpisany w input (np. JUMP), próbuj to użyć
            if (currentStage == Stage.TARGET && ("JUMP".equals(selectedInstruction) || suggestionsList.getItems().isEmpty())) {
                String maybe = inputField.getText().trim();
                if (!maybe.isEmpty() && validateHex(maybe)) {
                    selectedTarget = maybe.toUpperCase();
                }
            }
            finalizeCommand();
        });

        clearButton.setOnAction(e -> clearAll());

        deleteLastButton.setOnAction(e -> {
            ObservableList<String> items = completedCommands.getItems();
            if (!items.isEmpty()) {
                items.remove(items.size() - 1);
                if (commandCounter > 0) commandCounter--;
            }
        });
    }

    /**
     * Walidacja HEX: maksymalnie 4 znaki, zakres 0-9 A-F (duże lub małe)
     */
    private boolean validateHex(String hex) {
        if (hex == null) return false;
        String s = hex.trim();
        // akceptujemy też prefix 0x lub 0X, usuwamy go przed walidacją
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
                    selectedData = "---";
                    selectedTarget = "---";
                    finalizeCommand();
                    return;
                }

                if (selectedInstruction.equals("IN")) {
                    // IN: użytkownik wpisuje dane HEX w polu input
                    inputField.clear();
                    inputField.setPromptText("Wprowadź dane HEX (maks 4 znaki) i naciśnij Enter");
                    suggestionsList.setItems(FXCollections.observableArrayList()); // usuń listę
                    return;
                }

                if (selectedInstruction.equals("JUMP")) {
                    // JUMP: nie ma danych, natychmiast przejdź do TARGET (adres)
                    selectedData = "---";
                    currentStage = Stage.TARGET;
                    inputField.clear();
                    inputField.setPromptText("Wprowadź ADRES w HEX (np. 0x00FF) i naciśnij Enter");
                    suggestionsList.setItems(FXCollections.observableArrayList()); // wpisz adres w pole
                    return;
                }

                // standardowy flow: wyświetl rejestry danych do wyboru (często '---')
                inputField.clear();
                inputField.setPromptText("Wybierz rejestr danych lub zatwierdź Enterem...");
                suggestionsList.setItems(FXCollections.observableArrayList(
                        dataRegisters.getOrDefault(selectedInstruction, List.of("---"))));
                suggestionsList.getSelectionModel().selectFirst();
            }

            case DATA -> {
                // jeśli IN - dane przychodzą z input (nie przez listę)
                if (!"IN".equals(selectedInstruction)) {
                    selectedData = selection;
                }
                // przejdź do TARGET
                currentStage = Stage.TARGET;
                inputField.clear();
                inputField.setPromptText("Wybierz rejestr docelowy lub wpisz adres (dla JUMP) i naciśnij Enter");
                suggestionsList.setItems(FXCollections.observableArrayList(
                        targetRegisters.getOrDefault(selectedInstruction, List.of("---"))));
                suggestionsList.getSelectionModel().selectFirst();
            }

            case TARGET -> {
                // Jeżeli wybrano z listy coś (np. REG A) -> ustaw i enable submit
                selectedTarget = selection;
                inputField.clear();
                inputField.setPromptText("Instrukcja gotowa! Wciśnij Zatwierdź lub Enter");
                submitButton.setDisable(false);
                // Jeśli target był placeholderem 'WPROWADŹ ADRES', użytkownik musi wpisać w pole i nacisnąć Enter (obsługiwane w handlerze input)
            }
        }
    }

    private void finalizeCommand() {
        // Warunki bezpieczeństwa
        if (selectedInstruction == null) return;

        // dla IN: selectedData musi być ustawione (wprowadzone i zwalidowane)
        if ("IN".equals(selectedInstruction)) {
            if (selectedData == null || selectedData.isEmpty()) return;
        } else {
            if (selectedData == null) return;
        }

        // dla JUMP: selectedTarget to wpisany adres
        if ("JUMP".equals(selectedInstruction)) {
            if (selectedTarget == null || selectedTarget.isEmpty()) return;
        } else {
            if (selectedTarget == null) return;
        }

        // utwórz polecenie
        String binaryNumber = Integer.toBinaryString(commandCounter++);
        String command = binaryNumber + ": " + selectedInstruction + " " + selectedData + " -> " + selectedTarget;
        completedCommands.getItems().add(command);
        completedCommands.scrollTo(completedCommands.getItems().size() - 1);

        // reset
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
    public ListView<String> getCompletedCommandsListView() {
        return completedCommands;
    }
}
