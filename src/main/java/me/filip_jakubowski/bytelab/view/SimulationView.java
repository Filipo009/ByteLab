package me.filip_jakubowski.bytelab.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

public class SimulationView extends VBox {

    private final Map<String, Integer> registers = new HashMap<>();
    private final Map<String, Integer> previousRegisters = new HashMap<>();
    private final Map<String, Label> regLabels = new HashMap<>();
    private final Label outLabel = new Label();
    private final Label pcLabel = new Label();
    private final Label zeroLabel = new Label();

    private final Button runButton = new Button("RUN");
    private final Button stopButton = new Button("STOP");
    private final Button resetButton = new Button("RESET");
    private final Button stepButton = new Button("STEP");
    private final ComboBox<Integer> freqCombo = new ComboBox<>();

    private ListView<String> historyListView;
    private Timeline timeline;
    private boolean jumpedLastInstruction = false;

    private final String COLOR_NORMAL = "#e0e0e0";
    private final String COLOR_CHANGED = "#ff4444";

    public SimulationView() {
        setPadding(new Insets(20));
        setSpacing(25);
        setAlignment(Pos.CENTER);

        initRegisters();
        saveState();

        VBox regBox = new VBox(15);
        regBox.setAlignment(Pos.CENTER);
        regBox.getChildren().addAll(new Label("--- STAN REJESTRÓW ---"), createRegisterGrid());

        getChildren().addAll(regBox, createControlPanel());
        updateDisplay();

        runButton.setOnAction(e -> start());
        stopButton.setOnAction(e -> pause());
        resetButton.setOnAction(e -> reset());
        stepButton.setOnAction(e -> step());

        freqCombo.valueProperty().addListener((obs, old, newVal) -> {
            if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) start();
        });
    }

    private void initRegisters() {
        String[] regs = {"0", "A", "B", "C", "D", "E", "OUT", "PC", "ZERO"};
        for (String r : regs) registers.put(r, 0);
    }

    private void saveState() {
        previousRegisters.clear();
        previousRegisters.putAll(registers);
    }

    private GridPane createRegisterGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(30); grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        String[] regs = {"0", "A", "B", "C", "D", "E"};
        for (int i = 0; i < regs.length; i++) {
            Label lbl = new Label();
            lbl.setStyle("-fx-text-fill: " + COLOR_NORMAL + "; -fx-font-weight: bold; -fx-font-size: 14;");
            regLabels.put(regs[i], lbl);
            grid.addRow(i, new Label("REG " + regs[i] + ":"), lbl);
        }

        grid.addRow(6, new Label("OUT:"), outLabel);
        grid.addRow(7, new Label("PC:"), pcLabel);
        grid.addRow(8, new Label("ZERO:"), zeroLabel);

        // Styl dla specjalnych etykiet
        List.of(outLabel, pcLabel, zeroLabel).forEach(l -> l.setStyle("-fx-text-fill: " + COLOR_NORMAL + "; -fx-font-weight: bold; -fx-font-size: 14;"));

        return grid;
    }

    private VBox createControlPanel() {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);

        freqCombo.getItems().addAll(1, 2, 4, 8, 16, 32, 64);
        freqCombo.setValue(4);

        HBox hzBox = new HBox(10, new Label("Częstotliwość (Hz):"), freqCombo);
        hzBox.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(12, runButton, stopButton, stepButton, resetButton);
        buttons.setAlignment(Pos.CENTER);

        box.getChildren().addAll(hzBox, buttons);
        return box;
    }

    public void bindHistoryList(ListView<String> list) { this.historyListView = list; }

    private void start() {
        if (historyListView == null || historyListView.getItems().isEmpty()) return;
        if (timeline != null) timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.millis(1000.0 / freqCombo.getValue()), e -> executeCycle()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        runButton.setDisable(true);
    }

    private void pause() { if (timeline != null) timeline.pause(); runButton.setDisable(false); }

    private void reset() {
        pause(); initRegisters(); saveState(); updateDisplay(); resetColors();
    }

    private void step() { executeCycle(); }

    private void executeCycle() {
        if (historyListView == null || historyListView.getItems().isEmpty()) return;
        int pc = registers.get("PC");
        if (pc < 0 || pc >= historyListView.getItems().size()) { pause(); return; }

        jumpedLastInstruction = false;
        saveState();
        execute(historyListView.getItems().get(pc));
        if (!jumpedLastInstruction) registers.put("PC", pc + 1);

        updateDisplay();
        highlightChanged();
        historyListView.getSelectionModel().select(registers.get("PC"));
        historyListView.scrollTo(registers.get("PC"));
    }

    private void highlightChanged() {
        regLabels.forEach((name, lbl) -> setLabelColor(lbl, name));
        setLabelColor(outLabel, "OUT");
        setLabelColor(pcLabel, "PC");
        setLabelColor(zeroLabel, "ZERO");
    }

    private void setLabelColor(Label lbl, String regName) {
        boolean changed = !registers.get(regName).equals(previousRegisters.get(regName));
        lbl.setStyle("-fx-text-fill: " + (changed ? COLOR_CHANGED : COLOR_NORMAL) + "; -fx-font-weight: bold; -fx-font-size: 14;");
    }

    private void resetColors() {
        regLabels.values().forEach(l -> l.setStyle("-fx-text-fill: " + COLOR_NORMAL + "; -fx-font-weight: bold; -fx-font-size: 14;"));
        List.of(outLabel, pcLabel, zeroLabel).forEach(l -> l.setStyle("-fx-text-fill: " + COLOR_NORMAL + "; -fx-font-weight: bold; -fx-font-size: 14;"));
    }

    private void updateDisplay() {
        regLabels.forEach((name, lbl) -> lbl.setText(fmt(registers.get(name))));
        outLabel.setText(fmt(registers.get("OUT")));
        pcLabel.setText(fmt(registers.get("PC")));
        zeroLabel.setText(String.valueOf(registers.get("ZERO")));
    }

    private String fmt(int v) { return String.format("0x%04X", v & 0xFFFF); }

    private void execute(String line) {
        if (!line.contains(":")) return;
        String raw = line.substring(line.indexOf(":") + 1).trim();
        String[] parts = raw.split("[\\s\\->,]+");
        List<String> tokens = Arrays.stream(parts).filter(s -> !s.isBlank()).collect(Collectors.toList());
        if (tokens.isEmpty()) return;

        String cmd = tokens.get(0).toUpperCase();
        List<String> args = tokens.subList(1, tokens.size());

        try {
            switch (cmd) {
                case "ADD" -> alu(args, Integer::sum);
                case "SUB" -> alu(args, (a, b) -> a - b);
                case "AND" -> alu(args, (a, b) -> a & b);
                case "OR"  -> alu(args, (a, b) -> a | b);
                case "XOR" -> alu(args, (a, b) -> a ^ b);
                case "NOT" -> applyResult(args, (~registers.get("A")) & 0xFFFF);
                case "MOV" -> { List<String> rs = findRegs(args); if (rs.size() >= 2) registers.put(rs.get(1), registers.get(rs.get(0))); }
                case "IN" -> {
                    int val = 0; String target = null;
                    for (int i = 0; i < args.size(); i++) {
                        String a = args.get(i).toUpperCase();
                        if (a.matches("(?i)^(0x)?[0-9A-F]+$")) val = Integer.parseInt(a.replaceFirst("(?i)0x", ""), 16);
                        else if (registers.containsKey(a)) target = a;
                        else if (a.equals("REG") && i+1 < args.size()) { target = args.get(i+1).toUpperCase(); i++; }
                    }
                    if (target != null && !target.equals("0")) registers.put(target, val & 0xFFFF);
                }
                case "OUT" -> { List<String> rs = findRegs(args); if (!rs.isEmpty()) registers.put("OUT", registers.get(rs.get(0))); }
                case "JUMP" -> { jumpedLastInstruction = true; registers.put("PC", getJumpVal(args)); }
                case "JZ" -> { if (registers.get("ZERO") == 1) { jumpedLastInstruction = true; registers.put("PC", getJumpVal(args)); } }
            }
        } catch (Exception ignored) {}
    }

    private void alu(List<String> args, java.util.function.BinaryOperator<Integer> op) {
        int res = op.apply(registers.get("A"), registers.get("B")) & 0xFFFF;
        registers.put("ZERO", res == 0 ? 1 : 0);
        applyResult(args, res);
    }

    private void applyResult(List<String> args, int val) {
        List<String> rs = findRegs(args);
        if (!rs.isEmpty()) { String target = rs.get(rs.size() - 1); if (!target.equals("0")) registers.put(target, val); }
    }

    private List<String> findRegs(List<String> args) {
        List<String> found = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            String a = args.get(i).toUpperCase();
            if (a.equals("REG") && i + 1 < args.size()) { found.add(args.get(i+1).toUpperCase()); i++; }
            else if (registers.containsKey(a)) found.add(a);
        }
        return found;
    }

    private int getJumpVal(List<String> args) {
        for (String a : args) if (a.matches("(?i)^(0x)?[0-9A-F]+$")) return Integer.parseInt(a.replaceFirst("(?i)0x", ""), 16);
        return registers.get("PC");
    }
}