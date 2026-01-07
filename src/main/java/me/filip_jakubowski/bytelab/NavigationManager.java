package me.filip_jakubowski.bytelab;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import me.filip_jakubowski.bytelab.view.CommandSearchView;
import me.filip_jakubowski.bytelab.view.DiagramView;
import me.filip_jakubowski.bytelab.view.SimulationView;
import me.filip_jakubowski.bytelab.logicgame.LogicGameView;

public class NavigationManager {

    private final Stage stage;

    public NavigationManager(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("ByteLab");
    }

    public void showStartScreen() {
        StartView view = new StartView();
        stage.setScene(new Scene(view, 600, 450));
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
        CommandSearchView commandSearchView = new CommandSearchView(this);
        SimulationView simulationView = new SimulationView();

        HBox root = new HBox();
        Scene scene = new Scene(root, 1200, 800);

        simulationView.bindHistoryList(commandSearchView.getCompletedCommandsListView());

        commandSearchView.prefWidthProperty().bind(root.widthProperty().multiply(0.5));
        simulationView.prefWidthProperty().bind(root.widthProperty().multiply(0.5));

        HBox.setHgrow(commandSearchView, Priority.ALWAYS);
        HBox.setHgrow(simulationView, Priority.ALWAYS);

        root.getChildren().addAll(commandSearchView, simulationView);

        if (getClass().getResource("/me/filip_jakubowski/bytelab/styles.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/me/filip_jakubowski/bytelab/styles.css").toExternalForm());
        }

        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void showLogicGame() {
        LogicGameView view = new LogicGameView();
        Scene scene = new Scene(view, 1000, 750);
        if (getClass().getResource("/me/filip_jakubowski/bytelab/styles.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/me/filip_jakubowski/bytelab/styles.css").toExternalForm());
        }
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void openDiagramWindow() {
        Stage diagramStage = new Stage();
        diagramStage.setTitle("Schemat blokowy procesora");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);
        DiagramView diagramView = new DiagramView(scene);
        root.setCenter(diagramView);
        diagramStage.setScene(scene);
        diagramStage.show();
    }
}