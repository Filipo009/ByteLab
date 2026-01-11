package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class TheoryChallengeView extends VBox {
    public TheoryChallengeView(String steps, String solution) {
        setSpacing(15);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER);
        setMaxWidth(550);
        setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #f1c40f; -fx-border-radius: 10; -fx-border-width: 2;");

        Text hintHeader = new Text("POMOC DO ZADANIA");
        hintHeader.setFill(Color.web("#f1c40f"));
        hintHeader.setFont(Font.font("System", FontWeight.BOLD, 14));

        // --- PANEL 1: WSKAZÓWKI (KROKI) ---
        Text stepsText = new Text(steps);
        stepsText.setFill(Color.LIGHTGRAY);
        stepsText.setFont(Font.font("System", 13));
        stepsText.setWrappingWidth(420);

        VBox stepsContainer = new VBox(stepsText);
        stepsContainer.setPadding(new Insets(10));
        stepsContainer.setAlignment(Pos.TOP_LEFT); // Wyrównanie do lewej
        stepsContainer.setStyle("-fx-background-color: #252525;");

        TitledPane stepsPane = new TitledPane("POKAŻ WSKAZÓWKI (KROK PO KROKU)", stepsContainer);
        stepsPane.setExpanded(false);
        stepsPane.setAnimated(true);
        stepsPane.setMaxWidth(450);

        // --- PANEL 2: ROZWIĄZANIE (KOD) ---
        Text solText = new Text(solution);
        solText.setFill(Color.web("#2ecc71"));
        solText.setFont(Font.font("Consolas", 16));

        VBox solContainer = new VBox(solText);
        solContainer.setPadding(new Insets(15));
        solContainer.setAlignment(Pos.TOP_LEFT); // Wyrównanie do lewej
        solContainer.setStyle("-fx-background-color: #000;");

        TitledPane solutionPane = new TitledPane("POKAŻ GOTOWY KOD (ROZWIĄZANIE)", solContainer);
        solutionPane.setExpanded(false);
        solutionPane.setAnimated(true);
        solutionPane.setMaxWidth(450);
        solutionPane.getStyleClass().add("challenge-pane");

        getChildren().addAll(hintHeader, stepsPane, solutionPane);
    }
}