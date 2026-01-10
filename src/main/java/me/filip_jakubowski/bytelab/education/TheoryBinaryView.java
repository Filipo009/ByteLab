package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TheoryBinaryView extends VBox {
    private final int[] bits = new int[8];
    private final Text[] bitTexts = new Text[8];
    private final Text resultValue = new Text("0");
    private final Text hexValue = new Text("00");
    private final boolean isU2;

    public TheoryBinaryView(boolean isU2) {
        this.isU2 = isU2;
        setSpacing(15);
        setAlignment(Pos.CENTER);
        setPadding(new javafx.geometry.Insets(20));
        setStyle("-fx-background-color: #2d2d2d; -fx-background-radius: 10; -fx-border-color: " + (isU2 ? "#ffa500" : "#007acc") + "; -fx-border-width: 2;");
        setMaxWidth(600);

        HBox bitsRow = new HBox(10);
        bitsRow.setAlignment(Pos.CENTER);

        for (int i = 0; i < 8; i++) {
            final int index = i;
            VBox bitContainer = new VBox(5);
            bitContainer.setAlignment(Pos.CENTER);

            int power = (int) Math.pow(2, 7 - i);
            String weightLabel = (isU2 && i == 0) ? "-128" : String.valueOf(power);
            Text weight = new Text(weightLabel);
            weight.setFont(Font.font("Consolas", 12));
            weight.setFill(Color.web("#888888"));

            Rectangle bg = new Rectangle(40, 50);
            bg.setFill(Color.web("#3d3d3d"));
            bg.setArcWidth(5);
            bg.setArcHeight(5);

            bitTexts[i] = new Text("0");
            bitTexts[i].setFont(Font.font("Consolas", 24));
            bitTexts[i].setFill(Color.web("#ff4444"));
            bitTexts[i].setStyle("-fx-cursor: hand;");
            bitTexts[i].setOnMouseClicked(e -> {
                bits[index] = 1 - bits[index];
                update();
            });

            bitContainer.getChildren().addAll(weight, new StackPane(bg, bitTexts[index]));
            bitsRow.getChildren().add(bitContainer);
        }

        HBox resultsRow = new HBox(40);
        resultsRow.setAlignment(Pos.CENTER);
        resultsRow.getChildren().addAll(
                createLabelVBox(isU2 ? "WARTOŚĆ (U2)" : "WARTOŚĆ (DEC)", resultValue),
                createLabelVBox("HEX", hexValue)
        );

        getChildren().addAll(bitsRow, resultsRow);
        update();
    }

    private VBox createLabelVBox(String label, Text val) {
        VBox vb = new VBox(2);
        vb.setAlignment(Pos.CENTER);
        Text l = new Text(label);
        l.setFont(Font.font("Consolas", 12));
        l.setFill(Color.web("#888888"));
        val.setFont(Font.font("Consolas", 28));
        val.setFill(Color.web("#e0e0e0"));
        vb.getChildren().addAll(l, val);
        return vb;
    }

    private void update() {
        int total = 0;
        for (int i = 0; i < 8; i++) {
            bitTexts[i].setText(String.valueOf(bits[i]));
            bitTexts[i].setFill(bits[i] == 1 ? Color.web("#2ecc71") : Color.web("#ff4444"));
            if (bits[i] == 1) {
                if (isU2 && i == 0) total -= 128;
                else total += Math.pow(2, 7 - i);
            }
        }
        resultValue.setText(String.valueOf(total));

        int hexVal = 0;
        for (int i = 0; i < 8; i++) if (bits[i] == 1) hexVal += Math.pow(2, 7 - i);
        hexValue.setText(String.format("%02X", hexVal));
    }
}