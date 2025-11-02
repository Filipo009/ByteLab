package me.filip_jakubowski.bytelab;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import me.filip_jakubowski.bytelab.view.DiagramView;
import me.filip_jakubowski.bytelab.view.RegisterView;
import me.filip_jakubowski.bytelab.view.CommandSearchView;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // Ustawienie sekcji głównych aplikacji
        root.setLeft(new DiagramView());
        root.setCenter(new RegisterView());
        root.setBottom(new CommandSearchView());

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(
                getClass().getResource("/me/filip_jakubowski/bytelab/styles.css").toExternalForm()
        );

        stage.setTitle("Symulator Komputera");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
