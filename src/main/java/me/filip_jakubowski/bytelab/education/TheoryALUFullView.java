package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TheoryALUFullView extends VBox {
    private final int[] bitsA = new int[8], bitsB = new int[8];
    private final Text[] textsA = new Text[8], textsB = new Text[8], textsSum = new Text[8];
    private final Text resDec = new Text("0"), cOutText = new Text("0");
    private final Circle zeroFlagCircle = new Circle(8, Color.web("#444"));
    private final ToggleButton modeBtn = new ToggleButton("TRYB: DODAWANIE");

    public TheoryALUFullView() {
        setSpacing(15); setAlignment(Pos.CENTER); setPadding(new Insets(20));
        // Ujednolicone obramowanie i szerokość
        setStyle("-fx-background-color: #252525; -fx-border-color: #ffa500; -fx-border-width: 2; -fx-border-radius: 10;");
        setMaxWidth(650);

        modeBtn.setStyle("-fx-background-color: #007acc; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: 'Consolas';");
        modeBtn.setOnAction(e -> {
            modeBtn.setText(modeBtn.isSelected() ? "TRYB: ODEJMOWANIE (A + NOT B + 1)" : "TRYB: DODAWANIE");
            update();
        });

        HBox rowA = createRow(bitsA, textsA, "#007acc");
        HBox rowB = createRow(bitsB, textsB, "#2ecc71");

        VBox aluBox = new VBox(10);
        aluBox.setAlignment(Pos.CENTER);
        aluBox.setPrefSize(500, 140);
        aluBox.setStyle("-fx-background-color: #333; -fx-background-radius: 15; -fx-border-color: #555; -fx-border-width: 1;");

        HBox sumRow = new HBox(10); sumRow.setAlignment(Pos.CENTER);
        for(int i=0; i<8; i++) { textsSum[i] = new Text("0"); textsSum[i].setFont(Font.font("Consolas", 24)); sumRow.getChildren().add(textsSum[i]); }

        HBox zFlagBox = new HBox(10, new Text("FLAG Z:"), zeroFlagCircle);
        zFlagBox.setAlignment(Pos.CENTER);
        ((Text)zFlagBox.getChildren().get(0)).setFill(Color.web("#bbbbbb"));

        resDec.setFont(Font.font("Consolas", 22)); resDec.setFill(Color.WHITE);
        aluBox.getChildren().addAll(modeBtn, sumRow, resDec, zFlagBox);

        HBox footer = new HBox(20, new Text("CARRY OUT:"), cOutText);
        footer.setAlignment(Pos.CENTER);
        cOutText.setFont(Font.font("Consolas", 20));

        getChildren().addAll(new Text("WEJŚCIE A"), rowA, aluBox, rowB, new Text("WEJŚCIE B"), footer);
        for(javafx.scene.Node n : getChildren()) if(n instanceof Text) ((Text)n).setFill(Color.web("#bbbbbb"));
        update();
    }

    private HBox createRow(int[] bits, Text[] texts, String color) {
        HBox hb = new HBox(10); hb.setAlignment(Pos.CENTER);
        for (int i = 0; i < 8; i++) {
            final int idx = i;
            StackPane sp = new StackPane(new Rectangle(35, 40, Color.web("#222")), texts[i] = new Text("0"));
            texts[idx].setFill(Color.web(color)); texts[idx].setFont(Font.font("Consolas", 18));
            sp.setStyle("-fx-cursor: hand;");
            sp.setOnMouseClicked(e -> { bits[idx] = 1 - bits[idx]; update(); });
            hb.getChildren().add(sp);
        }
        return hb;
    }

    private void update() {
        int a = 0, b = 0;
        for (int i = 0; i < 8; i++) {
            if(bitsA[i] == 1) a += Math.pow(2, 7 - i);
            if(bitsB[i] == 1) b += Math.pow(2, 7 - i);
            textsA[i].setText(String.valueOf(bitsA[i]));
            textsB[i].setText(String.valueOf(bitsB[i]));
        }
        int valB = modeBtn.isSelected() ? (b ^ 0xFF) : b;
        int cin = modeBtn.isSelected() ? 1 : 0;
        int sum = (a & 0xFF) + (valB & 0xFF) + cin;
        int res8 = sum & 0xFF;
        zeroFlagCircle.setFill(res8 == 0 ? Color.web("#2ecc71") : Color.web("#444"));
        resDec.setText("WYNIK DEC: " + res8);
        for (int i = 0; i < 8; i++) {
            int bit = (res8 >> (7 - i)) & 1;
            textsSum[i].setText(String.valueOf(bit));
            textsSum[i].setFill(bit == 1 ? Color.web("#2ecc71") : Color.web("#666"));
        }
        cOutText.setText(String.valueOf((sum > 255) ? 1 : 0));
        cOutText.setFill(sum > 255 ? Color.ORANGE : Color.web("#bbbbbb"));
    }
}