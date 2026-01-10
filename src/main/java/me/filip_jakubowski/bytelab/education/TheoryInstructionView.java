package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TheoryInstructionView extends StackPane { // Zmienione na StackPane dla łatwiejszego centrowania
    private final int[] busA = new int[8], busB = new int[8], regA = new int[8], regB = new int[8];
    private final int[] acc = new int[8], regC = new int[8], regOut = new int[8];

    private final Text[] textsA = new Text[8], textsB = new Text[8], textsRegA = new Text[8], textsRegB = new Text[8];
    private final Text[] textsAcc = new Text[8], textsRegC = new Text[8], textsOut = new Text[8];

    private final Line[] linesInA = new Line[8], linesInB = new Line[8], linesToAluA = new Line[8], linesToAluB = new Line[8];
    private final Line[] linesToAcc = new Line[8], linesToC = new Line[8], linesToOut = new Line[8];

    private final Color COLOR_ON = Color.web("#2ecc71"), COLOR_OFF = Color.web("#441111");
    private final String COLOR_REGS = "#9b59b6";

    private final double BIT_W = 30.0, BIT_H = 20.0, SPACING = 4.0; // Delikatnie zmniejszone wymiary
    private final double BIT_STEP_V = BIT_W + SPACING;
    private final double BIT_STEP_H = BIT_H + SPACING;

    public TheoryInstructionView() {
        // Główny kontener trzymający wszystko w kupie
        HBox mainLayout = new HBox(30); // Stały odstęp 30px zamiast dynamicznego Region
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(25));
        mainLayout.setMaxWidth(Region.USE_PREF_SIZE); // Nie rozciągaj się bardziej niż potrzeba
        mainLayout.setMaxHeight(Region.USE_PREF_SIZE);
        mainLayout.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #444; -fx-border-radius: 15; -fx-background-radius: 15;");

        // --- PANEL STEROWANIA ---
        VBox leftControls = new VBox(6);
        leftControls.setAlignment(Pos.CENTER);
        String[] ops = {
                "IN A", "IN B", "---",
                "ADD", "SUB", "AND", "NAND", "OR", "NOR", "XOR", "NOT",
                "---", "MOV ACC->C", "OUT C"
        };

        for (String op : ops) {
            if (op.equals("---")) {
                Region s = new Region(); s.setPrefHeight(5);
                leftControls.getChildren().add(s);
                continue;
            }
            Button b = new Button(op); b.setMinWidth(100);
            b.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: #ecf0f1; -fx-font-size: 11px; -fx-font-weight: bold; -fx-cursor: hand;");
            b.setOnAction(e -> execute(op));
            leftControls.getChildren().add(b);
        }

        // --- KOLUMNA ALU ---
        VBox aluColumn = new VBox(0); aluColumn.setAlignment(Pos.CENTER);

        VBox inA = createInputRow("INPUT A", busA, textsA, "#3498db", true, true);
        Pane busInA_Lines = createVerticalLines(linesInA, 30);
        VBox regABox = createInputRow("REG A", regA, textsRegA, COLOR_REGS, false, false);
        regABox.setStyle("-fx-background-color: #222; -fx-border-color: " + COLOR_REGS + "; -fx-border-radius: 5; -fx-padding: 8;");
        Pane busToAluA_Lines = createVerticalLines(linesToAluA, 30);

        StackPane aluSquare = new StackPane();
        double aluSize = (BIT_STEP_V * 8) - SPACING;
        Rectangle square = new Rectangle(aluSize, aluSize, Color.web("#252525"));
        square.setStroke(Color.DARKGRAY); square.setStrokeWidth(2);
        Text aluT = new Text("8-BIT ALU"); aluT.setFill(Color.WHITE); aluT.setFont(Font.font("Consolas", 14));
        aluSquare.getChildren().addAll(square, aluT);

        Pane busToAluB_Lines = createVerticalLines(linesToAluB, 30);
        VBox regBBox = createInputRow("REG B", regB, textsRegB, COLOR_REGS, false, false);
        regBBox.setStyle("-fx-background-color: #222; -fx-border-color: " + COLOR_REGS + "; -fx-border-radius: 5; -fx-padding: 8;");
        Pane busInB_Lines = createVerticalLines(linesInB, 30);
        VBox inB = createInputRow("INPUT B", busB, textsB, "#f1c40f", true, false);

        aluColumn.getChildren().addAll(inA, busInA_Lines, regABox, busToAluA_Lines, aluSquare, busToAluB_Lines, regBBox, busInB_Lines, inB);

        // --- DATA FLOW ---
        HBox seg1 = createDataSegment(linesToAcc, "ACC", textsAcc, "#3498db");
        HBox seg2 = createDataSegment(linesToC, "REG C", textsRegC, COLOR_REGS);
        HBox seg3 = createDataSegment(linesToOut, "OUT", textsOut, "#e74c3c");

        HBox dataFlow = new HBox(0, aluColumn, seg1, seg2, seg3);
        dataFlow.setAlignment(Pos.CENTER);

        mainLayout.getChildren().addAll(leftControls, dataFlow);

        // Wycentrowanie całej paczki w widoku
        this.getChildren().add(mainLayout);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(20));

        applyTextStyle(this);
        update();
    }

    private HBox createDataSegment(Line[] lines, String label, Text[] texts, String color) {
        VBox cells = new VBox(SPACING);
        cells.setPadding(new Insets(8));
        cells.setAlignment(Pos.CENTER);
        cells.setStyle("-fx-background-color: #222; -fx-border-color: " + color + "; -fx-border-radius: 5;");
        for (int i = 0; i < 8; i++) {
            texts[i] = new Text("0"); texts[i].setFont(Font.font("Consolas", 14));
            cells.getChildren().add(new StackPane(new Rectangle(BIT_W, BIT_H, Color.web("#111")), texts[i]));
        }

        double boxHeight = (8 * BIT_H) + (7 * SPACING) + 16;
        Pane busPane = new Pane();
        busPane.setPrefSize(40, boxHeight);
        for (int i = 0; i < 8; i++) {
            double y = 8 + (BIT_H / 2.0) + (i * BIT_STEP_H);
            lines[i] = new Line(0, y, 40, y);
            lines[i].setStrokeWidth(2.5); lines[i].setStroke(COLOR_OFF);
            busPane.getChildren().add(lines[i]);
        }

        VBox segmentWrapper = new VBox(4, new HBox(0, busPane, cells), new Text(label));
        ((Text)segmentWrapper.getChildren().get(1)).setFill(Color.web(color));
        ((Text)segmentWrapper.getChildren().get(1)).setFont(Font.font("Consolas", 9));
        segmentWrapper.setAlignment(Pos.CENTER);

        return new HBox(new StackPane(segmentWrapper));
    }

    private VBox createInputRow(String label, int[] bits, Text[] texts, String color, boolean inter, boolean labelTop) {
        HBox hb = new HBox(SPACING); hb.setAlignment(Pos.CENTER);
        for (int i = 0; i < 8; i++) {
            texts[i] = new Text("0"); texts[i].setFont(Font.font("Consolas", 12)); texts[i].setFill(Color.WHITE);
            StackPane sp = new StackPane(new Rectangle(BIT_W, BIT_H, Color.web("#2d2d2d")), texts[i]);
            if (inter) {
                final int idx = i;
                sp.setOnMouseClicked(e -> { bits[idx] = 1 - bits[idx]; update(); });
                sp.setStyle("-fx-cursor: hand;");
            }
            hb.getChildren().add(sp);
        }
        Text t = new Text(label); t.setFill(Color.web(color)); t.setFont(Font.font("Consolas", 9));
        return labelTop ? new VBox(3, t, hb) : new VBox(3, hb, t);
    }

    private Pane createVerticalLines(Line[] lines, double h) {
        Pane p = new Pane(); p.setPrefHeight(h);
        double w = (8 * BIT_W) + (7 * SPACING);
        p.setMaxWidth(w);
        for (int i = 0; i < 8; i++) {
            double x = (BIT_W / 2.0) + (i * BIT_STEP_V);
            lines[i] = new Line(x, 0, x, h);
            lines[i].setStrokeWidth(2.5); lines[i].setStroke(COLOR_OFF);
            p.getChildren().add(lines[i]);
        }
        return p;
    }

    private void execute(String op) {
        int a = bitsToInt(regA), b = bitsToInt(regB);
        switch (op) {
            case "IN A" -> System.arraycopy(busA, 0, regA, 0, 8);
            case "IN B" -> System.arraycopy(busB, 0, regB, 0, 8);
            case "ADD" -> intToBits((a + b) & 0xFF, acc);
            case "SUB" -> intToBits((a - b) & 0xFF, acc);
            case "AND" -> intToBits((a & b) & 0xFF, acc);
            case "NAND" -> intToBits(~(a & b) & 0xFF, acc);
            case "OR"  -> intToBits((a | b) & 0xFF, acc);
            case "NOR" -> intToBits(~(a | b) & 0xFF, acc);
            case "XOR" -> intToBits((a ^ b) & 0xFF, acc);
            case "NOT" -> intToBits((~a) & 0xFF, acc);
            case "MOV ACC->C" -> System.arraycopy(acc, 0, regC, 0, 8);
            case "OUT C" -> System.arraycopy(regC, 0, regOut, 0, 8);
        }
        update();
    }

    private void update() {
        for (int i = 0; i < 8; i++) {
            textsA[i].setText(String.valueOf(busA[i]));
            textsB[i].setText(String.valueOf(busB[i]));
            textsRegA[i].setText(String.valueOf(regA[i]));
            textsRegB[i].setText(String.valueOf(regB[i]));
            textsAcc[i].setText(String.valueOf(acc[i]));
            textsRegC[i].setText(String.valueOf(regC[i]));
            textsOut[i].setText(String.valueOf(regOut[i]));

            textsRegA[i].setFill(regA[i] == 1 ? COLOR_ON : Color.GRAY);
            textsRegB[i].setFill(regB[i] == 1 ? COLOR_ON : Color.GRAY);
            textsAcc[i].setFill(acc[i] == 1 ? COLOR_ON : Color.GRAY);
            textsRegC[i].setFill(regC[i] == 1 ? COLOR_ON : Color.GRAY);
            textsOut[i].setFill(regOut[i] == 1 ? COLOR_ON : Color.GRAY);

            if(linesInA[i]!=null) linesInA[i].setStroke(busA[i] == 1 ? COLOR_ON : COLOR_OFF);
            if(linesInB[i]!=null) linesInB[i].setStroke(busB[i] == 1 ? COLOR_ON : COLOR_OFF);
            if(linesToAluA[i]!=null) linesToAluA[i].setStroke(regA[i] == 1 ? COLOR_ON : COLOR_OFF);
            if(linesToAluB[i]!=null) linesToAluB[i].setStroke(regB[i] == 1 ? COLOR_ON : COLOR_OFF);
            if(linesToAcc[i]!=null) linesToAcc[i].setStroke(acc[i] == 1 ? COLOR_ON : COLOR_OFF);
            if(linesToC[i]!=null) linesToC[i].setStroke(regC[i] == 1 ? COLOR_ON : COLOR_OFF);
            if(linesToOut[i]!=null) linesToOut[i].setStroke(regOut[i] == 1 ? COLOR_ON : COLOR_OFF);
        }
    }

    private int bitsToInt(int[] b) {
        int r = 0; for(int i=0; i<8; i++) if(b[i]==1) r += Math.pow(2, 7-i); return r;
    }
    private void intToBits(int v, int[] b) {
        for(int i=0; i<8; i++) b[i] = (v >> (7-i)) & 1;
    }
    private void applyTextStyle(javafx.scene.Node n) {
        if (n instanceof Text) {
            Text t = (Text) n;
            if (t.getFont().getSize() > 10) t.setFill(Color.web("#888"));
        }
        if (n instanceof Parent) for (javafx.scene.Node c : ((Parent)n).getChildrenUnmodifiable()) applyTextStyle(c);
    }
}