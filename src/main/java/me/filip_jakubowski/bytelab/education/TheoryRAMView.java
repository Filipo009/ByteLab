package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TheoryRAMView extends VBox {
    private final int[][] memory = new int[16][8];
    private final int[] addressBits = new int[4];
    private final int[] dataInputBits = new int[8];
    private final int[] outputRegister = new int[8];

    private final Text[] addressTexts = new Text[4];
    private final Text[] dataInputTexts = new Text[8];
    private final Text[] outputRegTexts = new Text[8];
    private final VBox[] memoryCellsUI = new VBox[16];
    private final Text[][] memoryBitsTexts = new Text[16][8];

    private final ToggleButton csBtn = new ToggleButton("CHIP SELECT: OFF");
    private final Text statusText = new Text("STATUS: OCZEKIWANIE");

    public TheoryRAMView() {
        setSpacing(15);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #252525; -fx-border-color: #e67e22; -fx-border-width: 2; -fx-border-radius: 10;");
        setMaxWidth(750);

        // 1. CHIP SELECT
        csBtn.setPrefWidth(200);
        csBtn.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: 'Consolas';");
        csBtn.setOnAction(e -> {
            csBtn.setText(csBtn.isSelected() ? "CHIP SELECT: ON (AKTYWNY)" : "CHIP SELECT: OFF (WYŁĄCZONY)");
            csBtn.setStyle(csBtn.isSelected() ? "-fx-background-color: #e67e22; -fx-text-fill: black;" : "-fx-background-color: #444; -fx-text-fill: white;");
            updateUI();
        });

        // 2. ADRESOWANIE
        HBox addrRow = createBitRow(addressBits, addressTexts, "#e74c3c", true, 4);
        VBox addrSection = new VBox(5, createLabel("ADRES WYBORU (4-BIT)"), addrRow);
        addrSection.setAlignment(Pos.CENTER);

        // 3. MATRYCA PAMIĘCI
        GridPane memoryGrid = new GridPane();
        memoryGrid.setHgap(8); memoryGrid.setVgap(8);
        memoryGrid.setAlignment(Pos.CENTER);
        for (int i = 0; i < 16; i++) {
            memoryCellsUI[i] = createMemoryCellUI(i);
            memoryGrid.add(memoryCellsUI[i], i % 4, i / 4);
        }

        // 4. WEJŚCIE DANYCH
        HBox dataInRow = createBitRow(dataInputBits, dataInputTexts, "#3498db", true, 8);
        VBox dataInSection = new VBox(5, createLabel("USTAW BITOWĄ WARTOŚĆ (WEJŚCIE)"), dataInRow);
        dataInSection.setAlignment(Pos.CENTER);

        // 5. PRZYCISKI AKCJI
        HBox actions = new HBox(15,
                createActionBtn("WRITE (ZAPISZ DO RAM)", this::writeMemory),
                createActionBtn("READ (ODCZYTAJ Z RAM)", this::readMemory)
        );
        actions.setAlignment(Pos.CENTER);

        // 6. REJESTR DANYCH ODCZYTANYCH
        HBox outRow = createBitRow(outputRegister, outputRegTexts, "#2ecc71", false, 8);
        VBox outSection = new VBox(5, createLabel("REJESTR DANYCH WYJŚCIOWYCH"), outRow);
        outSection.setAlignment(Pos.CENTER);

        statusText.setFont(Font.font("Consolas", 12));
        statusText.setFill(Color.web("#888"));

        getChildren().addAll(csBtn, addrSection, createLabel("KOMÓRKI PAMIĘCI"), memoryGrid, dataInSection, actions, outSection, statusText);
        updateUI();
    }

    private Text createLabel(String txt) {
        Text t = new Text(txt);
        t.setFill(Color.web("#bbbbbb"));
        t.setFont(Font.font("Consolas", 12));
        t.setStyle("-fx-font-weight: bold;");
        return t;
    }

    private VBox createMemoryCellUI(int index) {
        VBox cell = new VBox(2);
        cell.setAlignment(Pos.CENTER);
        cell.setPadding(new Insets(8));
        cell.setMinWidth(130);
        cell.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #333; -fx-border-radius: 5;");

        Text addrLabel = new Text("ADDR: " + String.format("%02d", index));
        addrLabel.setFont(Font.font("Consolas", 10));
        addrLabel.setFill(Color.GRAY);

        HBox bits = new HBox(2);
        bits.setAlignment(Pos.CENTER);
        for (int i = 0; i < 8; i++) {
            memoryBitsTexts[index][i] = new Text("0");
            memoryBitsTexts[index][i].setFont(Font.font("Consolas", 11));
            memoryBitsTexts[index][i].setFill(Color.web("#444"));
            bits.getChildren().add(memoryBitsTexts[index][i]);
        }
        cell.getChildren().addAll(addrLabel, bits);
        return cell;
    }

    private HBox createBitRow(int[] bits, Text[] texts, String color, boolean interactive, int size) {
        HBox hb = new HBox(5); hb.setAlignment(Pos.CENTER);
        for (int i = 0; i < size; i++) {
            final int idx = i;
            StackPane sp = new StackPane(new Rectangle(28, 35, Color.web("#2d2d2d")), texts[i] = new Text("0"));
            texts[idx].setFill(Color.web(color));
            texts[idx].setFont(Font.font("Consolas", 16));
            if(interactive) {
                sp.setStyle("-fx-cursor: hand;");
                sp.setOnMouseClicked(e -> {
                    bits[idx] = 1 - bits[idx];
                    updateUI();
                });
            }
            hb.getChildren().add(sp);
        }
        return hb;
    }

    private Button createActionBtn(String label, Runnable act) {
        Button b = new Button(label);
        b.setPrefWidth(180);
        b.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-font-family: 'Consolas'; -fx-cursor: hand;");
        b.setOnAction(e -> act.run());
        return b;
    }

    private void writeMemory() {
        if(!csBtn.isSelected()) { statusText.setText("BŁĄD: CHIP SELECT JEST WYŁĄCZONY!"); return; }
        int addr = getAddress();
        System.arraycopy(dataInputBits, 0, memory[addr], 0, 8);
        statusText.setText("ZAPISANO DANE POD ADRES: " + addr);
        updateUI();
    }

    private void readMemory() {
        if(!csBtn.isSelected()) { statusText.setText("BŁĄD: CHIP SELECT JEST WYŁĄCZONY!"); return; }
        int addr = getAddress();
        System.arraycopy(memory[addr], 0, outputRegister, 0, 8);
        statusText.setText("ODCZYTANO DANE Z ADRESU: " + addr);
        updateUI();
    }

    private int getAddress() {
        int addr = 0;
        for(int i=0; i<4; i++) if(addressBits[i] == 1) addr += Math.pow(2, 3-i);
        return addr;
    }

    private void updateUI() {
        int currentAddr = getAddress();
        for (int i = 0; i < 4; i++) addressTexts[i].setText(String.valueOf(addressBits[i]));
        for (int i = 0; i < 8; i++) {
            dataInputTexts[i].setText(String.valueOf(dataInputBits[i]));
            outputRegTexts[i].setText(String.valueOf(outputRegister[i]));
        }

        for (int i = 0; i < 16; i++) {
            boolean isSelected = (i == currentAddr && csBtn.isSelected());
            memoryCellsUI[i].setStyle(isSelected ?
                    "-fx-background-color: #4d3319; -fx-border-color: #e67e22; -fx-border-width: 2; -fx-border-radius: 5;" :
                    "-fx-background-color: #1a1a1a; -fx-border-color: #333; -fx-border-width: 1; -fx-border-radius: 5;");

            for (int j = 0; j < 8; j++) {
                memoryBitsTexts[i][j].setText(String.valueOf(memory[i][j]));
                memoryBitsTexts[i][j].setFill(memory[i][j] == 1 ? Color.web("#e67e22") : Color.web("#444"));
            }
        }
    }
}