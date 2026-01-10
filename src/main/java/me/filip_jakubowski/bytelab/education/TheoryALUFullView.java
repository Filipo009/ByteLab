package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TheoryALUFullView extends VBox {
    private final int[] bitsA = new int[8], bitsB = new int[8];
    private final Text[] textsA = new Text[8], textsB = new Text[8], textsSum = new Text[8];
    private final Text resDec = new Text("0"), cOutText = new Text("0");
    private final ToggleButton modeBtn = new ToggleButton("DODAWANIE");

    public TheoryALUFullView() {
        setSpacing(15); setAlignment(Pos.CENTER); setPadding(new Insets(20));
        setStyle("-fx-background-color: #252525; -fx-border-color: #444; -fx-border-radius: 10;");

        modeBtn.setStyle("-fx-background-color: #007acc; -fx-text-fill: white; -fx-font-weight: bold;");
        modeBtn.setOnAction(e -> {
            modeBtn.setText(modeBtn.isSelected() ? "ODEJMOWANIE (A + NOT B + 1)" : "DODAWANIE");
            modeBtn.setStyle(modeBtn.isSelected() ? "-fx-background-color: #e74c3c; -fx-text-fill: white;" : "-fx-background-color: #007acc; -fx-text-fill: white;");
            update();
        });

        HBox rowA = createRow(bitsA, textsA, "#007acc");
        HBox rowB = createRow(bitsB, textsB, "#2ecc71");

        VBox aluBox = new VBox(10);
        aluBox.setAlignment(Pos.CENTER);
        aluBox.setPrefSize(450, 120);
        aluBox.setStyle("-fx-background-color: #333; -fx-background-radius: 15; -fx-border-color: #555;");

        HBox sumRow = new HBox(10); sumRow.setAlignment(Pos.CENTER);
        for(int i=0; i<8; i++) { textsSum[i] = new Text("0"); textsSum[i].setFont(Font.font("Consolas", 22)); sumRow.getChildren().add(textsSum[i]); }

        resDec.setFont(Font.font("Consolas", 20)); resDec.setFill(Color.WHITE);
        aluBox.getChildren().addAll(modeBtn, sumRow, resDec);

        HBox footer = new HBox(10, new Text("CARRY OUT:"), cOutText);
        footer.setAlignment(Pos.CENTER);
        ((Text)footer.getChildren().get(0)).setFill(Color.GRAY);

        getChildren().addAll(new Text("WEJŚCIE A"), rowA, aluBox, rowB, new Text("WEJŚCIE B"), footer);
        update();
    }

    private HBox createRow(int[] bits, Text[] texts, String color) {
        HBox hb = new HBox(10); hb.setAlignment(Pos.CENTER);
        for (int i = 0; i < 8; i++) {
            final int idx = i;
            StackPane sp = new StackPane(new Rectangle(35, 40, Color.web("#222")), texts[i] = new Text("0"));
            texts[i].setFill(Color.web(color)); texts[i].setFont(Font.font("Consolas", 18));
            sp.setOnMouseClicked(e -> { bits[idx] = 1 - bits[idx]; update(); });
            hb.getChildren().add(sp);
        }
        return hb;
    }

    private void update() {
        int a = 0, b = 0;
        for (int i = 0; i < 8; i++) {
            a += bitsA[i] * Math.pow(2, 7 - i);
            b += bitsB[i] * Math.pow(2, 7 - i);
            textsA[i].setText(String.valueOf(bitsA[i]));
            textsB[i].setText(String.valueOf(bitsB[i]));
        }
        int effectiveB = modeBtn.isSelected() ? (b ^ 0xFF) : b;
        int sum = a + effectiveB + (modeBtn.isSelected() ? 1 : 0);
        int res8 = sum & 0xFF;

        int sVal = (res8 >= 128) ? res8 - 256 : res8;
        resDec.setText("WYNIK U2: " + sVal);
        cOutText.setText(String.valueOf((sum > 255) ? 1 : 0));
        cOutText.setFill(sum > 255 ? Color.ORANGE : Color.GRAY);

        for (int i = 0; i < 8; i++) {
            int bit = (res8 >> (7 - i)) & 1;
            textsSum[i].setText(String.valueOf(bit));
            textsSum[i].setFill(bit == 1 ? Color.web("#2ecc71") : Color.web("#666"));
        }
    }
}