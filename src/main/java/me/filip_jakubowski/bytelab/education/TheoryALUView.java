package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TheoryALUView extends VBox {
    private final int[] bitsA = new int[8];
    private final int[] bitsB = new int[8];
    private final Text[] textsA = new Text[8];
    private final Text[] textsB = new Text[8];
    private final Text[] textsSum = new Text[8];

    private int carryIn = 0;
    private final Text carryInText = new Text("0");
    private final Text carryOutText = new Text("0");
    private final Text decimalResult = new Text("SUMA: 0");
    private final Rectangle aluBox = new Rectangle(450, 100);

    public TheoryALUView() {
        setSpacing(10);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));

        // Rząd A (Góra)
        HBox rowA = createBitRow(bitsA, textsA, "#007acc");

        // Środek - ALU Block z Carry
        HBox aluContainer = new HBox(15);
        aluContainer.setAlignment(Pos.CENTER);

        VBox cOutBox = createCarryBox("C-OUT", carryOutText, Pos.CENTER_LEFT);

        StackPane centerBlock = new StackPane();
        aluBox.setFill(Color.web("#333"));
        aluBox.setStroke(Color.web("#444"));
        aluBox.setStrokeWidth(2);
        aluBox.setArcWidth(15);
        aluBox.setArcHeight(15);

        HBox sumBitsRow = new HBox(10);
        sumBitsRow.setAlignment(Pos.CENTER);
        for(int i=0; i<8; i++) {
            textsSum[i] = new Text("0");
            textsSum[i].setFont(Font.font("Consolas", 20));
            textsSum[i].setFill(Color.web("#888"));
            sumBitsRow.getChildren().add(textsSum[i]);
        }

        VBox aluContent = new VBox(5, decimalResult, sumBitsRow);
        aluContent.setAlignment(Pos.CENTER);
        decimalResult.setFont(Font.font("Consolas", 24));
        decimalResult.setFill(Color.WHITE);

        centerBlock.getChildren().addAll(aluBox, aluContent);

        VBox cInBox = createCarryBox("C-IN", carryInText, Pos.CENTER_RIGHT);
        carryInText.setStyle("-fx-cursor: hand;");
        carryInText.setOnMouseClicked(e -> { carryIn = 1 - carryIn; update(); });

        aluContainer.getChildren().addAll(cOutBox, centerBlock, cInBox);

        // Rząd B (Dół)
        HBox rowB = createBitRow(bitsB, textsB, "#2ecc71");

        getChildren().addAll(new Text("WEJŚCIE A"), rowA, aluContainer, rowB, new Text("WEJŚCIE B"));
        update();
    }

    private HBox createBitRow(int[] bits, Text[] texts, String color) {
        HBox hb = new HBox(10);
        hb.setAlignment(Pos.CENTER);
        for (int i = 0; i < 8; i++) {
            final int idx = i;
            StackPane sp = new StackPane();
            Rectangle r = new Rectangle(35, 40, Color.web("#222"));
            r.setStroke(Color.web("#444"));
            texts[i] = new Text("0");
            texts[i].setFont(Font.font("Consolas", 18));
            texts[i].setFill(Color.web(color));
            sp.getChildren().addAll(r, texts[i]);
            sp.setStyle("-fx-cursor: hand;");
            sp.setOnMouseClicked(e -> { bits[idx] = 1 - bits[idx]; update(); });
            hb.getChildren().add(sp);
        }
        return hb;
    }

    private VBox createCarryBox(String label, Text valText, Pos align) {
        VBox vb = new VBox(5);
        vb.setAlignment(align);
        Text l = new Text(label);
        l.setFont(Font.font("Consolas", 10));
        l.setFill(Color.web("#888"));
        valText.setFont(Font.font("Consolas", 22));
        valText.setFill(Color.web("#ffa500"));
        vb.getChildren().addAll(l, valText);
        return vb;
    }

    private void update() {
        int valA = 0, valB = 0;
        for (int i = 0; i < 8; i++) {
            valA += bitsA[i] * Math.pow(2, 7 - i);
            valB += bitsB[i] * Math.pow(2, 7 - i);
            textsA[i].setText(String.valueOf(bitsA[i]));
            textsB[i].setText(String.valueOf(bitsB[i]));
        }

        int sum = valA + valB + carryIn;
        int result8Bit = sum & 0xFF;
        int cOut = (sum > 255) ? 1 : 0;

        carryInText.setText(String.valueOf(carryIn));
        carryInText.setFill(carryIn == 1 ? Color.web("#ffa500") : Color.web("#555"));

        carryOutText.setText(String.valueOf(cOut));
        carryOutText.setFill(cOut == 1 ? Color.web("#ffa500") : Color.web("#555"));
        aluBox.setStroke(cOut == 1 ? Color.web("#ffa500") : Color.web("#444"));

        decimalResult.setText("SUMA: " + result8Bit);

        for (int i = 0; i < 8; i++) {
            int bit = (result8Bit >> (7 - i)) & 1;
            textsSum[i].setText(String.valueOf(bit));
            textsSum[i].setFill(bit == 1 ? Color.web("#2ecc71") : Color.web("#888"));
        }
    }
}