package me.filip_jakubowski.bytelab;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import me.filip_jakubowski.bytelab.view.DiagramView;
import me.filip_jakubowski.bytelab.view.CommandSearchView;
import me.filip_jakubowski.bytelab.view.SimulationView;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // Główne komponenty aplikacji
        CommandSearchView commandSearchView = new CommandSearchView();
        SimulationView simulationView = new SimulationView();

        // Układ
        BorderPane root = new BorderPane();

        // Scena przed tworzeniem DiagramView (potrzebna do skalowania)
        Scene scene = new Scene(root, 1000, 700);

        DiagramView diagramView = new DiagramView(scene);
        simulationView.bindHistoryList(commandSearchView.getCompletedCommandsListView());

        // Ustawienie minimalnych wymiarów dla dolnego panelu (dynamiczne proporcje)
        commandSearchView.setMinHeight(250);
        commandSearchView.setPrefHeight(250);
        commandSearchView.setMaxHeight(Double.MAX_VALUE);

        root.setLeft(diagramView);
        root.setCenter(simulationView);
        root.setBottom(commandSearchView);

        // Marginesy
        BorderPane.setMargin(diagramView, new javafx.geometry.Insets(5));
        BorderPane.setMargin(simulationView, new javafx.geometry.Insets(5));
        BorderPane.setMargin(commandSearchView, new javafx.geometry.Insets(5));

        scene.getStylesheets().add(
                getClass().getResource("/me/filip_jakubowski/bytelab/styles.css").toExternalForm()
        );

        stage.setTitle("ByteLab");
        stage.setScene(scene);
        stage.show();

        // Dynamiczna wysokość dolnego panelu (15% wysokości okna)
        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            double height = newVal.doubleValue();
            commandSearchView.setPrefHeight(Math.max(150, height * 0.15));
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
