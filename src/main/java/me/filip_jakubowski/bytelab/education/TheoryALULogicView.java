package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TheoryALULogicView extends VBox {
    private final int[] bitsA = new int[8], bitsB = new int[8];
    private final Text[] textsA = new Text[8], textsB = new Text[8], textsRes = new Text[8];
    private final ComboBox<String> opCombo = new ComboBox<>();
    private final Circle zeroFlag = new Circle(8, Color.web("#444"));

    public TheoryALULogicView() {
        setSpacing(15); setAlignment(Pos.CENTER); setPadding(new Insets(20));
        // Ujednolicone obramowanie i szerokość (niebieski dla logiki)
        setStyle("-fx-background-color: #252525; -fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 10;");
        setMaxWidth(650);

        opCombo.getItems().addAll("AND", "OR", "XOR", "NAND", "NOR");
        opCombo.setValue("AND");
        opCombo.setOnAction(e -> update());
        opCombo.setStyle("-fx-base: #333; -fx-text-fill: white; -fx-font-family: 'Consolas';");

        HBox rowA = createRow(bitsA, textsA, "#007acc");
        HBox rowB = createRow(bitsB, textsB, "#2ecc71");

        VBox resBox = new VBox(10);
        resBox.setAlignment(Pos.CENTER);
        resBox.setPrefSize(500, 130);
        resBox.setStyle("-fx-background-color: #333; -fx-background-radius: 15; -fx-border-color: #555; -fx-border-width: 1;");

        HBox resRow = new HBox(10); resRow.setAlignment(Pos.CENTER);
        for(int i=0; i<8; i++) {
            textsRes[i] = new Text("0");
            textsRes[i].setFont(Font.font("Consolas", 24));
            resRow.getChildren().add(textsRes[i]);
        }

        HBox zBox = new HBox(10, new Text("FLAG Z:"), zeroFlag);
        zBox.setAlignment(Pos.CENTER);
        ((Text)zBox.getChildren().get(0)).setFill(Color.web("#bbbbbb"));

        resBox.getChildren().addAll(opCombo, resRow, zBox);
        getChildren().addAll(new Text("WEJŚCIE A"), rowA, resBox, rowB, new Text("WEJŚCIE B"));
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
        boolean isAllZero = true;
        for (int i = 0; i < 8; i++) {
            int a = bitsA[i], b = bitsB[i];
            int r = switch(opCombo.getValue()) {
                case "AND" -> a & b;
                case "OR" -> a | b;
                case "XOR" -> a ^ b;
                case "NAND" -> (a & b) == 1 ? 0 : 1;
                case "NOR" -> (a | b) == 1 ? 0 : 1;
                default -> 0;
            };
            textsRes[i].setText(String.valueOf(r));
            textsRes[i].setFill(r == 1 ? Color.web("#3498db") : Color.web("#555"));
            if(r == 1) isAllZero = false;
            textsA[i].setText(String.valueOf(a));
            textsB[i].setText(String.valueOf(b));
        }
        zeroFlag.setFill(isAllZero ? Color.web("#2ecc71") : Color.web("#444"));
    }
}