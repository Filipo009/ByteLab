package me.filip_jakubowski.bytelab.logicgame;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import javafx.geometry.Pos;

public class LogicTile extends StackPane {
    private final int row, col;
    private final ImageView gateView = new ImageView();
    private final Text stateText = new Text();
    private final Rectangle border = new Rectangle(130, 130);

    private String gateType = null;
    private int inputLeft = 0;
    private int inputTop = 0;

    public LogicTile(int row, int col) {
        this.row = row;
        this.col = col;

        border.setFill(Color.web("#2d2d2d"));
        border.setStroke(Color.web("#444"));
        border.setStrokeWidth(3);
        border.setArcWidth(10);
        border.setArcHeight(10);

        gateView.setFitWidth(90);
        gateView.setPreserveRatio(true);

        stateText.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        setAlignment(Pos.CENTER);
        getChildren().addAll(border, gateView, stateText);

        setupDragAndDrop();
    }

    private void setupDragAndDrop() {
        setOnDragOver(e -> {
            if (e.getDragboard().hasString()) e.acceptTransferModes(TransferMode.COPY);
            e.consume();
        });

        setOnDragEntered(e -> border.setStroke(Color.web("#007acc")));
        setOnDragExited(e -> updateBorderColor());

        setOnDragDropped(e -> {
            String data = e.getDragboard().getString();
            if (data.startsWith("GATE:")) {
                setGate(data.split(":")[1]);
            } else if (data.startsWith("PIN:")) {
                inputLeft = Integer.parseInt(data.split(":")[1]);
                calculate();
            }
            e.setDropCompleted(true);
            e.consume();
        });
    }

    private void setGate(String type) {
        this.gateType = type;
        try {
            this.gateView.setImage(new Image(getClass().getResourceAsStream("/me/filip_jakubowski/bytelab/images/" + type + ".png")));
        } catch (Exception ignored) {}
        calculate();
    }

    private void calculate() {
        if (gateType == null) return;

        int result = 0;
        switch (gateType) {
            case "and" -> result = (inputLeft == 1 && inputTop == 1) ? 1 : 0;
            case "or" -> result = (inputLeft == 1 || inputTop == 1) ? 1 : 0;
            case "xor" -> result = (inputLeft != inputTop) ? 1 : 0;
            case "nand" -> result = (inputLeft == 1 && inputTop == 1) ? 0 : 1;
            case "nor" -> result = (inputLeft == 0 && inputTop == 0) ? 1 : 0;
        }

        stateText.setText(result == 1 ? "X" : "O");
        stateText.setFill(result == 1 ? Color.web("#ff4444") : Color.web("#4444ff"));
        updateBorderColor();
    }

    private void updateBorderColor() {
        if ("X".equals(stateText.getText())) border.setStroke(Color.web("#ff4444"));
        else if ("O".equals(stateText.getText())) border.setStroke(Color.web("#4444ff"));
        else border.setStroke(Color.web("#444"));
    }
}