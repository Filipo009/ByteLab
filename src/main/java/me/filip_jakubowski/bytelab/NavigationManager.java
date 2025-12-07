package me.filip_jakubowski.bytelab;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import me.filip_jakubowski.bytelab.view.CommandSearchView;
import me.filip_jakubowski.bytelab.view.DiagramView;
import me.filip_jakubowski.bytelab.view.SimulationView;

public class NavigationManager {

    private final Stage stage;

    public NavigationManager(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("ByteLab");
    }

    public void showStartScreen() {
        StartView view = new StartView();
        stage.setScene(new Scene(view, 600, 400));
        stage.show();
    }

    public void showEducationMenu() {
        me.filip_jakubowski.bytelab.education.EducationMenuView view =
                new me.filip_jakubowski.bytelab.education.EducationMenuView();

        stage.setScene(new Scene(view, 900, 600));
        stage.show();
    }

    public void showLesson(int id) {
        me.filip_jakubowski.bytelab.education.LessonView view =
                new me.filip_jakubowski.bytelab.education.LessonView(id);

        stage.setScene(new Scene(view, 900, 600));
        stage.show();
    }

    public void showEmulator() {

        CommandSearchView commandSearchView = new CommandSearchView();
        SimulationView simulationView = new SimulationView();

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1000, 700);

        DiagramView diagramView = new DiagramView(scene);
        simulationView.bindHistoryList(commandSearchView.getCompletedCommandsListView());

        commandSearchView.setMinHeight(150);
        commandSearchView.setPrefHeight(250);
        commandSearchView.setMaxHeight(500);

        root.setLeft(diagramView);
        root.setCenter(simulationView);
        root.setBottom(commandSearchView);

        commandSearchView.setOnMousePressed(event -> {
            yOffset = event.getSceneY();
            initialHeight = commandSearchView.getHeight();
        });

        commandSearchView.setOnMouseDragged(event -> {
            double deltaY = event.getSceneY() - yOffset;
            double newHeight = initialHeight - deltaY;
            if (newHeight >= 150 && newHeight <= 500) {
                commandSearchView.setPrefHeight(newHeight);
            }
        });

        commandSearchView.setOnMouseMoved(event -> {
            double border = 5;
            if (event.getY() < border) {
                commandSearchView.setCursor(javafx.scene.Cursor.N_RESIZE);
            } else {
                commandSearchView.setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });

        scene.getStylesheets().add(
                getClass().getResource("/me/filip_jakubowski/bytelab/styles.css").toExternalForm()
        );

        stage.setScene(scene);
        stage.show();
    }

    private double yOffset = 0;
    private double initialHeight = 0;
}
