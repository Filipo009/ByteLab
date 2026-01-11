package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.util.LinkedHashMap;
import java.util.Map;

public class TheoryInstructionCompilerView extends VBox {
    private final Map<String, String> ops = new LinkedHashMap<>();
    private final Map<String, String> regs = new LinkedHashMap<>();
    private final int[] bits = new int[8];
    private final Text[] bitTexts = new Text[8];
    private final Text asmPreview = new Text("NOP");

    public TheoryInstructionCompilerView() {
        setupMappings();
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(25));
        setStyle("-fx-background-color: #121212; -fx-border-color: #3498db; -fx-border-radius: 10;");
        setMaxWidth(750);

        Text title = new Text("8-BIT INSTRUCTION COMPILER");
        title.setFill(Color.web("#3498db"));
        title.setFont(Font.font("Consolas", FontWeight.BOLD, 18));

        HBox bitBox = new HBox(5);
        bitBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < 8; i++) {
            final int idx = i;
            bitTexts[i] = new Text("0");
            bitTexts[i].setFill(Color.WHITE);
            bitTexts[i].setFont(Font.font("Consolas", 20));
            Color sectionColor = i < 4 ? Color.web("#3498db") : (i < 6 ? Color.web("#f1c40f") : Color.web("#e74c3c"));
            StackPane bitPlate = new StackPane(new Rectangle(35, 45, Color.web("#222")), bitTexts[i]);
            bitPlate.setStyle("-fx-border-color: " + toHex(sectionColor) + "; -fx-border-radius: 4; -fx-cursor: hand;");
            bitPlate.setOnMouseClicked(e -> { bits[idx] = 1 - bits[idx]; update(); });
            bitBox.getChildren().add(bitPlate);
            if(i == 3 || i == 5) bitBox.getChildren().add(new Region(){{setPrefWidth(15);}});
        }

        VBox res = new VBox(5, new Label("ASM PREVIEW:"), asmPreview);
        res.setAlignment(Pos.CENTER);
        asmPreview.setFont(Font.font("Consolas", FontWeight.BOLD, 28));
        asmPreview.setFill(Color.web("#2ecc71"));

        getChildren().addAll(title, bitBox, res);
        update();
    }

    private void setupMappings() {
        ops.put("0000", "NOP");  ops.put("0001", "IN");   ops.put("0010", "OUT");
        ops.put("0011", "ADD");  ops.put("0100", "SUB");  ops.put("0101", "AND");
        ops.put("1010", "NOT");  ops.put("1011", "MOV");  ops.put("1100", "JUMP");
        ops.put("1101", "JZ");
        regs.put("00", "REG A"); regs.put("01", "REG B"); regs.put("10", "REG C"); regs.put("11", "REG D");
    }

    private void update() {
        StringBuilder opB = new StringBuilder(), srcB = new StringBuilder(), dstB = new StringBuilder();
        for(int i=0; i<8; i++) {
            bitTexts[i].setText(String.valueOf(bits[i]));
            if(i < 4) opB.append(bits[i]);
            else if(i < 6) srcB.append(bits[i]);
            else dstB.append(bits[i]);
        }
        String op = ops.getOrDefault(opB.toString(), "???");
        String src = regs.get(srcB.toString());
        String dst = regs.get(dstB.toString());

        if (op.equals("???")) { asmPreview.setText("UNKNOWN"); asmPreview.setFill(Color.RED); }
        else {
            asmPreview.setFill(Color.web("#2ecc71"));
            switch(op) {
                case "NOP" -> asmPreview.setText("NOP");
                case "IN" -> asmPreview.setText("IN -> " + dst);
                case "OUT" -> asmPreview.setText("OUT " + src);
                case "JUMP", "JZ" -> asmPreview.setText(op + " 0x" + srcB + dstB);
                case "ADD", "SUB", "AND" -> asmPreview.setText(op + " -> " + dst);
                default -> asmPreview.setText(op + " " + src + " -> " + dst);
            }
        }
    }

    private String toHex(Color c) { return String.format("#%02X%02X%02X", (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255)); }
}