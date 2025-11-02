package me.filip_jakubowski.bytelab.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Okno symulacji rejestrów + kontroler RUN/STOP.
 */
public class SimulationView extends VBox {

    private final Label regA = new Label("REG A: 0x0");
    private final Label regB = new Label("REG B: 0x0");
    private final Label regC = new Label("REG C: 0x0");
    private final Label regD = new Label("REG D: 0x0");
    private final Label out  = new Label("OUT   : 0x0");

    private final Button runButton  = new Button("RUN");
    private final Button stopButton = new Button("STOP");
    private final ComboBox<Integer> frequencyBox = new ComboBox<>();

    private final SimpleStringProperty runningStatus = new SimpleStringProperty("STOPPED");

    private Timeline timeline;
    private int currentIndex = 0;

    // Referencja do listy historii (z CommandSearchView)
    private final ListView<String> historyList;

    public SimulationView(ListView<String> historyList) {
        this.historyList = historyList;

        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER_LEFT);

        regA.setFont(Font.font("Consolas", 16));
        regB.setFont(Font.font("Consolas", 16));
        regC.setFont(Font.font("Consolas", 16));
        regD.setFont(Font.font("Consolas", 16));
        out.setFont(Font.font("Consolas", 16));

        runButton.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white; -fx-font-weight: bold;");
        stopButton.setStyle("-fx-background-color: #DC143C; -fx-text-fill: white; -fx-font-weight: bold;");

        frequencyBox.getItems().addAll(1, 2, 4, 8, 16, 32, 64);
        frequencyBox.setValue(4);

        HBox controlBox = new HBox(10, runButton, stopButton, new Label("Hz:"), frequencyBox);
        controlBox.setAlignment(Pos.CENTER_LEFT);

        VBox registerBox = new VBox(5, regA, regB, regC, regD, out);
        registerBox.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-padding: 8;");

        getChildren().addAll(new Label("Symulator rejestrów:"), registerBox, controlBox);

        setupListeners();
        resetRegisters();
    }

    private void setupListeners() {
        runButton.setOnAction(e -> startSimulation());
        stopButton.setOnAction(e -> stopSimulation());
    }

    private void startSimulation() {
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) return;
        if (historyList.getItems().isEmpty()) return;

        resetRegisters();
        currentIndex = 0;
        runningStatus.set("RUNNING");

        double periodMs = 1000.0 / frequencyBox.getValue();

        timeline = new Timeline(new KeyFrame(Duration.millis(periodMs), e -> stepSimulation()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void stopSimulation() {
        if (timeline != null) {
            timeline.stop();
        }
        runningStatus.set("STOPPED");
        clearHighlight();
    }

    private void stepSimulation() {
        if (currentIndex >= historyList.getItems().size()) {
            stopSimulation();
            return;
        }

        clearHighlight();

        // Podświetlenie bieżącego elementu historii
        historyList.getSelectionModel().select(currentIndex);
        historyList.scrollTo(currentIndex);
        historyList.lookupAll(".list-cell").forEach(cell -> cell.setStyle("")); // wyczyść style

        // Na niebiesko aktywny wiersz
        historyList.lookupAll(".list-cell").forEach(node -> {
            if (node instanceof Labeled labeled) {
                if (labeled.getText() != null && labeled.getText().equals(historyList.getItems().get(currentIndex))) {
                    labeled.setStyle("-fx-background-color: lightblue;");
                }
            }
        });

        // Wykonaj polecenie — tu prosty interpreter
        String cmd = historyList.getItems().get(currentIndex);
        executeCommand(cmd);

        currentIndex++;
    }

    private void executeCommand(String cmd) {
        // Przykład interpretacji
        // Format: "0: IN 0xFF -> REG A" albo "1: ADD --- -> REG B"
        try {
            String[] parts = cmd.split(": ", 2);
            if (parts.length < 2) return;

            String[] tokens = parts[1].split(" ");
            String instr = tokens[0];

            switch (instr) {
                case "IN" -> {
                    String hex = tokens[1];
                    String reg = tokens[tokens.length - 1];
                    setRegister(reg, hex);
                }
                case "MOV" -> {
                    String src = tokens[1];
                    String dst = tokens[tokens.length - 1];
                    String val = getRegisterValue(src);
                    setRegister(dst, val);
                }
                case "ADD" -> performBinaryOp("ADD");
                case "SUB" -> performBinaryOp("SUB");
                case "AND" -> performBinaryOp("AND");
                case "OR"  -> performBinaryOp("OR");
                case "NOT" -> performUnaryOp("NOT");
                case "OUT" -> {
                    String src = tokens[1];
                    out.setText("OUT   : " + getRegisterValue(src));
                }
                case "JUMP" -> {
                    // tylko wizualny efekt – w realnym CPU zmiana PC
                    out.setText("OUT   : Skok -> " + tokens[tokens.length - 1]);
                }
                case "NOP" -> {
                    // nic nie robi
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd interpretacji: " + cmd);
        }
    }

    private void performBinaryOp(String op) {
        int a = parseHex(getRegisterValue("REG A"));
        int b = parseHex(getRegisterValue("REG B"));
        int result = switch (op) {
            case "ADD" -> a + b;
            case "SUB" -> a - b;
            case "AND" -> a & b;
            case "OR"  -> a | b;
            default -> 0;
        };
        setRegister("REG A", "0x" + Integer.toHexString(result & 0xFFFF).toUpperCase());
    }

    private void performUnaryOp(String op) {
        int a = parseHex(getRegisterValue("REG A"));
        if ("NOT".equals(op)) {
            int res = (~a) & 0xFFFF;
            setRegister("REG A", "0x" + Integer.toHexString(res).toUpperCase());
        }
    }

    private int parseHex(String val) {
        try {
            return Integer.parseInt(val.replace("0x", "").toUpperCase(), 16);
        } catch (Exception e) {
            return 0;
        }
    }

    private String getRegisterValue(String reg) {
        return switch (reg) {
            case "REG A" -> regA.getText().split(": ")[1];
            case "REG B" -> regB.getText().split(": ")[1];
            case "REG C" -> regC.getText().split(": ")[1];
            case "REG D" -> regD.getText().split(": ")[1];
            default -> "0x0";
        };
    }

    private void setRegister(String reg, String value) {
        value = value.toUpperCase();
        switch (reg) {
            case "REG A" -> regA.setText("REG A: " + value);
            case "REG B" -> regB.setText("REG B: " + value);
            case "REG C" -> regC.setText("REG C: " + value);
            case "REG D" -> regD.setText("REG D: " + value);
            default -> out.setText("OUT   : " + value);
        }
    }

    private void clearHighlight() {
        historyList.lookupAll(".list-cell").forEach(node -> node.setStyle(""));
    }

    private void resetRegisters() {
        regA.setText("REG A: 0x0");
        regB.setText("REG B: 0x0");
        regC.setText("REG C: 0x0");
        regD.setText("REG D: 0x0");
        out.setText("OUT   : 0x0");
    }
}
