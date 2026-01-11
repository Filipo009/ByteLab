package me.filip_jakubowski.bytelab.education;

// KLUCZOWE IMPORTY, KTÓRYCH BRAKOWAŁO:
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane; // To rozwiązuje błąd ScrollPane
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.LinkedHashMap;
import java.util.Map;

public class TheoryOpcodeDecoderView extends VBox {
    private final Map<String, String> opcodes = new LinkedHashMap<>();
    private final int[] currentInput = new int[4];
    private final Text[] inputBits = new Text[4];
    private final Text decodedOpDisplay = new Text("NOP");
    private final VBox tableBox = new VBox(2);

    public TheoryOpcodeDecoderView() {
        setupData();
        setAlignment(Pos.CENTER);
        setSpacing(20.0);
        setPadding(new Insets(25));
        setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #444; -fx-border-radius: 15;");
        setMaxWidth(700.0);

        Text title = new Text("DEKODER OPCODE (STEROWANIE)");
        title.setFill(Color.web("#3498db"));
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 18));

        HBox mainLayout = new HBox(40.0);
        mainLayout.setAlignment(Pos.CENTER);

        // --- LISTA DOSTĘPNYCH INSTRUKCJI (LEWO) ---
        renderTable();
        ScrollPane scroll = new ScrollPane(tableBox);

        // Używamy wartości double (z kropką), aby uniknąć błędów typowania
        scroll.setPrefHeight(200.0);
        scroll.setPrefWidth(180.0);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-viewport-background: transparent;");

        // --- INTERAKTYWNE BITY (PRAWO) ---
        VBox interaction = new VBox(15.0);
        interaction.setAlignment(Pos.CENTER);

        HBox bitInput = new HBox(8.0);
        bitInput.setAlignment(Pos.CENTER);
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            inputBits[idx] = new Text("0");
            inputBits[idx].setFill(Color.WHITE);
            inputBits[idx].setFont(Font.font("Consolas", 24));

            StackPane bitBox = new StackPane(new Rectangle(40.0, 50.0, Color.web("#333")), inputBits[idx]);
            bitBox.setStyle("-fx-cursor: hand; -fx-border-color: #555; -fx-border-radius: 5;");
            bitBox.setOnMouseClicked(e -> {
                currentInput[idx] = 1 - currentInput[idx];
                update();
            });
            bitInput.getChildren().add(bitBox);
        }

        Text arrow = new Text("▼");
        arrow.setFill(Color.GRAY);
        arrow.setFont(Font.font(20.0));

        decodedOpDisplay.setFont(Font.font("Consolas", FontWeight.BOLD, 42));
        decodedOpDisplay.setFill(Color.web("#2ecc71"));

        interaction.getChildren().addAll(new Text("USTAW BITY OPCODE:"), bitInput, arrow, new Text("INSTRUKCJA:"), decodedOpDisplay);

        // Naprawa błędu addAll - upewniamy się, że dodajemy poprawne obiekty Node
        mainLayout.getChildren().addAll(scroll, interaction);

        getChildren().addAll(title, mainLayout);

        // Stylizacja tekstów pomocniczych (żeby były szare/jasne)
        for (javafx.scene.Node n : interaction.getChildren()) {
            if (n instanceof Text && n != decodedOpDisplay) {
                ((Text) n).setFill(Color.GRAY);
                ((Text) n).setFont(Font.font("Consolas", 12));
            }
        }

        update();
    }

    private void setupData() {
        opcodes.put("0000", "NOP");  opcodes.put("0001", "IN");
        opcodes.put("0010", "OUT");  opcodes.put("0011", "ADD");
        opcodes.put("0100", "SUB");  opcodes.put("0101", "AND");
        opcodes.put("0110", "NAND"); opcodes.put("0111", "OR");
        opcodes.put("1000", "NOR");  opcodes.put("1001", "XOR");
        opcodes.put("1010", "NOT");  opcodes.put("1011", "MOV");
        opcodes.put("1100", "JUMP"); opcodes.put("1101", "JZ");
    }

    private void renderTable() {
        opcodes.forEach((bin, name) -> {
            HBox row = new HBox(15.0);
            row.setPadding(new Insets(2, 5, 2, 5));
            Text tBin = new Text(bin);
            tBin.setFill(Color.web("#f1c40f"));
            tBin.setFont(Font.font("Consolas", 12));

            Text tName = new Text(name);
            tName.setFill(Color.WHITE);
            tName.setFont(Font.font("Consolas", FontWeight.BOLD, 12));

            row.getChildren().addAll(tBin, tName);
            tableBox.getChildren().add(row);
        });
    }

    private void update() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            inputBits[i].setText(String.valueOf(currentInput[i]));
            sb.append(currentInput[i]);
        }
        String key = sb.toString();
        String found = opcodes.getOrDefault(key, "???");

        decodedOpDisplay.setText(found);
        decodedOpDisplay.setFill(found.equals("???") ? Color.web("#e74c3c") : Color.web("#2ecc71"));

        for (javafx.scene.Node n : tableBox.getChildren()) {
            if (n instanceof HBox row) {
                Text binInTable = (Text) row.getChildren().get(0);
                if (binInTable.getText().equals(key)) {
                    row.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 3;");
                } else {
                    row.setStyle("");
                }
            }
        }
    }
}