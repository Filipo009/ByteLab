package me.filip_jakubowski.bytelab.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;

public class DiagramView extends StackPane {

    public DiagramView() {
        setStyle("-fx-background-color: #e6f0ff; -fx-padding: 10;");

        try {
            Image image = new Image(
                    getClass().getResourceAsStream("/me/filip_jakubowski/bytelab/schemat.png")
            );

            ImageView imageView = new ImageView(image);

            imageView.setPreserveRatio(true);


            getChildren().add(imageView);

        } catch (Exception e) {
            // Gdyby plik nie został znaleziony, wyświetl komunikat
            getChildren().add(new Label("Nie znaleziono pliku schemat.png"));
            e.printStackTrace();
        }
    }
}
