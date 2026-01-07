package me.filip_jakubowski.bytelab.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.Scene;

public class DiagramView extends StackPane {

    public DiagramView(Scene scene) {
        setStyle("-fx-background-color: #f4f4f4; -fx-padding: 20;");

        try {
            Image image = new Image(
                    getClass().getResourceAsStream("/me/filip_jakubowski/bytelab/schemat.png")
            );

            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);

            imageView.fitWidthProperty().bind(scene.widthProperty().subtract(40));
            imageView.fitHeightProperty().bind(scene.heightProperty().subtract(40));

            getChildren().add(imageView);

        } catch (Exception e) {
            getChildren().add(new Label("Błąd: Nie znaleziono pliku /me/filip_jakubowski/bytelab/schemat.png"));
        }
    }
}