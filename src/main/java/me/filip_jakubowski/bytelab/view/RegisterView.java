package me.filip_jakubowski.bytelab.view;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RegisterView extends VBox {
    public RegisterView() {
        setStyle("-fx-background-color: #f7f7f7; -fx-padding: 10;");
        getChildren().add(new Label("Rejestry i pamięć RAM"));
    }
}
