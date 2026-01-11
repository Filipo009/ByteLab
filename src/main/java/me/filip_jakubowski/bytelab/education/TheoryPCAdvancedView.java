package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TheoryPCAdvancedView extends VBox {
    private int pc = 0;
    private boolean isBinaryMode = false;
    private boolean isHalted = false;
    private final List<InstructionRow> program = new ArrayList<>();

    private final Map<String, Integer> currentRegs = new HashMap<>();
    private final Map<String, Integer> lastRegs = new HashMap<>();

    private final Text regA = new Text();
    private final Text regB = new Text();
    private final Text regC = new Text();
    private final Text reg0 = new Text("REG0: 0000");

    private final Text zeroFlagText = new Text("0");
    private final Circle zeroFlagLed = new Circle(6, Color.web("#3d0000"));
    private final Text pcDisplay = new Text("PC: 0000");
    private final Text statusText = new Text("System gotowy.");

    public TheoryPCAdvancedView() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(20));
        setMaxWidth(700);
        setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #444; -fx-border-radius: 10;");

        resetRegisters();
        setupProgram();

        HBox controls = new HBox(15, createControlButtons());
        controls.setAlignment(Pos.CENTER);

        HBox cpuPanel = createCPUPanel();

        VBox programBox = new VBox(2);
        programBox.setAlignment(Pos.CENTER);
        program.forEach(row -> programBox.getChildren().add(row.view));

        Text title = new Text("Przykładowy program z JZ:");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        statusText.setFill(Color.web("#3498db"));
        statusText.setFont(Font.font("Consolas", 13));

        getChildren().addAll(title, cpuPanel, programBox, controls, statusText);
        updateUI();
    }

    private void resetRegisters() {
        currentRegs.put("REGA", 0);
        currentRegs.put("REGB", 0);
        currentRegs.put("REGC", 0);
        lastRegs.putAll(currentRegs);
    }

    private void setupProgram() {
        program.add(new InstructionRow("0000", "IN 1 REGA",     "1010 0001"));
        program.add(new InstructionRow("0001", "IN 2 REGC",     "1010 0011"));
        program.add(new InstructionRow("0010", "ADD --- REGB",  "1100 0110"));
        program.add(new InstructionRow("0011", "XOR REGB REGC", "1001 0110"));
        program.add(new InstructionRow("0100", "JZ 0x08",       "1101 1000"));
        program.add(new InstructionRow("0101", "ADD --- REGB",  "1100 0110"));
        program.add(new InstructionRow("0110", "XOR REGB REGC", "1001 0110")); // Dodany XOR
        program.add(new InstructionRow("0111", "MOV REG0 REGA", "1011 0001"));
        program.add(new InstructionRow("1000", "MOV REG0 REGB", "1011 0010"));
        program.add(new InstructionRow("1001", "MOV REG0 REGC", "1011 0011"));
    }

    private void step() {
        if (isHalted) return;
        lastRegs.putAll(currentRegs);

        switch (pc) {
            case 0 -> {
                currentRegs.put("REGA", 1);
                statusText.setText("Krok 0: REGA = 1.");
            }
            case 1 -> {
                currentRegs.put("REGC", 2);
                statusText.setText("Krok 1: REGC = 2.");
            }
            case 2 -> {
                currentRegs.put("REGB", 1); // Poprawione: teraz 1 zamiast 3
                setZeroFlag(false);
                statusText.setText("Krok 2: ADD zapisał 1 do REGB. Z=0.");
            }
            case 3 -> {
                setZeroFlag(false);
                statusText.setText("Krok 3: XOR REGB(1), REGC(2) = 3. Z=0.");
            }
            case 4 -> {
                if (zeroFlagText.getText().equals("1")) {
                    statusText.setText("JZ: Skok do 0x09.");
                    pc = 9; updateUI(); return;
                }
                statusText.setText("JZ: Brak skoku (Z=0).");
            }
            case 5 -> {
                currentRegs.put("REGB", 2);
                setZeroFlag(false);
                statusText.setText("Krok 5: ADD zapisał 2 do REGB.");
            }
            case 6 -> {
                // XOR REGB(2) REGC(2) = 0
                setZeroFlag(true);
                statusText.setText("Krok 6: XOR REGB(2), REGC(2) = 0. Flaga Z=1!");
            }
            case 7 -> {
                currentRegs.put("REGA", 0);
                setZeroFlag(true);
                statusText.setText("Krok 7: Wyzerowano REGA.");
            }
            case 8 -> {
                currentRegs.put("REGB", 0);
                setZeroFlag(true);
                statusText.setText("Krok 8: Wyzerowano REGB.");
            }
            case 9 -> {
                currentRegs.put("REGC", 0);
                setZeroFlag(true);
                statusText.setText("Krok 9: Wyzerowano REGC. Restart pętli.");
                pc = 0; updateUI(); return;
            }
        }

        pc++;
        updateUI();
    }

    private void updateUI() {
        pcDisplay.setText("PC: " + (pc < program.size() ? program.get(pc).addr : "0000"));

        updateRegText(regA, "REGA", currentRegs.get("REGA"));
        updateRegText(regB, "REGB", currentRegs.get("REGB"));
        updateRegText(regC, "REGC", currentRegs.get("REGC"));

        reg0.setFill(Color.WHITE);

        for (int i = 0; i < program.size(); i++) {
            program.get(i).update(i == pc, isBinaryMode);
        }
    }

    private void updateRegText(Text textNode, String key, int val) {
        textNode.setText(String.format("%s: %04d", key, val));
        if (!currentRegs.get(key).equals(lastRegs.get(key))) {
            textNode.setFill(Color.web("#e74c3c"));
            textNode.setStyle("-fx-font-weight: bold;");
        } else {
            textNode.setFill(Color.LIGHTGRAY);
            textNode.setStyle("-fx-font-weight: normal;");
        }
    }

    private void setZeroFlag(boolean active) {
        zeroFlagText.setText(active ? "1" : "0");
        zeroFlagLed.setFill(active ? Color.web("#2ecc71") : Color.web("#3d0000"));
    }

    private void reset() {
        pc = 0;
        resetRegisters();
        setZeroFlag(false);
        statusText.setText("Zresetowano procesor.");
        updateUI();
    }

    private HBox createCPUPanel() {
        HBox panel = new HBox(30);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #222; -fx-border-color: #3498db; -fx-border-radius: 5;");

        VBox pcBox = new VBox(2);
        pcBox.setAlignment(Pos.CENTER);
        Text pcLabel = new Text("PROGRAM COUNTER");
        pcLabel.setFill(Color.GRAY);
        pcLabel.setFont(Font.font(10));
        pcDisplay.setFill(Color.web("#e67e22"));
        pcDisplay.setFont(Font.font("Consolas", FontWeight.BOLD, 22));
        pcBox.getChildren().addAll(pcLabel, pcDisplay);

        HBox flagBox = new HBox(8);
        flagBox.setAlignment(Pos.CENTER);
        Text zLabel = new Text("Z-FLAG:");
        zLabel.setFill(Color.GRAY);
        zeroFlagText.setFill(Color.WHITE);
        flagBox.getChildren().addAll(zLabel, zeroFlagLed, zeroFlagText);

        VBox pcAndFlags = new VBox(10, pcBox, flagBox);
        pcAndFlags.setAlignment(Pos.CENTER);

        VBox regs = new VBox(5, regA, regB, regC, reg0);
        for (Text r : new Text[]{regA, regB, regC, reg0}) {
            r.setFont(Font.font("Consolas", 14));
        }

        panel.getChildren().addAll(pcAndFlags, regs);
        return panel;
    }

    private Button[] createControlButtons() {
        Button btnStep = new Button("▶ KROK");
        Button btnReset = new Button("↺ RESET");
        Button btnMode = new Button("BIN/ASM");
        btnStep.setOnAction(e -> step());
        btnReset.setOnAction(e -> reset());
        btnMode.setOnAction(e -> { isBinaryMode = !isBinaryMode; updateUI(); });
        return new Button[]{btnStep, btnReset, btnMode};
    }

    private static class InstructionRow {
        String addr, asm, bin;
        HBox view = new HBox(20);
        Text txtContent = new Text();

        InstructionRow(String addr, String asm, String bin) {
            this.addr = addr; this.asm = asm; this.bin = bin;
            view.setAlignment(Pos.CENTER_LEFT);
            view.setPadding(new Insets(2, 15, 2, 15));
            txtContent.setFont(Font.font("Consolas", 12));
            view.getChildren().addAll(new Text(addr + ":") {{ setFill(Color.GRAY); }}, txtContent);
        }

        void update(boolean active, boolean binary) {
            txtContent.setText(binary ? bin : asm);
            if (active) {
                view.setStyle("-fx-background-color: #3e3e3e; -fx-border-color: #3498db; -fx-border-width: 0 0 0 4;");
                txtContent.setFill(Color.web("#3498db"));
            } else {
                view.setStyle("-fx-background-color: transparent;");
                txtContent.setFill(Color.WHITE);
            }
        }
    }
}