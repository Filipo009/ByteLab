package me.filip_jakubowski.bytelab.logicgame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import me.filip_jakubowski.bytelab.MainApp;

public class AdvancedLogicGameView extends BorderPane {

    private final LogicTile[][] tiles = new LogicTile[3][3];
    private final Label statusLabel = new Label();
    private final BusManager busManager = new BusManager();
    private final Pane matrixLayer = new Pane();
    private boolean gameOver = false;
    private boolean isGreenTurn = true;

    public AdvancedLogicGameView() {
        setPadding(new Insets(10));
        matrixLayer.setMouseTransparent(true);

        // Panele boczne
        setLeft(createToolbox("BRAMKI", new String[]{"and", "or", "xor", "nand", "nor"}, true));
        setRight(createToolbox("PINY", new String[]{"1", "0"}, false));

        StackPane gameStack = new StackPane();
        gameStack.setAlignment(Pos.CENTER);

        // 1. Rysujemy siatkę gniazd (każde z 9-bitową magistralą)
        drawGridMatrix();

        // 2. Rozmieszczamy kafelki i switche
        GridPane componentGrid = new GridPane();
        componentGrid.setAlignment(Pos.CENTER);
        componentGrid.setHgap(40); // Odstęp między gniazdami
        componentGrid.setVgap(40);

        setupComponents(componentGrid);

        gameStack.getChildren().addAll(matrixLayer, componentGrid);

        VBox centerArea = new VBox(15, statusLabel, gameStack);
        centerArea.setAlignment(Pos.CENTER);
        updateStatusStyle();
        setCenter(centerArea);

        setupFooter();
    }

    private void drawGridMatrix() {
        double slotSize = 130;
        double gap = 80;
        double spacing = 5;
        double startCoord = 120;

        double bundleWidth = 8 * spacing;

        // 1. LINIE POZIOME (H-Bus)
        for (int bundle = 0; bundle < 4; bundle++) {
            double bundleCenterY = startCoord + (bundle * (slotSize + gap)) - (gap / 2);

            for (int bit = 0; bit < 9; bit++) {
                double y = bundleCenterY - (bundleWidth / 2) + (bit * spacing);

                // Punkt startowy to X pierwszej pionowej wiązki dla tego bitu
                double hStart = (startCoord + 0 * (slotSize + gap) - gap/2) - (bundleWidth/2) + (bit * spacing);
                // Punkt końcowy to X ostatniej (czwartej) pionowej wiązki dla tego bitu
                double hEnd = (startCoord + 3 * (slotSize + gap) - gap/2) - (bundleWidth/2) + (bit * spacing);

                Line hLine = new Line(hStart, y, hEnd, y);
                applyBusStyle(hLine, bit);
                matrixLayer.getChildren().add(hLine);
            }
        }

        // 2. LINIE PIONOWE (V-Bus)
        for (int bundle = 0; bundle < 4; bundle++) {
            double bundleCenterX = startCoord + (bundle * (slotSize + gap)) - (gap / 2);

            for (int bit = 0; bit < 9; bit++) {
                double x = bundleCenterX - (bundleWidth / 2) + (bit * spacing);

                // Punkt startowy to Y pierwszej poziomej wiązki dla tego bitu
                double vStart = (startCoord + 0 * (slotSize + gap) - gap/2) - (bundleWidth/2) + (bit * spacing);
                // Punkt końcowy to Y ostatniej poziomej wiązki dla tego bitu
                double vEnd = (startCoord + 3 * (slotSize + gap) - gap/2) - (bundleWidth/2) + (bit * spacing);

                Line vLine = new Line(x, vStart, x, vEnd);
                applyBusStyle(vLine, bit);
                matrixLayer.getChildren().add(vLine);

                // 3. KROPKI (Na przecięciach tych samych bitów)
                for (int hBundle = 0; hBundle < 4; hBundle++) {
                    double hCenterY = startCoord + (hBundle * (slotSize + gap)) - (gap / 2);
                    double hY = hCenterY - (bundleWidth / 2) + (bit * spacing);

                    Circle dot = new Circle(x, hY, 2.5);
                    styleDot(dot, bit);
                    matrixLayer.getChildren().add(dot);
                }
            }
        }
    }

    private void styleDot(Circle dot, int bitIdx) {
        dot.setFill(Color.web("#333"));
        busManager.addListener((idx, val) -> {
            if (idx == bitIdx) {
                dot.setFill(val == null ? Color.web("#333") :
                        (val == 1 ? Color.web("#2ecc71") : Color.web("#ff4444")));
            }
        });
    }

