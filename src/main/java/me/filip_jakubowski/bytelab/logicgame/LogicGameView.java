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

    private final LogicTile[][] tiles = new LogicTile[3][3];
    private final Label statusLabel = new Label();
    private boolean gameOver = false;
    private boolean isGreenTurn = true;

    public LogicGameView() {
        setPadding(new Insets(20));

        VBox leftToolbox = createToolbox("BRAMKI", new String[]{"and", "or", "xor", "nand", "nor"}, true);
        VBox rightToolbox = createToolbox("PINY", new String[]{"1", "0"}, false);

        setLeft(leftToolbox);
        setRight(rightToolbox);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                tiles[row][col] = new LogicTile(row, col, t -> onActionMade());
                grid.add(tiles[row][col], col, row);
            }
        }

        VBox centerArea = new VBox(20, statusLabel, grid);
        centerArea.setAlignment(Pos.CENTER);
        updateStatusStyle();
        setCenter(centerArea);

        Button resetButton = new Button("Resetuj Grę");
        resetButton.setOnAction(e -> resetGame());
        resetButton.setPrefWidth(120);

        Button backButton = new Button("Wyjdź do Menu");
        backButton.setOnAction(e -> MainApp.getNavigationManager().showStartScreen());
        backButton.setPrefWidth(120);

        HBox bottomActions = new HBox(20, resetButton, backButton);
        bottomActions.setAlignment(Pos.CENTER);
        bottomActions.setPadding(new Insets(20));
        setBottom(bottomActions);
    }

    public void resetGame() {
        gameOver = false;
        isGreenTurn = true;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                tiles[row][col].reset();
            }
        }
        updateStatusStyle();
    }

    private void onActionMade() {
        if (gameOver) return;

        checkWin(); // Sprawdź czy po tej akcji ktoś wygrał

        if (!gameOver) {
            isGreenTurn = !isGreenTurn; // Zmiana tury po KAŻDEJ akcji
            updateStatusStyle();
        }
    }

    private void updateStatusStyle() {
        statusLabel.setText("Tura Gracza: " + (isGreenTurn ? "1 (ZIELONY)" : "0 (CZERWONY)"));
        statusLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + (isGreenTurn ? "#2ecc71" : "#ff4444") + ";");
    }

    private void checkWin() {
        String winner = null;
        for (int i = 0; i < 3; i++) {
            if (checkLine(tiles[i][0], tiles[i][1], tiles[i][2])) winner = tiles[i][0].getSymbol();
            if (checkLine(tiles[0][i], tiles[1][i], tiles[2][i])) winner = tiles[0][i].getSymbol();
        }
        if (checkLine(tiles[0][0], tiles[1][1], tiles[2][2])) winner = tiles[0][0].getSymbol();
        if (checkLine(tiles[0][2], tiles[1][1], tiles[2][0])) winner = tiles[0][2].getSymbol();

        if (winner != null && !winner.isEmpty()) {
            gameOver = true;
            statusLabel.setText("WYGRAŁ GRACZ: " + winner);
            statusLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + (winner.equals("1") ? "#2ecc71" : "#ff4444") + ";");
        }
    }

    private boolean checkLine(LogicTile t1, LogicTile t2, LogicTile t3) {
        String s1 = t1.getSymbol();
        if (s1 == null || s1.isEmpty()) return false;
        return s1.equals(t2.getSymbol()) && s1.equals(t3.getSymbol());
    }

    // Pozostałe metody createToolbox, createGateIcon, createPinIcon pozostają bez zmian względem poprzedniej wersji
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
                ImageView icon = createGateIcon(item);
                if (icon != null) box.getChildren().add(icon);
            } else {
                box.getChildren().add(createPinIcon(item));
            }
        }
        return box;
    }

    private ImageView createGateIcon(String type) {
        String path = "/me/filip_jakubowski/bytelab/" + type + ".png";
        java.io.InputStream is = getClass().getResourceAsStream(path);
        if (is == null) return null;
        ImageView iv = new ImageView(new Image(is));
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
    }

    private Button createPinIcon(String val) {
        Button btn = new Button(val);
        btn.setPrefSize(60, 60);
        btn.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: " + (val.equals("1") ? "#27ae60" : "#c0392b"));
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