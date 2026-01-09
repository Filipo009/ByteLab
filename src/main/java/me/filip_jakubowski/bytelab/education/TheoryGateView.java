package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import java.io.InputStream;

public class TheoryGateView extends StackPane {
    private final ImageView gateView = new ImageView();
    private final Text stateText = new Text("0");
    private final Text input1Text = new Text("0");
    private final Text input2Text = new Text("0");
    private final Rectangle border = new Rectangle(130, 130);

    private String gateType;
    private int in1 = 0;
    private int in2 = 0;

    public TheoryGateView(String type) {
        this.gateType = type.toLowerCase();

        // Rozmiar całego komponentu
        setMaxSize(130, 130);
        setMinSize(130, 130);

        border.setFill(Color.web("#2d2d2d"));
        border.setStroke(Color.web("#444"));
        border.setStrokeWidth(3);
        border.setArcWidth(10);
        border.setArcHeight(10);

        gateView.setFitWidth(80);
        gateView.setPreserveRatio(true);
        loadGateImage();

        // Nakładka o stałym rozmiarze, by teksty były zawsze na bramce
        AnchorPane overlays = new AnchorPane();
        overlays.setMaxSize(130, 130);
        overlays.setPrefSize(130, 130);

        // Zmienione pozycje tekstów
        setupText(input1Text, 10, 25);
        setupText(input2Text, 10, 85);
        setupText(stateText, 105, 51);
        stateText.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-cursor: default;");

        input1Text.setOnMouseClicked(e -> { in1 = 1 - in1; update(); });
        input2Text.setOnMouseClicked(e -> { in2 = 1 - in2; update(); });

        overlays.getChildren().addAll(input1Text, input2Text, stateText);

        setAlignment(Pos.CENTER);
        getChildren().addAll(border, gateView, overlays);

        update();
    }

    private void setupText(Text t, double x, double y) {
        t.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #aaa; -fx-cursor: hand;");
        AnchorPane.setLeftAnchor(t, x);
        AnchorPane.setTopAnchor(t, y);
    }

    private void loadGateImage() {
        String path = "/me/filip_jakubowski/bytelab/" + gateType + ".png";
        InputStream is = getClass().getResourceAsStream(path);
        if (is != null) gateView.setImage(new Image(is));
    }

    private void update() {
        input1Text.setText(String.valueOf(in1));
        input1Text.setFill(in1 == 1 ? Color.web("#2ecc71") : Color.web("#ff4444"));
        input2Text.setText(String.valueOf(in2));
        input2Text.setFill(in2 == 1 ? Color.web("#2ecc71") : Color.web("#ff4444"));

        int result = switch (gateType) {
            case "and" -> (in1 == 1 && in2 == 1) ? 1 : 0;
            case "or" -> (in1 == 1 || in2 == 1) ? 1 : 0;
            case "xor" -> (in1 != in2) ? 1 : 0;
            case "nand" -> (in1 == 1 && in2 == 1) ? 0 : 1;
            case "nor" -> (in1 == 0 && in2 == 0) ? 1 : 0;
            default -> 0;
        };

        stateText.setText(String.valueOf(result));
        stateText.setFill(result == 1 ? Color.web("#2ecc71") : Color.web("#ff4444"));
        border.setStroke(result == 1 ? Color.web("#2ecc71") : Color.web("#ff4444"));
    }
}