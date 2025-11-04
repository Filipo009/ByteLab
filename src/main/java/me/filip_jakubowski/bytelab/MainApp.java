package me.filip_jakubowski.bytelab;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import me.filip_jakubowski.bytelab.view.DiagramView;
import me.filip_jakubowski.bytelab.view.CommandSearchView;
import me.filip_jakubowski.bytelab.view.SimulationView;

public class MainApp extends Application {

    private double yOffset = 0;
    private double initialHeight = 0;

    @Override
    public void start(Stage stage) {
        CommandSearchView commandSearchView = new CommandSearchView();
        SimulationView simulationView = new SimulationView();

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1000, 700);

        DiagramView diagramView = new DiagramView(scene);
        simulationView.bindHistoryList(commandSearchView.getCompletedCommandsListView());

        commandSearchView.setMinHeight(150);
        commandSearchView.setPrefHeight(250);
        commandSearchView.setMaxHeight(500); // możesz zmienić maksymalną wysokość, jeśli chcesz

        root.setLeft(diagramView);
        root.setCenter(simulationView);
        root.setBottom(commandSearchView);

        // === 🔧 DODAJEMY RESIZER (możliwość zmiany wysokości myszką) ===
        commandSearchView.setOnMousePressed(event -> {
            yOffset = event.getSceneY();
            initialHeight = commandSearchView.getHeight();
        });

        commandSearchView.setOnMouseDragged(event -> {
            double deltaY = event.getSceneY() - yOffset;
            double newHeight = initialHeight - deltaY; // przeciągnięcie w górę -> zwiększa wysokość
            if (newHeight >= 150 && newHeight <= 500) {
                commandSearchView.setPrefHeight(newHeight);
            }
        });

        // Styl kursora podczas najechania na górną krawędź panelu
        commandSearchView.setOnMouseMoved(event -> {
            double border = 5;
            if (event.getY() < border) {
                commandSearchView.setCursor(javafx.scene.Cursor.N_RESIZE);
            } else {
                commandSearchView.setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });

        // ===============================================================

        scene.getStylesheets().add(
                getClass().getResource("/me/filip_jakubowski/bytelab/styles.css").toExternalForm()
        );

        stage.setTitle("ByteLab");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
