package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TheoryPCView extends VBox {
    private int pc = 0;
    private boolean isBinaryMode = false;
    private boolean isHalted = false;
    private final List<InstructionRow> program = new ArrayList<>();
    private final Text regA = new Text("REGA: 00000000");
    private final Text regB = new Text("REGB: 00000000");
    private final Text regC = new Text("REGC: 00000000");
    private final Text pcDisplay = new Text("PC: 0000"); // 4 bity
    private final Text statusText = new Text("Gotowy. Kliknij 'KROK', aby wykonać pierwszą instrukcję.");

    public TheoryPCView() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(20));
        setMaxWidth(650);
        setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #444; -fx-border-radius: 10;");

        setupProgram();

        // Panel sterowania
        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER);
        Button btnStep = new Button("▶ KROK (STEP)");
        Button btnReset = new Button("↺ RESET");
        Button btnMode = new Button("BIN / ASM");

        btnStep.setOnAction(e -> step());
        btnReset.setOnAction(e -> reset());
        btnMode.setOnAction(e -> {
            isBinaryMode = !isBinaryMode;
            updateUI();
        });

        controls.getChildren().addAll(btnStep, btnReset, btnMode);

        // Panel procesora
        HBox cpuPanel = new HBox(30);
        cpuPanel.setAlignment(Pos.CENTER);
        cpuPanel.setPadding(new Insets(15));
        cpuPanel.setStyle("-fx-background-color: #222; -fx-border-color: #3498db; -fx-border-radius: 5;");

        VBox pcBox = new VBox(5);
        pcBox.setAlignment(Pos.CENTER);
        Text pcLabel = new Text("PROGRAM COUNTER");
        pcLabel.setFill(Color.GRAY);
        pcLabel.setFont(Font.font("Consolas", 10));
        pcDisplay.setFill(Color.web("#e67e22"));
        pcDisplay.setFont(Font.font("Consolas", FontWeight.BOLD, 22));
        pcBox.getChildren().addAll(pcLabel, pcDisplay);

        VBox regs = new VBox(5);
        for (Text r : new Text[]{regA, regB, regC}) {
            r.setFill(Color.LIGHTGRAY);
            r.setFont(Font.font("Consolas", 14));
            regs.getChildren().add(r);
        }

        cpuPanel.getChildren().addAll(pcBox, regs);

        // Lista instrukcji
        VBox programBox = new VBox(2);
        programBox.setAlignment(Pos.CENTER);
        for (InstructionRow row : program) {
            programBox.getChildren().add(row.view);
        }

        statusText.setFill(Color.web("#3498db"));
        statusText.setFont(Font.font("Consolas", 14));

        getChildren().addAll(new Text("SYMULACJA LICZNIKA ROZKAZÓW (4-BIT PC)"), cpuPanel, programBox, controls, statusText);
        updateUI();
    }

    private void setupProgram() {
        // PC 4-bitowy: adresy od 0000 do 1111
        program.add(new InstructionRow("0000", "IN 1, REGA", "1010 0001"));
        program.add(new InstructionRow("0001", "IN 2, REGB", "1010 0010"));
        program.add(new InstructionRow("0010", "ADD REGA, REGB", "1100 0110"));
        program.add(new InstructionRow("0011", "OUT REGC", "1110 0011"));
        program.add(new InstructionRow("0100", "HLT",      "0000 0000")); // Zatrzymanie procesora
    }

    private void step() {
        if (isHalted) return;

        if (pc >= program.size()) {
            isHalted = true;
            return;
        }

        switch (pc) {
            case 0 -> { regA.setText("REGA: 00000001"); statusText.setText("PC=0: Pobrano 'IN 1, REGA'. Wpisano 1 do REGA."); }
            case 1 -> { regB.setText("REGB: 00000010"); statusText.setText("PC=1: Pobrano 'IN 2, REGB'. Wpisano 2 do REGB."); }
            case 2 -> { regC.setText("REGC: 00000011"); statusText.setText("PC=2: Pobrano 'ADD'. ALU oblicza sumę w REGC."); }
            case 3 -> { statusText.setText("PC=3: Pobrano 'OUT'. Wysłano wartość 3 na piny wyjściowe."); }
            case 4 -> {
                isHalted = true;
                statusText.setText("PC=4: Pobrano 'HLT'. Procesor zatrzymany (Zegar stoi).");
                statusText.setFill(Color.web("#e74c3c"));
            }
        }

        if (!isHalted) pc++;
        updateUI();
    }

    private void reset() {
        pc = 0;
        isHalted = false;
        regA.setText("REGA: 00000000");
        regB.setText("REGB: 00000000");
        regC.setText("REGC: 00000000");
        statusText.setText("Zresetowano procesor.");
        statusText.setFill(Color.web("#3498db"));
        updateUI();
    }

    private void updateUI() {
        // Wyświetlanie adresu PC w formacie 4-bitowym
        if (pc < program.size()) {
            pcDisplay.setText("PC: " + program.get(pc).addr);
        } else {
            pcDisplay.setText("PC: HALT");
        }

        for (int i = 0; i < program.size(); i++) {
            program.get(i).update(i == pc && !isHalted, isBinaryMode);
        }
    }

    private static class InstructionRow {
        String addr, asm, bin;
        HBox view = new HBox(20);
        Text txtAddr = new Text();
        Text txtContent = new Text();

        InstructionRow(String addr, String asm, String bin) {
            this.addr = addr; this.asm = asm; this.bin = bin;
            view.setAlignment(Pos.CENTER_LEFT);
            view.setPadding(new Insets(3, 15, 3, 15));
            txtAddr.setFill(Color.GRAY);
            txtContent.setFill(Color.WHITE);
            txtAddr.setFont(Font.font("Consolas", 14));
            txtContent.setFont(Font.font("Consolas", 14));
            view.getChildren().addAll(txtAddr, txtContent);
        }

        void update(boolean active, boolean binary) {
            txtAddr.setText(addr + ":");
            txtContent.setText(binary ? bin : asm);
            if (active) {
                view.setStyle("-fx-background-color: #3e3e3e; -fx-border-color: #e67e22; -fx-border-width: 0 0 0 4;");
                txtContent.setFill(Color.web("#e67e22"));
                txtContent.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
            } else {
                view.setStyle("-fx-background-color: transparent;");
                txtContent.setFill(Color.WHITE);
                txtContent.setFont(Font.font("Consolas", FontWeight.NORMAL, 14));
            }
        }
    }
}