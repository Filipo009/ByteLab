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
        CommandSearchView commandSearchView = new CommandSearchView(this);
        SimulationView simulationView = new SimulationView();

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1100, 750);

        simulationView.bindHistoryList(commandSearchView.getCompletedCommandsListView());

        commandSearchView.setPrefWidth(450);

        root.setLeft(commandSearchView);
        root.setCenter(simulationView);

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