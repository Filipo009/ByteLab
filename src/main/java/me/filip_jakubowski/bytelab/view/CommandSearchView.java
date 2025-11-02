package me.filip_jakubowski.bytelab.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class CommandSearchView extends VBox {
    private final TextField searchField = new TextField();
    private final ListView<String> suggestions = new ListView<>();
    private final ObservableList<String> allCommands = FXCollections.observableArrayList(
            "add", "and", "sub", "load", "store", "jump"
    );

    public CommandSearchView() {
        setStyle("-fx-background-color: #ffffff; -fx-padding: 10;");
        Label title = new Label("Wyszukiwarka komend:");

        searchField.setPromptText("Wpisz komendę...");
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.isEmpty()) {
                suggestions.setItems(allCommands);
            } else {
                suggestions.setItems(allCommands.filtered(cmd -> cmd.startsWith(newText.toLowerCase())));
            }
        });

        suggestions.setItems(allCommands);

        getChildren().addAll(title, searchField, suggestions);
    }
}

