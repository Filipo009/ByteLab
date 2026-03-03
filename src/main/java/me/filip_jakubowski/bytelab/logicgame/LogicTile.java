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
    private String inputA = null; // Stan górnego wejścia z magistrali
    private String inputB = null; // Stan dolnego wejścia z magistrali
    private boolean connectedToBus = false;

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
        setupText(input1Text, 10, 25);
        setupText(input2Text, 10, 85);
        setupText(stateText, 105, 51);
        stateText.setStyle("-fx-font-size: 38px; -fx-font-weight: bold;");

        overlays.getChildren().addAll(input1Text, input2Text, stateText);

        setAlignment(Pos.CENTER);
        getChildren().addAll(border, gateView, overlays);

        setupDragAndDrop();
    }

    // --- LOGIKA WEJŚĆ I OBLICZEŃ ---

    public void setInputA(String value) {
        this.inputA = value;
        updateUIForInput(input1Text, value);
        calculate();
    }

    public void setInputB(String value) {
        this.inputB = value;
        updateUIForInput(input2Text, value);
        calculate();
    }

    private void updateUIForInput(Text textNode, String value) {
        if (value == null) {
            textNode.setText("");
        } else {
            textNode.setText(value);
            textNode.setFill(value.equals("1") ? Color.web("#2ecc71") : Color.web("#ff4444"));
        }
    }

    public void calculate() {
        // Bramka liczy tylko gdy ma typ i oba wejścia (niezależnie czy z pinu czy magistrali)
        if (gateType == null || inputA == null || inputB == null) {
            stateText.setText("");
            updateBorderColor();
            return;
        }

        int in1 = Integer.parseInt(inputA);
        int in2 = Integer.parseInt(inputB);
        int result = 0;

        switch (gateType.toLowerCase()) {
            case "and" -> result = (in1 == 1 && in2 == 1) ? 1 : 0;
            case "or"  -> result = (in1 == 1 || in2 == 1) ? 1 : 0;
            case "xor" -> result = (in1 != in2) ? 1 : 0;
            case "nand" -> result = (in1 == 1 && in2 == 1) ? 0 : 1;
            case "nor"  -> result = (in1 == 0 && in2 == 0) ? 1 : 0;
        }

        stateText.setText(String.valueOf(result));
        stateText.setFill(result == 1 ? Color.web("#2ecc71") : Color.web("#ff4444"));
        updateBorderColor();
    }

    // --- OBSŁUGA DRAG & DROP ---

    private void setupDragAndDrop() {
        setOnDragOver(e -> {
            if (e.getDragboard().hasString()) e.acceptTransferModes(TransferMode.ANY);
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
            } else if (data.startsWith("PIN:") && gateType != null) {
                // Ręczne piny traktujemy tak samo jak sygnał z magistrali
                String val = data.split(":")[1];
                if (inputA == null) {
                    setInputA(val);
                    actionDone = true;
                } else if (inputB == null) {
                    setInputB(val);
                    actionDone = true;
                }
            }

            if (actionDone) {
                onStateChanged.accept(this);
            }
            e.setDropCompleted(true);
            e.consume();
        });
    }

    private void setGate(String type) {
        this.gateType = type;
        String path = "/me/filip_jakubowski/bytelab/" + type + ".png";
        InputStream is = getClass().getResourceAsStream(path);
        if (is != null) this.gateView.setImage(new Image(is));
        calculate(); // Przelicz, jeśli piny już tam były
    }

    // --- GETTERY I SETTERY ---

    private void updateBorderColor() {
        String currentStatus = stateText.getText();
        if ("1".equals(currentStatus)) border.setStroke(Color.web("#2ecc71"));
        else if ("0".equals(currentStatus)) border.setStroke(Color.web("#ff4444"));
        else border.setStroke(Color.web("#444"));
    }

    private void setupText(Text t, double x, double y) {
        t.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-fill: #aaa;");
        AnchorPane.setLeftAnchor(t, x);
        AnchorPane.setTopAnchor(t, y);
    }

    public void reset() {
        this.gateType = null;
        this.inputA = null;
        this.inputB = null;
        this.connectedToBus = false;
        this.gateView.setImage(null);
        this.stateText.setText("");
        this.input1Text.setText("");
        this.input2Text.setText("");
        updateBorderColor();
    }

    private boolean isTileFull() {
        return gateType != null && inputA != null && inputB != null;
    }

    public ImageView getGateView() {
        return gateView;
    }

    private boolean inputAOccupied = false;
    private boolean inputBOccupied = false;

    // Getter i Setter dla flag zajętości
    public boolean isInputAOccupied() { return inputAOccupied; }
    public void setInputAOccupied(boolean occupied) { this.inputAOccupied = occupied; }

    public boolean isInputBOccupied() { return inputBOccupied; }
    public void setInputBOccupied(boolean occupied) { this.inputBOccupied = occupied; }

    public String getInputA() { return inputA; }
    public String getInputB() { return inputB; }
    public String getSymbol() { return stateText.getText(); }
    public String getGateType() { return gateType; }
    public void setGateType(String type) { this.gateType = type; calculate(); }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public boolean isConnectedToBus() { return connectedToBus; }
    public void setConnectedToBus(boolean state) { this.connectedToBus = state; }
}