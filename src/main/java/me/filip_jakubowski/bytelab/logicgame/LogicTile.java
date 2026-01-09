package me.filip_jakubowski.bytelab.logicgame;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import java.io.InputStream;
import java.util.function.Consumer;

public class LogicTile extends StackPane {
    private final int row, col;
    private final ImageView gateView = new ImageView();
    private final Text stateText = new Text();
    private final Text input1Text = new Text();
    private final Text input2Text = new Text();
    private final Rectangle border = new Rectangle(130, 130);

    private String gateType = null;
    private Integer in1 = null;
    private Integer in2 = null;
    private final Consumer<LogicTile> onStateChanged;

    public LogicTile(int row, int col, Consumer<LogicTile> onStateChanged) {
        this.row = row;
        this.col = col;
        this.onStateChanged = onStateChanged;

        border.setFill(Color.web("#2d2d2d"));
        border.setStroke(Color.web("#444"));
        border.setStrokeWidth(3);
        border.setArcWidth(10);
        border.setArcHeight(10);

        gateView.setFitWidth(80);
        gateView.setPreserveRatio(true);

        AnchorPane overlays = new AnchorPane();
        setupText(input1Text, 10, 10);
        setupText(input2Text, 10, 85);
        setupText(stateText, 95, 45);
        stateText.setStyle("-fx-font-size: 38px; -fx-font-weight: bold;");

        overlays.getChildren().addAll(input1Text, input2Text, stateText);

        setAlignment(Pos.CENTER);
        getChildren().addAll(border, gateView, overlays);

        setupDragAndDrop();
    }

    public void reset() {
        this.gateType = null;
        this.in1 = null;
        this.in2 = null;
        this.gateView.setImage(null);
        this.stateText.setText("");
        this.input1Text.setText("");
        this.input2Text.setText("");
        updateBorderColor();
    }

    private void setupText(Text t, double x, double y) {
        t.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #aaa;");
        AnchorPane.setLeftAnchor(t, x);
        AnchorPane.setTopAnchor(t, y);
    }

    private void setupDragAndDrop() {
        setOnDragOver(e -> {
            if (e.getDragboard().hasString()) e.acceptTransferModes(TransferMode.COPY);
            e.consume();
        });

        setOnDragEntered(e -> {
            if (!isTileFull()) border.setStroke(Color.web("#007acc"));
        });
        setOnDragExited(e -> updateBorderColor());

        setOnDragDropped(e -> {
            String data = e.getDragboard().getString();
            boolean actionDone = false;

            if (data.startsWith("GATE:") && gateType == null) {
                setGate(data.split(":")[1]);
                actionDone = true;
            } else if (data.startsWith("PIN:") && gateType != null && in2 == null) {
                setPinValue(Integer.parseInt(data.split(":")[1]));
                actionDone = true;
            }

            if (actionDone) {
                onStateChanged.accept(this); // Każda akcja zmienia turę
            }
            e.setDropCompleted(true);
            e.consume();
        });
    }

    private boolean isTileFull() {
        return gateType != null && in1 != null && in2 != null;
    }

    private void setGate(String type) {
        this.gateType = type;
        String path = "/me/filip_jakubowski/bytelab/" + type + ".png";
        InputStream is = getClass().getResourceAsStream(path);
        if (is != null) this.gateView.setImage(new Image(is));
    }

    private void setPinValue(int val) {
        if (in1 == null) {
            in1 = val;
            input1Text.setText(String.valueOf(val));
            input1Text.setFill(val == 1 ? Color.web("#2ecc71") : Color.web("#ff4444"));
        } else if (in2 == null) {
            in2 = val;
            input2Text.setText(String.valueOf(val));
            input2Text.setFill(val == 1 ? Color.web("#2ecc71") : Color.web("#ff4444"));
            calculate();
        }
    }

    private void calculate() {
        int result = 0;
        switch (gateType) {
            case "and" -> result = (in1 == 1 && in2 == 1) ? 1 : 0;
            case "or" -> result = (in1 == 1 || in2 == 1) ? 1 : 0;
            case "xor" -> result = (in1 != in2) ? 1 : 0;
            case "nand" -> result = (in1 == 1 && in2 == 1) ? 0 : 1;
            case "nor" -> result = (in1 == 0 && in2 == 0) ? 1 : 0;
        }

        stateText.setText(String.valueOf(result)); // Zamiast X/O dajemy 1/0
        stateText.setFill(result == 1 ? Color.web("#2ecc71") : Color.web("#ff4444"));
        updateBorderColor();
    }

    private void updateBorderColor() {
        if ("1".equals(stateText.getText())) border.setStroke(Color.web("#2ecc71"));
        else if ("0".equals(stateText.getText())) border.setStroke(Color.web("#ff4444"));
        else border.setStroke(Color.web("#444"));
    }

    public String getSymbol() { return stateText.getText(); }
}