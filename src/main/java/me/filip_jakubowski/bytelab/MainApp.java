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
        // Tworzymy główne komponenty aplikacji
        CommandSearchView commandSearchView = new CommandSearchView();
        SimulationView simulationView = new SimulationView(commandSearchView.getCompletedCommandsListView());
        DiagramView diagramView = new DiagramView();

        // Ustawienie układu aplikacji
        BorderPane root = new BorderPane();
        root.setLeft(diagramView);
        root.setCenter(simulationView);
        root.setBottom(commandSearchView);

        // Tworzenie sceny i wczytanie CSS
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(
                getClass().getResource("/me/filip_jakubowski/bytelab/styles.css").toExternalForm()
        );

        // Konfiguracja i uruchomienie okna
        stage.setTitle("Symulator Komputera");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
