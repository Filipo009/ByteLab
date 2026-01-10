package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TheoryRegisterView extends VBox {
    private final int[] inputBus = new int[8];
    private final int[] storedData = new int[8];
    private final Text[] inputTexts = new Text[8];
    private final Text[] storedTexts = new Text[8];
    private final Text decStoredValue = new Text("WARTOŚĆ: 0");

    public TheoryRegisterView() {
        setSpacing(20); setAlignment(Pos.CENTER); setPadding(new Insets(25));
        setStyle("-fx-background-color: #252525; -fx-border-color: #2ecc71; -fx-border-width: 2; -fx-border-radius: 10;");
        setMaxWidth(650);

        // Sekcja Wejściowa (Dane na magistrali wejściowej)
        HBox inputRow = createBitRow(inputBus, inputTexts, "#888", true);
        VBox inputSection = new VBox(5, new Text("MAGISTRALA WEJŚCIOWA (DANE DO ZAPISU)"), inputRow);
        inputSection.setAlignment(Pos.CENTER);

        // Panel sterowania (LOAD / CLEAR)
        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);

        Button loadBtn = new Button("LOAD (ZAPISZ)");
        loadBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: black; -fx-font-weight: bold;");
        loadBtn.setOnAction(e -> {
            System.arraycopy(inputBus, 0, storedData, 0, 8);
            update();
        });

        Button clearBtn = new Button("CLEAR (WYCZYŚĆ)");
        clearBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        clearBtn.setOnAction(e -> {
            for(int i=0; i<8; i++) storedData[i] = 0;
            update();
        });

        controls.getChildren().addAll(loadBtn, clearBtn);

        // Obudowa Rejestru (Przechowywane dane)
        VBox registerBox = new VBox(10);
        registerBox.setAlignment(Pos.CENTER);
        registerBox.setPrefSize(500, 100);
        registerBox.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #2ecc71; -fx-border-radius: 10; -fx-background-radius: 10;");

        HBox storedRow = createBitRow(storedData, storedTexts, "#2ecc71", false);
        decStoredValue.setFont(Font.font("Consolas", 20)); decStoredValue.setFill(Color.WHITE);
        registerBox.getChildren().addAll(new Text("ZAWARTOŚĆ REJESTRU"), storedRow, decStoredValue);

        getChildren().addAll(inputSection, controls, registerBox);

        // Stylizacja etykiet
        for(javafx.scene.Node n : getChildren()) {
            if(n instanceof VBox) {
                for(javafx.scene.Node inner : ((VBox)n).getChildren()) {
                    if(inner instanceof Text) ((Text)inner).setFill(Color.web("#bbbbbb"));
                }
            } else if(n instanceof Text) ((Text)n).setFill(Color.web("#bbbbbb"));
        }
        update();
    }

    private HBox createBitRow(int[] bits, Text[] texts, String color, boolean interactive) {
        HBox hb = new HBox(10); hb.setAlignment(Pos.CENTER);
        for (int i = 0; i < 8; i++) {
            final int idx = i;
            StackPane sp = new StackPane(new Rectangle(35, 40, Color.web("#2d2d2d")), texts[i] = new Text("0"));
            texts[idx].setFill(Color.web(color)); texts[idx].setFont(Font.font("Consolas", 18));
            if(interactive) {
                sp.setStyle("-fx-cursor: hand;");
                sp.setOnMouseClicked(e -> { bits[idx] = 1 - bits[idx]; update(); });
            }
            hb.getChildren().add(sp);
        }
        return hb;
    }

    private void update() {
        int val = 0;
        for (int i = 0; i < 8; i++) {
            inputTexts[i].setText(String.valueOf(inputBus[i]));
            storedTexts[i].setText(String.valueOf(storedData[i]));
            if(storedData[i] == 1) val += Math.pow(2, 7-i);
        }
        decStoredValue.setText("WARTOŚĆ: " + val);
    }
}