    private void applyBusStyle(Line line, int bitIdx) {
        line.setStrokeWidth(1.5); // Cieńsze linie, by 9 zmieściło się estetycznie
        line.setStroke(Color.web("#333"));
        busManager.addListener((idx, val) -> {
            if (idx == bitIdx) {
                line.setStroke(val == null ? Color.web("#333") :
                        (val == 1 ? Color.web("#2ecc71") : Color.web("#ff4444")));
            }
        });
    }

    private void setupComponents(GridPane grid) {
        // Kafelki bramek wewnątrz gniazd
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                tiles[r][c] = new LogicTile(r, c, t -> onActionMade());
                grid.add(tiles[r][c], c + 1, r + 1);
            }
        }
        // Switche w krzyżu (Góra, Lewo, Prawo, Dół)
        grid.add(createWorldSwitch(0), 2, 0);
        grid.add(createWorldSwitch(1), 0, 2);
        grid.add(createWorldSwitch(2), 4, 2);
        grid.add(createWorldSwitch(3), 2, 4);
    }

    private VBox createWorldSwitch(int id) {
        VBox swBox = new VBox(2);
        swBox.setAlignment(Pos.CENTER);
        Button btn = new Button("SW " + id);
        btn.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
        ComboBox<Integer> bitSel = new ComboBox<>();
        for(int i=0; i<9; i++) bitSel.getItems().add(i);
        bitSel.setPromptText("BIT");
        bitSel.setPrefWidth(70);

        btn.setOnAction(e -> {
            Integer bit = bitSel.getValue();
            if (bit != null && !gameOver) {
                Integer curr = busManager.getBusState(bit);
                int next = (curr == null || curr == 0) ? 1 : 0;
                busManager.setBusState(bit, next);
                btn.setStyle("-fx-background-color: " + (next == 1 ? "#2ecc71" : "#ff4444") + "; -fx-text-fill: white;");
                onActionMade();
            }
        });
        swBox.getChildren().addAll(btn, bitSel);
        return swBox;
    }

    // --- TOOLBOX I LOGIKA (createToolbox, checkWin, etc. jak wcześniej) ---

    private VBox createToolbox(String title, String[] items, boolean isGate) {
        VBox box = new VBox(15);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: #252526; -fx-border-color: #333; -fx-border-radius: 5;");
        Label l = new Label(title);
        l.setStyle("-fx-text-fill: #007acc; -fx-font-weight: bold;");
        box.getChildren().addAll(l);
        for (String item : items) {
            if (isGate) {
                ImageView iv = createGateIcon(item);
                if (iv != null) box.getChildren().add(iv);
            } else {
                box.getChildren().add(createPinIcon(item));
            }
        }
        return box;
    }

    private Button createPinIcon(String val) {
        Button btn = new Button(val);
        btn.setPrefSize(50, 50);
        btn.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: " + (val.equals("1") ? "#27ae60" : "#c0392b"));
        btn.setOnDragDetected(e -> {
            Dragboard db = btn.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString("PIN:" + val);
            db.setContent(content);
            e.consume();
        });
        return btn;
    }

    private ImageView createGateIcon(String type) {
        String path = "/me/filip_jakubowski/bytelab/" + type + ".png";
        java.io.InputStream is = getClass().getResourceAsStream(path);
        if (is == null) return null;
        ImageView iv = new ImageView(new Image(is));
        iv.setFitWidth(80); iv.setPreserveRatio(true); iv.setStyle("-fx-cursor: hand;");
        iv.setOnDragDetected(e -> {
            Dragboard db = iv.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString("GATE:" + type);
            db.setContent(content);
            e.consume();
        });
        return iv;
    }

    private void onActionMade() { if (!gameOver) { checkWin(); if (!gameOver) { isGreenTurn = !isGreenTurn; updateStatusStyle(); } } }
    private void updateStatusStyle() {
        statusLabel.setText("Tura: " + (isGreenTurn ? "ZIELONY" : "CZERWONY"));
        statusLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + (isGreenTurn ? "#2ecc71" : "#ff4444"));
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
            statusLabel.setText("WYGRANA: " + (winner.equals("1") ? "ZIELONY" : "CZERWONY"));
            statusLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + (winner.equals("1") ? "#2ecc71" : "#ff4444"));
        }
    }

    private boolean checkLine(LogicTile t1, LogicTile t2, LogicTile t3) {
        String s1 = t1.getSymbol();
        return s1 != null && !s1.isEmpty() && s1.equals(t2.getSymbol()) && s1.equals(t3.getSymbol());
    }

    private void setupFooter() {
        Button reset = new Button("Resetuj Grę");
        reset.setOnAction(e -> MainApp.getNavigationManager().showAdvancedLogicGame());
        Button back = new Button("Menu");
        back.setOnAction(e -> MainApp.getNavigationManager().showStartScreen());
        HBox f = new HBox(20, reset, back); f.setAlignment(Pos.CENTER); f.setPadding(new Insets(20));
        setBottom(f);
    }
}