package me.filip_jakubowski.bytelab.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SimulationView extends VBox {

    private final Map<String, Integer> registers = new HashMap<>();
    private final Label regALabel = new Label();
    private final Label regBLabel = new Label();
    private final Label regCLabel = new Label();
    private final Label regDLabel = new Label();
    private final Label outLabel = new Label();
    private final Label zeroFlagLabel = new Label();

    private final Button runButton = new Button("RUN");
    private final Button stopButton = new Button("STOP");
    private final Button resetButton = new Button("RESET");
    private final Button stepButton = new Button("STEP");
    private final ComboBox<Integer> freqCombo = new ComboBox<>();

    private ListView<String> historyListView;
    private Timeline timeline;
    private int currentInstructionIndex = 0;
    private boolean jumpedLastInstruction = false;

    public SimulationView() {
        setPadding(new Insets(10));
        setSpacing(10);
        setAlignment(Pos.TOP_CENTER);

        initRegisters();
        getChildren().addAll(new Label("Symulacja rejestrów"), createRegisterGrid(), createControlPanel());
        updateDisplay();

        runButton.setOnAction(e -> start());
        stopButton.setOnAction(e -> pause());
        resetButton.setOnAction(e -> reset());
        stepButton.setOnAction(e -> step());
    }

    private void initRegisters() {
        registers.put("A", 0);
        registers.put("B", 0);
        registers.put("C", 0);
        registers.put("D", 0);
        registers.put("OUT", 0);
        registers.put("ZF", 0); // flaga zero
    }

    private GridPane createRegisterGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.addRow(0, new Label("REG A:"), regALabel);
        grid.addRow(1, new Label("REG B:"), regBLabel);
        grid.addRow(2, new Label("REG C:"), regCLabel);
        grid.addRow(3, new Label("REG D:"), regDLabel);
        grid.addRow(4, new Label("OUT:"), outLabel);
        grid.addRow(5, new Label("Flaga Zero:"), zeroFlagLabel);

        return grid;
    }

    private VBox createControlPanel() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);

        HBox freqBox = new HBox(10, new Label("Taktowanie [Hz]:"), freqCombo);
        freqBox.setAlignment(Pos.CENTER);
        freqCombo.getItems().addAll(1, 2, 4, 8, 16, 32, 64);
        freqCombo.setValue(4);

        HBox buttons = new HBox(15, runButton, stopButton, stepButton, resetButton);
        buttons.setAlignment(Pos.CENTER);

        style(runButton, "#4CAF50", "white");
        style(stopButton, "#f44336", "white");
        style(stepButton, "#FFC107", "black");
        style(resetButton, "#2196F3", "white");

        box.getChildren().addAll(freqBox, buttons);
        return box;
    }

    private void style(Button b, String bg, String fg) {
        b.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s;", bg, fg));
    }

    public void bindHistoryList(ListView<String> list) {
        this.historyListView = list;
    }

    // ======== Sterowanie ========
    private void start() {
        if (historyListView == null || historyListView.getItems().isEmpty()) return;
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) return;

        double period = 1000.0 / freqCombo.getValue();
        timeline = new Timeline(new KeyFrame(Duration.millis(period), e -> next()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        runButton.setDisable(true);
        stopButton.setDisable(false);
    }

    private void pause() {
        if (timeline != null) timeline.pause();
        runButton.setDisable(false);
        stopButton.setDisable(true);
    }

    private void reset() {
        if (timeline != null) timeline.stop();
        currentInstructionIndex = 0;
        initRegisters();
        updateDisplay();
        highlightCurrent();
    }

    private void step() {
        if (historyListView == null || historyListView.getItems().isEmpty()) return;
        if (currentInstructionIndex >= historyListView.getItems().size()) return;

        jumpedLastInstruction = false;
        execute(historyListView.getItems().get(currentInstructionIndex));
        highlightCurrent();

        if (!jumpedLastInstruction) currentInstructionIndex++;
    }

    private void next() {
        if (currentInstructionIndex >= historyListView.getItems().size()) {
            if (timeline != null) timeline.stop();
            return;
        }

        jumpedLastInstruction = false;
        execute(historyListView.getItems().get(currentInstructionIndex));
        highlightCurrent();

        if (!jumpedLastInstruction) currentInstructionIndex++;
    }

    // ======== Logika instrukcji ========
    private static final String[] COMMANDS = {"ADD", "SUB", "AND", "OR", "NOT", "MOV", "IN", "OUT", "JUMP", "NOP"};

    private void execute(String line) {
        if (line == null || line.isBlank()) return;
        String[] p = line.trim().split("\\s+");

        int cmdIdx = -1;
        for (int i = 0; i < p.length; i++) {
            String up = p[i].replace(":", "").toUpperCase();
            for (String c : COMMANDS) {
                if (up.equals(c)) {
                    cmdIdx = i;
                    break;
                }
            }
            if (cmdIdx != -1) break;
        }
        if (cmdIdx == -1) return;

        String cmd = p[cmdIdx].toUpperCase();
        String[] args = (cmdIdx + 1 < p.length) ? Arrays.copyOfRange(p, cmdIdx + 1, p.length) : new String[0];

        switch (cmd) {
            case "ADD" -> registers.put("OUT", registers.get("A") + registers.get("B"));
            case "SUB" -> registers.put("OUT", registers.get("A") - registers.get("B"));
            case "AND" -> registers.put("OUT", registers.get("A") & registers.get("B"));
            case "OR"  -> registers.put("OUT", registers.get("A") | registers.get("B"));
            case "NOT" -> registers.put("OUT", ~registers.get("A") & 0xFFFF);
            case "MOV" -> mov(args);
            case "IN"  -> in(args);
            case "OUT" -> out(args);
            case "JUMP"-> jump(args);
            case "NOP" -> {}
        }

        updateZeroFlag();
        updateDisplay();
    }

    private void mov(String[] args) {
        if (args.length < 2) return;
        String from = findRegisterToken(args, 0);
        String to = findRegisterToken(args, 1);
        if (from == null || to == null) return;
        String f = from.replace("REG", "").trim();
        String t = to.replace("REG", "").trim();
        if (registers.containsKey(f) && registers.containsKey(t)) {
            registers.put(t, registers.get(f));
        }
    }

    private void in(String[] args) {
        if (args.length < 2) return;

        String hexToken = null;
        String regToken = null;

        for (String a : args) {
            if (a.matches("(?i)(0x)?[0-9A-F]+")) hexToken = a;
            if (a.toUpperCase().startsWith("REG") || a.matches("^[ABCD]$")) regToken = a;
        }

        if (hexToken == null || regToken == null) return;

        try {
            int val = Integer.parseInt(hexToken.replaceFirst("(?i)0x", ""), 16) & 0xFFFF;
            String reg = regToken.replace("REG", "").trim();
            if (registers.containsKey(reg)) registers.put(reg, val);
        } catch (NumberFormatException ignored) {}
    }

    private void out(String[] args) {
        if (args.length < 1) return;
        String regToken = findRegisterToken(args, 0);
        if (regToken == null) return;
        String reg = regToken.replace("REG", "").trim();
        if (registers.containsKey(reg)) registers.put("OUT", registers.get(reg));
    }

    private void jump(String[] args) {
        jumpedLastInstruction = false;
        if (args.length < 1) {
            registers.put("OUT", 0xFFFF);
            return;
        }
        String candidate = null;
        for (String a : args) {
            String cleaned = a.replaceAll("[^0-9A-Fa-f]", "");
            if (!cleaned.isEmpty()) {
                candidate = cleaned;
                break;
            }
        }
        if (candidate == null) {
            registers.put("OUT", 0xFFFF);
            return;
        }
        try {
            int target = Integer.parseInt(candidate, 16);
            registers.put("OUT", target & 0xFFFF);
            if (historyListView != null && target >= 0 && target < historyListView.getItems().size()) {
                currentInstructionIndex = target;
                jumpedLastInstruction = true;
            }
        } catch (NumberFormatException ex) {
            registers.put("OUT", 0xFFFF);
        }
    }

    private String findRegisterToken(String[] args, int startIndex) {
        for (int i = startIndex; i < args.length; i++) {
            String t = args[i].toUpperCase();
            if (t.startsWith("REG")) return args[i];
            if (t.matches("^[ABCD]$")) return "REG" + args[i];
            if (t.matches("REG[A-D]")) return args[i];
        }
        return null;
    }

    private void updateZeroFlag() {
        int outVal = registers.getOrDefault("OUT", 0);
        registers.put("ZF", (outVal == 0) ? 1 : 0);
    }

    private void updateDisplay() {
        regALabel.setText(fmt(registers.get("A")));
        regBLabel.setText(fmt(registers.get("B")));
        regCLabel.setText(fmt(registers.get("C")));
        regDLabel.setText(fmt(registers.get("D")));
        outLabel.setText(fmt(registers.get("OUT")));
        zeroFlagLabel.setText(String.valueOf(registers.get("ZF")));
    }

    private String fmt(int val) {
        return String.format("0x%04X", val & 0xFFFF);
    }

    private void highlightCurrent() {
        if (historyListView == null) return;
        historyListView.getSelectionModel().clearSelection();
        if (currentInstructionIndex < historyListView.getItems().size()) {
            historyListView.getSelectionModel().select(currentInstructionIndex);
            historyListView.scrollTo(currentInstructionIndex);
        }
    }
}
