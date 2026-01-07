package me.filip_jakubowski.bytelab.logicgame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import me.filip_jakubowski.bytelab.MainApp;

public class LogicGameView extends BorderPane {

    private final GridPane grid = new GridPane();

    public LogicGameView() {
        setPadding(new Insets(20));

        VBox leftToolbox = createToolbox("BRAMKI", new String[]{"and", "or", "xor", "nand", "nor"}, true);
        VBox rightToolbox = createToolbox("PINY", new String[]{"0", "1"}, false);

        setLeft(leftToolbox);
        setRight(rightToolbox);

        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                grid.add(new LogicTile(row, col), col, row);
            }
        }
        setCenter(grid);

        Button back = new Button("Wyjdź do Menu");
        back.setOnAction(e -> MainApp.getNavigationManager().showStartScreen());
        HBox bottom = new HBox(back);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(20));
        setBottom(bottom);
    }

    private VBox createToolbox(String title, String[] items, boolean isGate) {
        VBox box = new VBox(15);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.TOP_CENTER);
        box.setMinWidth(150);
        box.setStyle("-fx-background-color: #252526; -fx-border-color: #333; -fx-border-radius: 5;");

        Label label = new Label(title);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #007acc;");
        box.getChildren().add(label);

        for (String item : items) {
            if (isGate) {
                box.getChildren().add(createGateIcon(item));
            } else {
                box.getChildren().add(createPinIcon(item));
            }
        }
        return box;
    }

    private ImageView createGateIcon(String type) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/me/filip_jakubowski/bytelab/images/" + type + ".png"));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(80);
            iv.setPreserveRatio(true);
            iv.setStyle("-fx-cursor: hand;");

            iv.setOnDragDetected(e -> {
                Dragboard db = iv.startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.putString("GATE:" + type);
                db.setContent(content);
                e.consume();
            });
            return iv;
        } catch (Exception e) {
            return new ImageView();
        }
    }

    private Button createPinIcon(String val) {
        Button btn = new Button(val);
        btn.setPrefSize(60, 60);
        btn.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-background-color: " + (val.equals("1") ? "#cc3333" : "#3366cc"));

        btn.setOnDragDetected(e -> {
            Dragboard db = btn.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString("PIN:" + val);
            db.setContent(content);
            e.consume();
        });
        return btn;
    }
}