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

import java.util.Arrays;

public class TheoryComplexBusView extends HBox {
    private final int[] busA = new int[8], busB = new int[8], regBits = new int[8];
    private final Text[] textsA = new Text[8], textsB = new Text[8], textsReg = new Text[8];
    private final Line[] linesA = new Line[8], linesB = new Line[8], linesOut = new Line[8];

    private final Color COLOR_ON = Color.web("#2ecc71");
    private final Color COLOR_OFF = Color.web("#441111");

    // Kluczowe wymiary dla idealnej symetrii
    private final double RECT_SIZE = 25.0;
    private final double SPACING = 10.0;
    private final double BIT_SPACING = RECT_SIZE + SPACING; // 35.0

    public TheoryComplexBusView() {
        setSpacing(0);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(40));
        setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #555; -fx-border-radius: 10;");
        setMaxWidth(1000);

        // --- LEWA STRONA: WEJŚCIA I ADDER ---
        VBox leftSection = new VBox(0);
        leftSection.setAlignment(Pos.CENTER);

        // 1. INPUT A
        HBox rowA = createInputRow(busA, textsA, "#3498db");
        VBox topInput = new VBox(5, new Text("INPUT A"), rowA);
        topInput.setAlignment(Pos.CENTER);

        // 2. Linie A -> ADDER (Pionowe)
        Pane linesAPane = createVerticalLines(linesA);

        // 3. ADDER (Kwadrat dopasowany do szerokości szyny)
        StackPane adderSquare = new StackPane();
        double adderSize = (BIT_SPACING * 8);
        Rectangle square = new Rectangle(adderSize, adderSize, Color.web("#333"));
        square.setStroke(Color.WHITE);
        square.setStrokeWidth(2);
        Text adderText = new Text("8-BIT\nADDER\nUNIT");
        adderText.setFill(Color.WHITE);
        adderText.setFont(Font.font("Consolas", 20));
        adderText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        adderSquare.getChildren().addAll(square, adderText);

        // 4. Linie B -> ADDER (Pionowe)
        Pane linesBPane = createVerticalLines(linesB);

        // 5. INPUT B
        HBox rowB = createInputRow(busB, textsB, "#f1c40f");
        VBox bottomInput = new VBox(5, rowB, new Text("INPUT B"));
        bottomInput.setAlignment(Pos.CENTER);

        leftSection.getChildren().addAll(topInput, linesAPane, adderSquare, linesBPane, bottomInput);

        // --- ŚRODEK: MAGISTRALA WYJŚCIOWA (Pozioma) ---
        Pane outputBusPane = new Pane();
        outputBusPane.setPrefWidth(120);
        // Pozycjonowanie linii wyjściowych tak, by wychodziły ze środka boku addera
        double startY = (adderSize / 2.0) - (3.5 * 20);
        for (int i = 0; i < 8; i++) {
            linesOut[i] = new Line(0, 0, 120, 0);
            linesOut[i].setStrokeWidth(3);
            linesOut[i].setStroke(COLOR_OFF);
            // Przesunięcie o 45px w dół (topInput + linesA) + startY + odstęp między bitami
            linesOut[i].setTranslateY(85 + startY + (i * 20));
            outputBusPane.getChildren().add(linesOut[i]);
        }

        // --- PRAWA STRONA: REJESTR PIONOWY ---
        VBox registerSection = new VBox(15);
        registerSection.setAlignment(Pos.CENTER);

        VBox regBox = new VBox(5);
        regBox.setAlignment(Pos.CENTER);
        regBox.setPadding(new Insets(15));
        regBox.setStyle("-fx-background-color: #222; -fx-border-color: #2ecc71; -fx-border-radius: 5;");

