package me.filip_jakubowski.bytelab.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.Scene;

public class DiagramView extends StackPane {

    private final ImageView imageView;

    public DiagramView(Scene scene) {
        setStyle("-fx-background-color: #e6f0ff; -fx-padding: 10;");

        ImageView tempView = null;

        try {
            Image image = new Image(
                    getClass().getResourceAsStream("/me/filip_jakubowski/bytelab/schemat.png")
            );

            tempView = new ImageView(image);
            tempView.setPreserveRatio(true);

            // skalowanie względem całego okna – 60% jego rozmiaru
            tempView.fitWidthProperty().bind(scene.widthProperty().multiply(0.6));
            tempView.fitHeightProperty().bind(scene.heightProperty().multiply(0.6));

            getChildren().add(tempView);

        } catch (Exception e) {
            getChildren().add(new Label("Nie znaleziono pliku schemat.png"));
            e.printStackTrace();
        }

        imageView = tempView;
    }
}
