package me.filip_jakubowski.bytelab;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class StartView extends VBox {

    public StartView() {
        setSpacing(25);
        setAlignment(Pos.CENTER);

        Text title = new Text("Witaj w ByteLab");
        title.setFont(Font.font(32));

        Button emulatorButton = new Button("Uruchom Emulator");
        Button educationButton = new Button("Tryb Edukacyjny");
        Button gameButton = new Button("Logiczne Kółko i Krzyżyk");

        emulatorButton.setPrefWidth(220);
        educationButton.setPrefWidth(220);
        gameButton.setPrefWidth(220);

        emulatorButton.setOnAction(e -> MainApp.getNavigationManager().showEmulator());
        educationButton.setOnAction(e -> MainApp.getNavigationManager().showEducationMenu());
        gameButton.setOnAction(e -> MainApp.getNavigationManager().showLogicGame());

        getChildren().addAll(title, emulatorButton, educationButton, gameButton);
    }
}