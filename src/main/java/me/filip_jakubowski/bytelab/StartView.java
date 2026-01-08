package me.filip_jakubowski.bytelab;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class StartView extends VBox {

    public StartView() {
        // Główny kontener
        setSpacing(30);
        setAlignment(Pos.CENTER);

        // Nagłówek
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);

        Text title = new Text("ByteLab");
        title.setFont(Font.font("Consolas", 50));
        title.setStyle("-fx-fill: #007acc; -fx-font-weight: bold;"); // Kolor akcentu z Twojego CSS

        Text subtitle = new Text("Komputer od zera do program countera");
        subtitle.setStyle("-fx-fill: #666666; -fx-font-size: 14px;");

        header.getChildren().addAll(title, subtitle);

        // Przyciski
        VBox buttons = new VBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button emulatorButton = new Button("Uruchom Emulator");
        Button educationButton = new Button("Teoria + Instrukcja");
        Button gameButton = new Button("Gra: 0 vs 1");

        // Szerokość przycisków
        double btnWidth = 250;
        emulatorButton.setPrefWidth(btnWidth);
        educationButton.setPrefWidth(btnWidth);
        gameButton.setPrefWidth(btnWidth);

        // Nawigacja
        emulatorButton.setOnAction(e -> MainApp.getNavigationManager().showEmulator());
        educationButton.setOnAction(e -> MainApp.getNavigationManager().showEducationMenu());
        gameButton.setOnAction(e -> MainApp.getNavigationManager().showLogicGame());

        buttons.getChildren().addAll(emulatorButton, educationButton, gameButton);
        getChildren().addAll(header, buttons);
    }
}