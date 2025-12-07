package me.filip_jakubowski.bytelab;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static NavigationManager navigationManager;

    @Override
    public void start(Stage stage) {
        navigationManager = new NavigationManager(stage);
        navigationManager.showStartScreen();
    }

    public static NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public static void main(String[] args) {
        launch();
    }
}
