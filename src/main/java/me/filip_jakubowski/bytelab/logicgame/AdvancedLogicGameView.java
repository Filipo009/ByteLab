package me.filip_jakubowski.bytelab.logicgame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import me.filip_jakubowski.bytelab.MainApp;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdvancedLogicGameView extends BorderPane {

    private final LogicTile[][] tiles = new LogicTile[3][3];
    private final Label statusLabel = new Label();
    private final BusManager busManager = new BusManager();
    private final Pane matrixLayer = new Pane();
    private final Pane connectionLayer = new Pane();

    private boolean gameOver = false;
    private boolean isGreenTurn = true;

    private final double slotSize = 140;
    private final double gap = 120;
    private final double spacing = 10;
    private final double startCoord = 60;

    private final Map<LogicTile, Integer> tileToBit = new HashMap<>();
    private final Set<Integer> occupiedBusBits = new HashSet<>();

    public AdvancedLogicGameView() {
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #1e1e1e;");

        setLeft(createToolbox("BRAMKI LOGICZNE", new String[]{"and", "or", "xor", "nand", "nor"}, true));

        VBox rightSidebar = new VBox(20);
        rightSidebar.getChildren().addAll(createInputControlPanel(), createToolbox("WARTOŚCI", new String[]{"1", "0"}, false));
        setRight(rightSidebar);

        Group gameGroup = new Group();
        matrixLayer.setPickOnBounds(false);
        connectionLayer.setPickOnBounds(false);

        drawGridMatrix();
        setupComponents(matrixLayer);

        gameGroup.getChildren().addAll(matrixLayer, connectionLayer);

        StackPane centerContainer = new StackPane(gameGroup);
        centerContainer.setAlignment(Pos.CENTER);
        centerContainer.setPadding(new Insets(50));
        centerContainer.setStyle("-fx-background-color: #1e1e1e;");

        // ScrollPane rozwiązuje problem znikania przycisków na małych ekranach
        ScrollPane scrollPane = new ScrollPane(centerContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #1e1e1e; -fx-border-color: #1e1e1e;");

        VBox centerArea = new VBox(10, statusLabel, scrollPane);
        centerArea.setAlignment(Pos.CENTER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        updateStatusStyle();
        setCenter(centerArea);
        setupFooter(); // Teraz na pewno będzie widoczne na dole BorderPane
    }

    private void drawGridMatrix() {
        double bundleWidth = 8 * spacing;
        double[] nodeCoords = new double[4];
        for (int i = 0; i < 4; i++) {
            nodeCoords[i] = startCoord + (i * (slotSize + gap)) - (gap / 2);
        }

        // rangeStart i rangeEnd teraz kończą się równo z ostatnią linią bitu
        double rangeStart = nodeCoords[0] - (bundleWidth / 2);
        double rangeEnd = nodeCoords[3] + (bundleWidth / 2);

        for (int bundleIdx = 0; bundleIdx < 4; bundleIdx++) {
            double bundlePos = nodeCoords[bundleIdx];
            for (int bit = 0; bit < 9; bit++) {
                double offset = bundlePos - (bundleWidth / 2) + ((8 - bit) * spacing);

                // Linie magistrali (przycięte do krawędzi)
                Line hLine = new Line(rangeStart, offset, rangeEnd, offset);
                Line vLine = new Line(offset, rangeStart, offset, rangeEnd);

                setupBusSource(hLine, bit);
                setupBusSource(vLine, bit);
                setupBusInteractions(hLine, bit);
                setupBusInteractions(vLine, bit);
                applyBusStyle(hLine, bit);
                applyBusStyle(vLine, bit);

                matrixLayer.getChildren().addAll(hLine, vLine);

                for (int targetBundle = 0; targetBundle < 4; targetBundle++) {
                    double crossY = nodeCoords[targetBundle] - (bundleWidth / 2) + ((8 - bit) * spacing);
                    Circle dot = new Circle(offset, crossY, 2.5);
                    styleDot(dot, bit);
                    matrixLayer.getChildren().add(dot);
                }
            }
        }
    }

    private void setupFooter() {
        Button btnReset = new Button("RESETUJ GRĘ");
        btnReset.setPrefWidth(150);
        btnReset.setStyle("-fx-background-color: #007acc; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;");
        btnReset.setOnAction(e -> MainApp.getNavigationManager().showAdvancedLogicGame());

        Button btnMenu = new Button("MENU");
        btnMenu.setPrefWidth(150);
        btnMenu.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-cursor: hand;");
        btnMenu.setOnAction(e -> MainApp.getNavigationManager().showStartScreen());

        HBox footer = new HBox(30, btnReset, btnMenu);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: #252526; -fx-border-color: #444; -fx-border-width: 1 0 0 0;");

        this.setBottom(footer);
    }

    // --- Reszta metod Drag & Drop oraz Logiki (bez zmian w logice) ---

    private void setupBusSource(Line line, int bitIdx) {
        line.setOnDragDetected(e -> {
            Dragboard db = line.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString("BUS_BIT:" + bitIdx);
            db.setContent(content);
            e.consume();
        });
    }

    private void setupComponents(Pane parentPane) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                LogicTile tile = new LogicTile(r, c, t -> onActionMade());
                tiles[r][c] = tile;
                tile.setLayoutX(startCoord + c * (slotSize + gap));
                tile.setLayoutY(startCoord + r * (slotSize + gap));

                tile.setOnDragOver(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasString()) {
                        String data = db.getString();
                        if (data.startsWith("GATE:") && tile.getGateType() == null) {
                            e.acceptTransferModes(TransferMode.COPY);
                        } else if ((data.startsWith("BUS_BIT:") || data.startsWith("PIN:")) && tile.getGateType() != null) {
                            e.acceptTransferModes(TransferMode.ANY);
                        }
                    }
                    e.consume();
                });

                tile.setOnDragDropped(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasString()) {
                        String data = db.getString();
                        boolean actionDone = false;
                        if (data.startsWith("GATE:")) {
                            tile.setGateType(data.split(":")[1]);
                            String path = "/me/filip_jakubowski/bytelab/" + data.split(":")[1] + ".png";
                            InputStream is = getClass().getResourceAsStream(path);
                            if (is != null) tile.getGateView().setImage(new Image(is));
                            actionDone = true;
                        } else if (data.startsWith("BUS_BIT:")) {
                            int bitIdx = Integer.parseInt(data.split(":")[1]);
                            connectBusToTileInput(tile, bitIdx);
                            actionDone = true;
                        } else if (data.startsWith("PIN:")) {
                            String val = data.split(":")[1];
                            if (!tile.isInputAOccupied()) {
                                tile.setInputAOccupied(true);
                                tile.setInputA(val);
                                actionDone = true;
                            } else if (!tile.isInputBOccupied()) {
                                tile.setInputBOccupied(true);
                                tile.setInputB(val);
                                actionDone = true;
                            }
                        }
                        if (actionDone) onActionMade();
                        e.setDropCompleted(true);
                    }
                    e.consume();
                });

                tile.setOnDragDetected(e -> {
                    if (!tile.isConnectedToBus() && tile.getSymbol() != null && !tile.getSymbol().isEmpty()) {
                        Dragboard db = tile.startDragAndDrop(TransferMode.LINK);
                        ClipboardContent content = new ClipboardContent();
                        content.putString("OUT:" + tile.getRow() + ":" + tile.getCol());
                        db.setContent(content);
                    }
                    e.consume();
                });
                parentPane.getChildren().add(tile);
            }
        }
    }

    private void connectBusToTileInput(LogicTile tile, int bitIdx) {
        Integer val = busManager.getBusState(bitIdx);
        String stringVal = (val == null) ? null : String.valueOf(val);

        if (!tile.isInputAOccupied()) {
            tile.setInputAOccupied(true);
            tile.setInputA(stringVal);
            createInputWire(tile, bitIdx, true);
        } else if (!tile.isInputBOccupied()) {
            tile.setInputBOccupied(true);
            tile.setInputB(stringVal);
            createInputWire(tile, bitIdx, false);
        } else return;

        onActionMade();
    }

    private void createInputWire(LogicTile tile, int bitIdx, boolean isTopPin) {
        double endX = tile.getLayoutX();
        double endY = tile.getLayoutY() + (isTopPin ? slotSize * 0.3 : slotSize * 0.7);
        double bundleWidth = 8 * spacing;
        double startX = -1;

        for (int i = 3; i >= 0; i--) {
            double bundlePos = startCoord + (i * (slotSize + gap)) - (gap / 2);
            double busX = bundlePos - (bundleWidth / 2) + ((8 - bitIdx) * spacing);
            if (busX < endX) { startX = busX; break; }
        }
        if (startX == -1) startX = endX - 30;

        Polyline wire = new Polyline(startX, endY, endX, endY);
        wire.setStrokeWidth(3.0);
        Circle jointDot = new Circle(startX, endY, 4);
        Group inputGroup = new Group(wire, jointDot);

        busManager.addListener((idx, val) -> {
            if (idx == bitIdx) {
                Color c = (val == null) ? Color.web("#333") : (val == 1 ? Color.web("#2ecc71") : Color.web("#ff4444"));
                wire.setStroke(c);
                jointDot.setFill(c);
                String sVal = (val == null) ? null : String.valueOf(val);
                if (isTopPin) tile.setInputA(sVal); else tile.setInputB(sVal);
            }
        });
        connectionLayer.getChildren().add(inputGroup);
    }

    private void createConnection(LogicTile tile, int bitIdx) {
        double startX = tile.getLayoutX() + slotSize;
        double startY = tile.getLayoutY() + (slotSize / 2);
        double bundleWidth = 8 * spacing;
        double targetX = -1;

        for (int i = 0; i < 4; i++) {
            double bundlePos = startCoord + (i * (slotSize + gap)) - (gap / 2);
            double busX = bundlePos - (bundleWidth / 2) + ((8 - bitIdx) * spacing);
            if (busX > startX) { targetX = busX; break; }
        }
        if (targetX == -1) targetX = startX + 40;

        Polyline wire = new Polyline(startX, startY, targetX, startY);
        wire.setStrokeWidth(4.0);
        Circle jointDot = new Circle(targetX, startY, 4.5);
        tileToBit.put(tile, bitIdx);

        busManager.addListener((idx, val) -> {
            if (idx == bitIdx) {
                Color c = (val == null) ? Color.web("#333") : (val == 1 ? Color.web("#2ecc71") : Color.web("#ff4444"));
                wire.setStroke(c);
                jointDot.setFill(c);
            }
        });
        connectionLayer.getChildren().add(new Group(wire, jointDot));
        refreshLogicNetwork();
    }

    private void refreshLogicNetwork() {
        tileToBit.forEach((tile, bitIdx) -> {
            String sym = tile.getSymbol();
            if (sym != null && !sym.isEmpty()) {
                busManager.setBusState(bitIdx, sym.equals("1") ? 1 : 0);
            }
        });
    }

    private void onActionMade() {
        if (!gameOver) {
            refreshLogicNetwork();
            checkWin();
            if (!gameOver) {
                isGreenTurn = !isGreenTurn;
                updateStatusStyle();
            }
        }
    }

    private void setupBusInteractions(Line line, int bitIdx) {
        line.setPickOnBounds(true);
        line.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString() && db.getString().startsWith("OUT:")) {
                if (bitIdx > 3 && !occupiedBusBits.contains(bitIdx)) e.acceptTransferModes(TransferMode.LINK);
            }
            e.consume();
        });
        line.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasString() && db.getString().startsWith("OUT:")) {
                String[] p = db.getString().split(":");
                LogicTile tile = tiles[Integer.parseInt(p[1])][Integer.parseInt(p[2])];
                if (!tile.isConnectedToBus() && !occupiedBusBits.contains(bitIdx)) {
                    createConnection(tile, bitIdx);
                    tile.setConnectedToBus(true);
                    occupiedBusBits.add(bitIdx);
                    e.setDropCompleted(true);
                }
            }
            e.consume();
        });
    }

    private void applyBusStyle(Line line, int bitIdx) {
        line.setStrokeWidth(2.5); line.setStroke(Color.web("#333"));
        busManager.addListener((idx, val) -> {
            if (idx == bitIdx) line.setStroke(val == null ? Color.web("#333") : (val == 1 ? Color.web("#2ecc71") : Color.web("#ff4444")));
        });
    }

    private void styleDot(Circle dot, int bitIdx) {
        dot.setFill(Color.web("#333"));
        busManager.addListener((idx, val) -> {
            if (idx == bitIdx) dot.setFill(val == null ? Color.web("#333") : (val == 1 ? Color.web("#2ecc71") : Color.web("#ff4444")));
        });
    }

    private VBox createInputControlPanel() {
        VBox panel = new VBox(10); panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #252526; -fx-border-color: #444; -fx-border-radius: 5;");
        Label title = new Label("KONTROLA WEJŚĆ (0-3)");
        title.setStyle("-fx-text-fill: #007acc; -fx-font-weight: bold;");
        panel.getChildren().add(title);
        for (int i = 0; i < 4; i++) {
            final int bitIdx = i;
            HBox row = new HBox(8); row.setAlignment(Pos.CENTER_LEFT);
            Label lbl = new Label("BIT " + i); lbl.setStyle("-fx-text-fill: #aaa; -fx-min-width: 35;");
            Button b0 = new Button("0"); b0.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
            Button b1 = new Button("1"); b1.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
            b0.setOnAction(e -> handleBusAction(bitIdx, 0));
            b1.setOnAction(e -> handleBusAction(bitIdx, 1));
            row.getChildren().addAll(lbl, b0, b1);
            panel.getChildren().add(row);
        }
        return panel;
    }

    private VBox createToolbox(String title, String[] items, boolean isGate) {
        VBox box = new VBox(10); box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: #252526; -fx-border-color: #333; -fx-border-radius: 5;");
        Label l = new Label(title); l.setStyle("-fx-text-fill: #007acc; -fx-font-weight: bold;");
        box.getChildren().add(l);
        for (String item : items) {
            if (isGate) box.getChildren().add(createGateIcon(item));
            else box.getChildren().add(createPinIcon(item));
        }
        return box;
    }

    private void handleBusAction(int bitIdx, int value) {
        if (!gameOver) {
            busManager.setBusState(bitIdx, value);
            refreshLogicNetwork();
            onActionMade();
        }
    }

    private void updateStatusStyle() {
        statusLabel.setText("Tura: " + (isGreenTurn ? "ZIELONY" : "CZERWONY"));
        statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + (isGreenTurn ? "#2ecc71" : "#ff4444"));
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
            boolean isOne = winner.equals("1");
            statusLabel.setText("WYGRANA: " + (isOne ? "JEDYNKI (ZIELONY)" : "ZERA (CZERWONY)"));
            statusLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + (isOne ? "#2ecc71" : "#ff4444"));
        }
    }

    private boolean checkLine(LogicTile t1, LogicTile t2, LogicTile t3) {
        String s1 = t1.getSymbol();
        return s1 != null && !s1.isEmpty() && s1.equals(t2.getSymbol()) && s1.equals(t3.getSymbol());
    }

    private Button createPinIcon(String val) {
        Button btn = new Button(val); btn.setPrefSize(45, 45);
        btn.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: " + (val.equals("1") ? "#27ae60" : "#c0392b"));
        btn.setOnDragDetected(e -> {
            Dragboard db = btn.startDragAndDrop(TransferMode.COPY);
            ClipboardContent c = new ClipboardContent(); c.putString("PIN:" + val); db.setContent(c);
            e.consume();
        });
        return btn;
    }

    private ImageView createGateIcon(String type) {
        String path = "/me/filip_jakubowski/bytelab/" + type + ".png";
        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(path)));
        iv.setFitWidth(60); iv.setPreserveRatio(true);
        iv.setOnDragDetected(e -> {
            Dragboard db = iv.startDragAndDrop(TransferMode.COPY);
            ClipboardContent c = new ClipboardContent(); c.putString("GATE:" + type); db.setContent(c);
            e.consume();
        });
        return iv;
    }
}