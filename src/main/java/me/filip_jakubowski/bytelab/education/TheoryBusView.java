package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Arrays;

public class TheoryBusView extends VBox {
    private final int[] busBits = new int[8]; // Bity na magistrali (od kluczy)
    private final int[] regBits = new int[8]; // Bity zatrzaśnięte w rejestrze
    private final Text[] busTexts = new Text[8];
    private final Text[] regTexts = new Text[8];
    private final Line[] busLines = new Line[8];

    private final Color COLOR_ON = Color.web("#2ecc71");
    private final Color COLOR_OFF = Color.web("#441111");

    public TheoryBusView() {
        setSpacing(0); // Spacing 0, bo sami rysujemy połączenia liniami
        setAlignment(Pos.CENTER);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #555; -fx-border-radius: 10;");
        setMaxWidth(700);

        // 1. KLUCZE LOGICZNE (Góra: s s s s)
        HBox switchesRow = new HBox(15);
        switchesRow.setAlignment(Pos.CENTER);
        for (int i = 0; i < 8; i++) {
            final int idx = i;
            VBox sw = new VBox(5);
            sw.setAlignment(Pos.CENTER);

            Rectangle toggle = new Rectangle(30, 30, Color.web("#333"));
            toggle.setStroke(Color.GRAY);
            toggle.setArcWidth(10); toggle.setArcHeight(10);
            toggle.setStyle("-fx-cursor: hand;");

            busTexts[i] = new Text("0");
            busTexts[i].setFill(Color.WHITE);
            busTexts[i].setFont(Font.font("Consolas", 14));

            StackPane sp = new StackPane(toggle, busTexts[i]);
            sp.setOnMouseClicked(e -> {
                busBits[idx] = 1 - busBits[idx];
                update();
            });

            sw.getChildren().addAll(new Text("S" + i), sp);
            switchesRow.getChildren().add(sw);
        }

        // 2. MAGISTRALA (Linie pionowe: | | | |)
        HBox linesRow = new HBox(15);
        linesRow.setAlignment(Pos.CENTER);
        linesRow.setPadding(new Insets(0, 0, 0, 0));
        for (int i = 0; i < 8; i++) {
            busLines[i] = new Line(0, 0, 0, 100);
            busLines[i].setStrokeWidth(4);
            busLines[i].setStroke(COLOR_OFF);

            // Kontener dla linii, żeby utrzymać spacing zgodny z kluczami
            Region spacer = new Region();
            spacer.setPrefWidth(30);
            StackPane linePane = new StackPane(busLines[i]);
            linePane.setPrefWidth(30);
            linesRow.getChildren().add(linePane);
        }

        // 3. REJESTR (Dół: REG)
        VBox regContainer = new VBox(10);
        regContainer.setAlignment(Pos.CENTER);
        regContainer.setPadding(new Insets(20));
        regContainer.setStyle("-fx-background-color: #222; -fx-border-color: #2ecc71; -fx-border-radius: 5;");

        HBox regBitsRow = new HBox(15);
        regBitsRow.setAlignment(Pos.CENTER);
        for (int i = 0; i < 8; i++) {
            regTexts[i] = new Text("0");
            regTexts[i].setFont(Font.font("Consolas", 18));
            regTexts[i].setFill(COLOR_OFF);
            StackPane bitBox = new StackPane(new Rectangle(30, 35, Color.web("#111")), regTexts[i]);
            regBitsRow.getChildren().add(bitBox);
        }

        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);
        Button loadBtn = new Button("LOAD DATA");
        loadBtn.setStyle("-fx-background-color: #2ecc71; -fx-font-weight: bold; -fx-cursor: hand;");
        loadBtn.setOnAction(e -> {
            System.arraycopy(busBits, 0, regBits, 0, 8);
            update();
        });

        Button clearBtn = new Button("CLEAR REG");
        clearBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> {
            Arrays.fill(regBits, 0);
            update();
        });

        controls.getChildren().addAll(loadBtn, clearBtn);
        regContainer.getChildren().addAll(new Text("REJESTR AKUMULATORA (8-BIT)"), regBitsRow, controls);

        // Dodanie wszystkiego do głównego kontenera
        getChildren().addAll(switchesRow, linesRow, regContainer);

        // Stylizacja wszystkich tekstów pomocniczych
        for(javafx.scene.Node n : getChildren()) {
            if (n instanceof Text) ((Text)n).setFill(Color.web("#888"));
            if (n instanceof VBox) {
                for(javafx.scene.Node sub : ((VBox)n).getChildren())
                    if(sub instanceof Text) ((Text)sub).setFill(Color.web("#888"));
            }
        }

        update();
    }

    private void update() {
        for (int i = 0; i < 8; i++) {
            // Aktualizacja wizualna magistrali
            busTexts[i].setText(String.valueOf(busBits[i]));
            busLines[i].setStroke(busBits[i] == 1 ? COLOR_ON : COLOR_OFF);

            // Aktualizacja wizualna rejestru
            regTexts[i].setText(String.valueOf(regBits[i]));
            regTexts[i].setFill(regBits[i] == 1 ? COLOR_ON : Color.web("#444"));
        }
    }
}