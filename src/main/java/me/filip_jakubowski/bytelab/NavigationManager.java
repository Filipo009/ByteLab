package me.filip_jakubowski.bytelab;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import me.filip_jakubowski.bytelab.view.CommandSearchView;
import me.filip_jakubowski.bytelab.view.SimulationView;
import me.filip_jakubowski.bytelab.logicgame.LogicGameView;
import me.filip_jakubowski.bytelab.education.EducationView;

public class NavigationManager {

    private final Stage stage;

    public NavigationManager(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("ByteLab");
    }

    public void showStartScreen() {
        StartView view = new StartView();
        Scene scene = new Scene(view, 600, 450);
        applyStyles(scene);
        stage.setScene(scene);
        stage.show();
    }

    public void showEducationMenu() {
        showLesson(0); // Przekierowanie do nowej klasy EducationView zaczynając od pierwszej lekcji
    }

    public void showLesson(int id) {
        // Jawna inicjalizacja widoku
        me.filip_jakubowski.bytelab.education.EducationView view =
                new me.filip_jakubowski.bytelab.education.EducationView(id);

        // Rozwiązanie błędu: upewnienie się, że przekazujemy (Parent, double, double)
        // Usunięto ewentualne literówki w liczbach
        Scene scene = new Scene((javafx.scene.Parent) view, 900.0, 600.0);

        applyStyles(scene);
        stage.setScene(scene);
        stage.show();
    }

    public void showEmulator() {
        CommandSearchView commandSearchView = new CommandSearchView(this);
        SimulationView simulationView = new SimulationView();

        HBox mainContent = new HBox();
        simulationView.bindHistoryList(commandSearchView.getCompletedCommandsListView());

        commandSearchView.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.5));
        simulationView.prefWidthProperty().bind(mainContent.widthProperty().multiply(0.5));

        HBox.setHgrow(commandSearchView, Priority.ALWAYS);
        HBox.setHgrow(simulationView, Priority.ALWAYS);

        mainContent.getChildren().addAll(commandSearchView, simulationView);

        Scene scene = new Scene(mainContent, 1200, 800);
        applyStyles(scene);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void showLogicGame() {
        LogicGameView view = new LogicGameView();
        Scene scene = new Scene(view, 1000, 750);
        applyStyles(scene);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void openDiagramWindow() {
        // Logika otwierania okna schematu
    }

    private void applyStyles(Scene scene) {
        String cssPath = "/me/filip_jakubowski/bytelab/styles.css";
        if (getClass().getResource(cssPath) != null) {
            scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        }
    }
}