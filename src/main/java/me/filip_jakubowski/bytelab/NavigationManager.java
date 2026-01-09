package me.filip_jakubowski.bytelab;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import me.filip_jakubowski.bytelab.view.CommandSearchView;
import me.filip_jakubowski.bytelab.view.SimulationView;
import me.filip_jakubowski.bytelab.view.DiagramView;
import me.filip_jakubowski.bytelab.logicgame.LogicGameView;
import me.filip_jakubowski.bytelab.education.EducationView;

public class NavigationManager {

    private final Stage stage;

    public NavigationManager(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("ByteLab");
    }

    private void switchRoot(Parent root, double prefWidth, double prefHeight) {
        if (stage.getScene() == null) {
            Scene scene = new Scene(root, prefWidth, prefHeight);
            applyStyles(scene);
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(root);
            // Jeśli okno nie jest w trybie pełnoekranowym ani zmaksymalizowane,
            // dostosowujemy rozmiar do preferowanego dla danego widoku.
            if (!stage.isFullScreen() && !stage.isMaximized()) {
                stage.setWidth(prefWidth);
                stage.setHeight(prefHeight);
                stage.centerOnScreen();
            }
        }
        stage.show();
    }

    public void showStartScreen() {
        StartView view = new StartView();
        switchRoot(view, 600, 450);
    }

    public void showEducationMenu() {
        showLesson(0);
    }

    public void showLesson(int id) {
        EducationView view = new EducationView(id);
        switchRoot(view, 900, 600);
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
        switchRoot(mainContent, 1200, 800);
    }

    public void showLogicGame() {
        LogicGameView view = new LogicGameView();
        switchRoot(view, 1000, 750);
    }

    public void openDiagramWindow() {
        if (stage.getScene() != null) {
            DiagramView view = new DiagramView(stage.getScene());
            switchRoot(view, 1000, 750);
        }
    }

    private void applyStyles(Scene scene) {
        String cssPath = "/me/filip_jakubowski/bytelab/styles.css";
        if (getClass().getResource(cssPath) != null) {
            scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        }
    }
}