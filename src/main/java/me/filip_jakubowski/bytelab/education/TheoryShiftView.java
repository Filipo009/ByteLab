package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TheoryShiftView extends VBox {
    private final int[] inputBits = new int[8];
    private final Text[] inputTextTexts = new Text[8];
    private final Text[] resultBitsTexts = new Text[8];
    private final Text inDecText = new Text("0");
    private final Text outDecText = new Text("0");
    private String lastOp = "BRAK";

    public TheoryShiftView() {
        setSpacing(20); setAlignment(Pos.CENTER); setPadding(new Insets(25));
        setStyle("-fx-background-color: #252525; -fx-border-color: #9b59b6; -fx-border-width: 2; -fx-border-radius: 10;");
        setMaxWidth(650);

        // Sekcja wejściowa
        HBox inRow = createBitRow(inputBits, inputTextTexts, "#007acc", true);
        VBox inputArea = new VBox(5, new Text("DANE WEJŚCIOWE"), inRow, inDecText);
        inputArea.setAlignment(Pos.CENTER);

        // Przyciski operacji
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);
        controls.getChildren().addAll(
                createOpBtn("SHL (<<)", () -> shiftLeft(false)),
                createOpBtn("SHR (>>)", () -> shiftRight(false)),
                createOpBtn("ROL", () -> shiftLeft(true)),
                createOpBtn("ROR", () -> shiftRight(true))
        );

        // Sekcja ALU / Wynik
        VBox resultBox = new VBox(10);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setPrefSize(500, 120);
        resultBox.setStyle("-fx-background-color: #333; -fx-background-radius: 15; -fx-border-color: #555;");

        HBox resRow = new HBox(10); resRow.setAlignment(Pos.CENTER);
        for(int i=0; i<8; i++) {
            resultBitsTexts[i] = new Text("0");
            resultBitsTexts[i].setFont(Font.font("Consolas", 24));
            resRow.getChildren().add(resultBitsTexts[i]);
        }

        Text opLabel = new Text("OSTATNIA OPERACJA: BRAK");
        opLabel.setFont(Font.font("Consolas", 14)); opLabel.setFill(Color.web("#9b59b6"));
        resultBox.getChildren().addAll(opLabel, resRow, outDecText);

        getChildren().addAll(inputArea, controls, resultBox);

        // Stylizacja tekstów
        for(javafx.scene.Node n : getChildren()) if(n instanceof Text) ((Text)n).setFill(Color.web("#bbbbbb"));
        inDecText.setFill(Color.WHITE); outDecText.setFill(Color.web("#2ecc71"));
        outDecText.setFont(Font.font("Consolas", 22));

        update();
    }

    private Button createOpBtn(String label, Runnable action) {
        Button b = new Button(label);
        b.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        b.setOnAction(e -> { lastOp = label; action.run(); });
        return b;
    }

    private HBox createBitRow(int[] bits, Text[] texts, String color, boolean interactive) {
        HBox hb = new HBox(10); hb.setAlignment(Pos.CENTER);
        for (int i = 0; i < 8; i++) {
            final int idx = i;
            StackPane sp = new StackPane(new Rectangle(35, 40, Color.web("#222")), texts[i] = new Text("0"));
            texts[idx].setFill(Color.web(color)); texts[idx].setFont(Font.font("Consolas", 18));
            if(interactive) {
                sp.setStyle("-fx-cursor: hand;");
                sp.setOnMouseClicked(e -> { bits[idx] = 1 - bits[idx]; update(); });
            }
            hb.getChildren().add(sp);
        }
        return hb;
    }

    private void shiftLeft(boolean rotate) {
        int first = inputBits[0];
        for(int i=0; i<7; i++) inputBits[i] = inputBits[i+1];
        inputBits[7] = rotate ? first : 0;
        update();
    }

    private void shiftRight(boolean rotate) {
        int last = inputBits[7];
        for(int i=7; i>0; i--) inputBits[i] = inputBits[i-1];
        inputBits[0] = rotate ? last : 0;
        update();
    }

    private void update() {
        int valIn = 0, valOut = 0;
        for (int i = 0; i < 8; i++) {
            if(inputBits[i] == 1) valIn += Math.pow(2, 7-i);
            inputTextTexts[i].setText(String.valueOf(inputBits[i]));
            resultBitsTexts[i].setText(String.valueOf(inputBits[i]));
            resultBitsTexts[i].setFill(inputBits[i] == 1 ? Color.web("#2ecc71") : Color.web("#666"));
        }
        inDecText.setText("WARTOŚĆ DEC: " + valIn);
        outDecText.setText("WYNIK DEC: " + valIn);
        ((Text)((VBox)getChildren().get(2)).getChildren().get(0)).setText("OPERACJA: " + lastOp);
    }
}