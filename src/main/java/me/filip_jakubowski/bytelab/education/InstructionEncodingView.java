package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.LinkedHashMap;
import java.util.Map;

public class InstructionEncodingView extends VBox {

    private final Map<String, String> opcodes = new LinkedHashMap<>();
    private final int[] currentInput = new int[4];
    private final Text[] inputBits = new Text[4];
    private final VBox tableBox = new VBox(5);

    public InstructionEncodingView() {
        setupData();
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(20));

        // --- NAGŁÓWEK ---
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);

        Text mainTitle = new Text("ARCHITEKTURA ZESTAWU INSTRUKCJI (ISA)");
        mainTitle.setFill(Color.WHITE);
        mainTitle.setFont(Font.font("Consolas", FontWeight.BOLD, 22));

        Text subTitle = new Text("Mapowanie poleceń na kody binarne zrozumiałe dla procesora");
        subTitle.setFill(Color.web("#888"));
        subTitle.setFont(Font.font("Consolas", 12));
        header.getChildren().addAll(mainTitle, subTitle);

        // --- PANEL GŁÓWNY ---
        HBox mainLayout = new HBox(40);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setMaxWidth(Region.USE_PREF_SIZE);
        mainLayout.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #444; -fx-border-radius: 15; -fx-background-radius: 15;");

        // --- TABELA ---
        VBox leftPanel = new VBox(15);
        leftPanel.setAlignment(Pos.TOP_LEFT);
        Text tableTitle = new Text("TABELA MAPOWANIA");
        tableTitle.setFill(Color.web("#3498db"));
        tableTitle.setFont(Font.font("Consolas", FontWeight.BOLD, 14));
        renderTable();
        leftPanel.getChildren().addAll(tableTitle, tableBox);

        // --- DEKODER ---
        VBox rightPanel = new VBox(25);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setMinWidth(280);

        Text decoderTitle = new Text("SYMULATOR DEKODERA");
        decoderTitle.setFill(Color.WHITE);
        decoderTitle.setFont(Font.font("Consolas", 14));

        HBox bitInput = new HBox(8);
        bitInput.setAlignment(Pos.CENTER);
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            inputBits[i] = new Text("0");
            inputBits[i].setFill(Color.WHITE);
            inputBits[i].setFont(Font.font("Consolas", 22));

            StackPane bitBox = new StackPane(new Rectangle(35, 45, Color.web("#333")), inputBits[i]);
            bitBox.setStyle("-fx-cursor: hand; -fx-border-color: #555; -fx-border-radius: 5;");
            bitBox.setOnMouseClicked(e -> {
                currentInput[idx] = 1 - currentInput[idx];
                updateDecoder();
            });
            bitInput.getChildren().add(bitBox);
        }

        VBox resultBox = new VBox(5);
        resultBox.setAlignment(Pos.CENTER);
        Text resultLabel = new Text("SYGNAŁ STERUJĄCY:");
        resultLabel.setFill(Color.GRAY);
        resultLabel.setFont(Font.font("Consolas", 10));

        Text decodedOp = new Text("NOP");
        decodedOp.setId("decodedOp");
        decodedOp.setFill(Color.web("#2ecc71"));
        // Poprawiony błąd font(): FontWeight jest trzecim argumentem
        decodedOp.setFont(Font.font("Consolas", FontWeight.BOLD, 32));

        resultBox.getChildren().addAll(resultLabel, decodedOp);
        rightPanel.getChildren().addAll(decoderTitle, bitInput, resultBox);

        mainLayout.getChildren().addAll(leftPanel, rightPanel);
        getChildren().addAll(header, mainLayout);

        updateDecoder();
    }

    private void setupData() {
        opcodes.put("0000", "NOP (No Operation)");
        opcodes.put("0001", "IN A");
        opcodes.put("0010", "IN B");
        opcodes.put("0011", "ADD");
        opcodes.put("0100", "SUB");
        opcodes.put("0101", "AND");
        opcodes.put("0110", "NAND");
        opcodes.put("0111", "OR");
        opcodes.put("1000", "NOR");
        opcodes.put("1001", "XOR");
        opcodes.put("1010", "NOT");
        opcodes.put("1011", "MOV ACC->C");
        opcodes.put("1100", "OUT C");
    }

    private void renderTable() {
        tableBox.getChildren().clear();
        opcodes.forEach((bin, name) -> {
            HBox row = new HBox(15);
            row.setPadding(new Insets(2, 10, 2, 10));

            Text binText = new Text(bin);
            binText.setFill(Color.web("#f1c40f"));
            binText.setFont(Font.font("Consolas", 13));

            Text nameText = new Text(name);
            nameText.setFill(Color.LIGHTGRAY);
            nameText.setFont(Font.font("Consolas", 13));

            row.getChildren().addAll(binText, nameText);
            tableBox.getChildren().add(row);
        });
    }

    private void updateDecoder() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            inputBits[i].setText(String.valueOf(currentInput[i]));
            sb.append(currentInput[i]);
        }

        String key = sb.toString();
        Text decodedOpDisplay = (Text) lookup("#decodedOp");
        if (decodedOpDisplay != null) {
            String instruction = opcodes.getOrDefault(key, "UNKNOWN");
            decodedOpDisplay.setText(instruction.split(" ")[0]);
            decodedOpDisplay.setFill(instruction.equals("UNKNOWN") ? Color.RED : Color.web("#2ecc71"));
        }

        tableBox.getChildren().forEach(node -> {
            HBox row = (HBox) node;
            Text t = (Text) row.getChildren().get(0);
            row.setStyle(t.getText().equals(key) ? "-fx-background-color: #2c3e50; -fx-background-radius: 4;" : "");
        });
    }
}