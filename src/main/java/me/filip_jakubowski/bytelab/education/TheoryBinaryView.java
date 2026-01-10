package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
            Text weight = new Text((isU2 && i == 0) ? "-128" : String.valueOf(power));
            weight.setFont(Font.font("Consolas", 12));
            weight.setFill(Color.web("#888888"));

            Rectangle bg = new Rectangle(40, 50, Color.web("#333"));
            bitTexts[i] = new Text("0");
            bitTexts[i].setFont(Font.font("Consolas", 24));
            bitTexts[i].setFill(Color.web("#ff4444"));
            bitTexts[i].setStyle("-fx-cursor: hand;");
            bitTexts[i].setOnMouseClicked(e -> { bits[index] = 1 - bits[index]; update(); });

            bitContainer.getChildren().addAll(weight, new StackPane(bg, bitTexts[index]));
            bitsRow.getChildren().add(bitContainer);
        }

        HBox resultsRow = new HBox(30);
        resultsRow.setAlignment(Pos.CENTER);
        resultsRow.getChildren().addAll(createLabelBox(isU2 ? "SIGNED (U2)" : "VALUE", resultValue), createLabelBox("HEX", hexValue));

        if (isU2) {
            Button signBtn = new Button("ZMIEŃ ZNAK");
            signBtn.setStyle("-fx-background-color: #ffa500; -fx-text-fill: black; -fx-font-weight: bold;");
            signBtn.setOnAction(e -> {
                for(int i=0; i<8; i++) bits[i] = 1 - bits[i];
                for(int i=7; i>=0; i--) {
                    if(bits[i] == 0) { bits[i] = 1; break; }
                    else { bits[i] = 0; }
                }
                update();
            });
            resultsRow.getChildren().add(signBtn);
        }

        getChildren().addAll(bitsRow, resultsRow);
        update();
    }

    private VBox createLabelBox(String label, Text val) {
        VBox vb = new VBox(2); vb.setAlignment(Pos.CENTER);
        Text l = new Text(label); l.setFont(Font.font("Consolas", 11)); l.setFill(Color.web("#888"));
        val.setFont(Font.font("Consolas", 26)); val.setFill(Color.WHITE);
        vb.getChildren().addAll(l, val);
        return vb;
    }

    private void update() {
        int val = 0;
        for (int i = 0; i < 8; i++) {
            bitTexts[i].setText(String.valueOf(bits[i]));
            bitTexts[i].setFill(bits[i] == 1 ? Color.web("#2ecc71") : Color.web("#ff4444"));
            if (bits[i] == 1) val += Math.pow(2, 7 - i);
        }
        hexValue.setText(String.format("%02X", val));
        if (isU2) {
            int s = (val >= 128) ? val - 256 : val;
            resultValue.setText(String.valueOf(s));
            resultValue.setFill(s < 0 ? Color.web("#ffa500") : Color.web("#2ecc71"));
        } else {
            resultValue.setText(String.valueOf(val));
        }
    }
}