        for (int i = 0; i < 8; i++) {
            textsReg[i] = new Text("0");
            textsReg[i].setFont(Font.font("Consolas", 18));
            textsReg[i].setFill(COLOR_OFF);
            StackPane bitBox = new StackPane(new Rectangle(35, 25, Color.web("#111")), textsReg[i]);
            regBox.getChildren().add(bitBox);
        }

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);
        Button loadBtn = new Button("LOAD");
        loadBtn.setStyle("-fx-background-color: #2ecc71; -fx-font-weight: bold; -fx-cursor: hand;");
        loadBtn.setOnAction(e -> { calculateSum(true); update(); });

        Button clearBtn = new Button("CLEAR");
        clearBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> { Arrays.fill(regBits, 0); update(); });

        controls.getChildren().addAll(loadBtn, clearBtn);
        registerSection.getChildren().addAll(new Text("ACCUMULATOR"), regBox, controls);

        getChildren().addAll(leftSection, outputBusPane, registerSection);

        applyTextStyle(this);
        update();
    }

    private HBox createInputRow(int[] bits, Text[] texts, String color) {
        HBox hb = new HBox(SPACING);
        hb.setAlignment(Pos.CENTER);
        for (int i = 0; i < 8; i++) {
            final int idx = i;
            StackPane sp = new StackPane(new Rectangle(RECT_SIZE, RECT_SIZE, Color.web("#2d2d2d")), texts[i] = new Text("0"));
            texts[i].setFont(Font.font("Consolas", 12));
            texts[i].setFill(Color.WHITE);
            sp.setOnMouseClicked(e -> { bits[idx] = 1 - bits[idx]; update(); });
            sp.setStyle("-fx-cursor: hand;");
            hb.getChildren().add(sp);
        }
        return hb;
    }

    private Pane createVerticalLines(Line[] lines) {
        Pane p = new Pane();
        p.setPrefHeight(40);
        double totalBusWidth = (8 * RECT_SIZE) + (7 * SPACING);
        p.setMinWidth(totalBusWidth);
        p.setMaxWidth(totalBusWidth);

        for (int i = 0; i < 8; i++) {
            lines[i] = new Line(0, 0, 0, 40);
            lines[i].setStrokeWidth(3);
            lines[i].setStroke(COLOR_OFF);

            double posX = (i * BIT_SPACING) + (RECT_SIZE / 2.0);

            lines[i].setStartX(posX);
            lines[i].setEndX(posX);
            p.getChildren().add(lines[i]);
        }

        StackPane wrapper = new StackPane(p);
        wrapper.setAlignment(Pos.CENTER);

        return p;
    }

    private void calculateSum(boolean saveToReg) {
        int a = 0, b = 0;
        for (int i = 0; i < 8; i++) {
            if(busA[i] == 1) a += Math.pow(2, 7-i);
            if(busB[i] == 1) b += Math.pow(2, 7-i);
            linesA[i].setStroke(busA[i] == 1 ? COLOR_ON : COLOR_OFF);
            linesB[i].setStroke(busB[i] == 1 ? COLOR_ON : COLOR_OFF);
        }
        int sum = (a + b) & 0xFF;
        for (int i = 0; i < 8; i++) {
            int bit = (sum >> (7 - i)) & 1;
            if(saveToReg) regBits[i] = bit;
            linesOut[i].setStroke(bit == 1 ? COLOR_ON : COLOR_OFF);
        }
    }

    private void update() {
        calculateSum(false);
        for (int i = 0; i < 8; i++) {
            textsA[i].setText(String.valueOf(busA[i]));
            textsB[i].setText(String.valueOf(busB[i]));
            textsReg[i].setText(String.valueOf(regBits[i]));
            textsReg[i].setFill(regBits[i] == 1 ? COLOR_ON : Color.web("#444"));
        }
    }

    private void applyTextStyle(javafx.scene.Node node) {
        if (node instanceof Text) ((Text) node).setFill(Color.web("#888"));
        if (node instanceof Parent) {
            for (javafx.scene.Node child : ((Parent) node).getChildrenUnmodifiable()) applyTextStyle(child);
        }
    }
}