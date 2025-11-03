package me.filip_jakubowski.bytelab.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class SimulationView extends VBox {

    private final Map<String, Integer> registers = new HashMap<>();
    private final Label regALabel = new Label();
    private final Label regBLabel = new Label();
    private final Label regCLabel = new Label();
    private final Label regDLabel = new Label();
    private final Label outLabel = new Label();

    private final Button runButton = new Button("RUN");
    private final Button stopButton = new Button("STOP");
    private final Button resetButton = new Button("RESET");
    private final Button stepButton = new Button("STEP");
    private final ComboBox<Integer> freqCombo = new ComboBox<>();

    private ListView<String> historyListView;
    private Timeline timeline;
    private int currentInstructionIndex = 0;
    private boolean isRunning = false;
    private boolean jumpedLastInstruction = false; // flaga dla JUMP

    public SimulationView() {
        setPadding(new Insets(10));
        setSpacing(10);
        setAlignment(Pos.TOP_CENTER);

        initializeRegisters();

        GridPane regGrid = createRegisterGrid();
        updateRegisterDisplay();

// Sekcja sterowania
        VBox controlBox = new VBox(10);
        controlBox.setAlignment(Pos.CENTER);

// Wiersz 1 – częstotliwość
        HBox freqBox = new HBox(10);
        freqBox.setAlignment(Pos.CENTER);
        Label freqLabel = new Label("Taktowanie [Hz]:");
        freqCombo.getItems().addAll(1, 2, 4, 8, 16, 32, 64);
        freqCombo.setValue(4);
        freqBox.getChildren().addAll(freqLabel, freqCombo);

// Wiersz 2 – przyciski sterujące
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        styleButtons();
        buttonBox.getChildren().addAll(runButton, stopButton, stepButton, resetButton);

// Dodanie obu wierszy do kontenera
        controlBox.getChildren().addAll(freqBox, buttonBox);

// Dodanie do widoku
        getChildren().addAll(new Label("Symulacja rejestrów"), regGrid, controlBox);


        // Obsługa przycisków
        runButton.setOnAction(e -> startSimulation());
        stopButton.setOnAction(e -> pauseSimulation());
        resetButton.setOnAction(e -> resetSimulation());
        stepButton.setOnAction(e -> stepSimulation());
    }

    private void initializeRegisters() {
        registers.put("A", 0);
        registers.put("B", 0);
        registers.put("C", 0);
        registers.put("D", 0);
        registers.put("OUT", 0);
    }

    private GridPane createRegisterGrid() {
        GridPane regGrid = new GridPane();
        regGrid.setHgap(20);
        regGrid.setVgap(10);
        regGrid.setAlignment(Pos.CENTER);

        regGrid.add(new Label("REG A:"), 0, 0);
        regGrid.add(regALabel, 1, 0);
        regGrid.add(new Label("REG B:"), 0, 1);
        regGrid.add(regBLabel, 1, 1);
        regGrid.add(new Label("REG C:"), 0, 2);
        regGrid.add(regCLabel, 1, 2);
        regGrid.add(new Label("REG D:"), 0, 3);
        regGrid.add(regDLabel, 1, 3);
        regGrid.add(new Label("OUT:"), 0, 4);
        regGrid.add(outLabel, 1, 4);

        return regGrid;
    }

    private void styleButtons() {
        runButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        stopButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        resetButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        stepButton.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black;");
    }

    public void bindHistoryList(ListView<String> listView) {
        this.historyListView = listView;
    }

    // ===== Symulacja =====
    private void startSimulation() {
        if (historyListView == null || historyListView.getItems().isEmpty()) return;

        if (!isRunning) {
            isRunning = true;
            double period = 1000.0 / freqCombo.getValue();

            timeline = new Timeline(new KeyFrame(Duration.millis(period), e -> runNextInstruction()));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

            runButton.setDisable(true);
            stopButton.setDisable(false);
        } else if (timeline != null) {
            timeline.play();
        }
    }

    private void pauseSimulation() {
        if (timeline != null) {
            timeline.pause();
        }
        runButton.setDisable(false);
        stopButton.setDisable(true);
    }

    private void resetSimulation() {
        stopSimulation();
        currentInstructionIndex = 0;
        initializeRegisters();
        updateRegisterDisplay();
        highlightCurrentInstruction();
    }

    private void stopSimulation() {
        if (timeline != null) {
            timeline.stop();
        }
        isRunning = false;
        runButton.setDisable(false);
        stopButton.setDisable(true);
    }

    private void stepSimulation() {
        if (historyListView == null || historyListView.getItems().isEmpty()) return;

        if (currentInstructionIndex >= historyListView.getItems().size()) {
            stopSimulation();
            return;
        }

        jumpedLastInstruction = false;
        String instruction = historyListView.getItems().get(currentInstructionIndex);
        executeInstruction(instruction);
        highlightCurrentInstruction();

        if (!jumpedLastInstruction) currentInstructionIndex++;
    }

    private void runNextInstruction() {
        if (currentInstructionIndex >= historyListView.getItems().size()) {
            stopSimulation();
            return;
        }

        jumpedLastInstruction = false;
        String instruction = historyListView.getItems().get(currentInstructionIndex);
        executeInstruction(instruction);
        highlightCurrentInstruction();

        if (!jumpedLastInstruction) currentInstructionIndex++;

        if (currentInstructionIndex >= historyListView.getItems().size()) {
            stopSimulation();
        }
    }

    // ===== Logika instrukcji =====
    private void executeInstruction(String instruction) {
        if (instruction == null || instruction.isBlank()) return;
        String[] parts = instruction.trim().split("\\s+");
        String command = parts[0].toUpperCase();

        switch (command) {
            case "ADD" -> registers.put("OUT", registers.get("A") + registers.get("B"));
            case "SUB" -> registers.put("OUT", registers.get("A") - registers.get("B"));
            case "AND" -> registers.put("OUT", registers.get("A") & registers.get("B"));
            case "OR" -> registers.put("OUT", registers.get("A") | registers.get("B"));
            case "NOT" -> registers.put("OUT", ~registers.get("A") & 0xFFFF);
            case "MOV" -> handleMov(parts);
            case "IN" -> handleIn(parts);
            case "OUT" -> handleOut(parts);
            case "NOP" -> { /* brak akcji */ }
            case "JUMP" -> handleJump(parts);
        }

        updateRegisterDisplay();
    }

    private void handleMov(String[] parts) {
        if (parts.length >= 3) {
            String from = parts[1].replace("REG", "").trim();
            String to = parts[2].replace("REG", "").trim();
            if (registers.containsKey(from) && registers.containsKey(to)) {
                registers.put(to, registers.get(from));
            }
        }
    }

    private void handleIn(String[] parts) {
        if (parts.length >= 3) {
            String hexValue = parts[1].replace("0x", "");
            String reg = parts[2].replace("REG", "").trim();
            try {
                int value = Integer.parseInt(hexValue, 16);
                if (registers.containsKey(reg)) {
                    registers.put(reg, value & 0xFFFF);
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private void handleOut(String[] parts) {
        if (parts.length >= 2) {
            String reg = parts[1].replace("REG", "").trim();
            if (registers.containsKey(reg)) {
                registers.put("OUT", registers.get(reg));
            }
        }
    }

    private void handleJump(String[] parts) {
        jumpedLastInstruction = false;
        if (parts.length < 2) {
            registers.put("OUT", 0xFFFF);
            return;
        }

        try {
            int target = Integer.parseInt(parts[1]);
            if (target >= 0 && target < historyListView.getItems().size()) {
                currentInstructionIndex = target;
                jumpedLastInstruction = true;
            } else {
                registers.put("OUT", 0xFFFF);
            }
        } catch (NumberFormatException e) {
            registers.put("OUT", 0xFFFF);
        }
    }

    // ===== UI helpery =====
    private void updateRegisterDisplay() {
        regALabel.setText(String.format("0x%04X", registers.get("A")));
        regBLabel.setText(String.format("0x%04X", registers.get("B")));
        regCLabel.setText(String.format("0x%04X", registers.get("C")));
        regDLabel.setText(String.format("0x%04X", registers.get("D")));
        outLabel.setText(String.format("0x%04X", registers.get("OUT")));
    }

    private void highlightCurrentInstruction() {
        if (historyListView == null) return;
        historyListView.getSelectionModel().clearSelection();
        if (currentInstructionIndex < historyListView.getItems().size()) {
            historyListView.getSelectionModel().select(currentInstructionIndex);
            historyListView.scrollTo(currentInstructionIndex);
        }
    }
}
