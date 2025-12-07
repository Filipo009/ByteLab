package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import me.filip_jakubowski.bytelab.MainApp;

public class EducationMenuView extends BorderPane {

    public EducationMenuView() {

        ListView<String> list = new ListView<>();
        list.getItems().addAll("Lekcja 1", "Lekcja 2", "Lekcja 3");

        list.setOnMouseClicked(e -> {
            if (list.getSelectionModel().getSelectedIndex() >= 0) {
                int id = list.getSelectionModel().getSelectedIndex();
                MainApp.getNavigationManager().showLesson(id);
            }
        });

        setLeft(list);
        BorderPane.setMargin(list, new Insets(10));

        HBox bottom = new HBox();
        bottom.setSpacing(20);
        bottom.setPadding(new Insets(10));
        bottom.setAlignment(Pos.CENTER);

        Button backToMenu = new Button("Wyjdź do Menu Głównego");
        backToMenu.setOnAction(e -> MainApp.getNavigationManager().showStartScreen());

        bottom.getChildren().add(backToMenu);
        setBottom(bottom);
    }
}
