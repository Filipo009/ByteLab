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
import me.filip_jakubowski.bytelab.MainApp;

public class AdvancedLogicGameView extends BorderPane {

    private final LogicTile[][] tiles = new LogicTile[3][3];
    private final Label statusLabel = new Label();
    private final BusManager busManager = new BusManager();
    private final Pane matrixLayer = new Pane();
    private boolean gameOver = false;
    private boolean isGreenTurn = true;

    // Parametry siatki - stałe dla wszystkich metod
    private final double slotSize = 130;
    private final double gap = 80;
    private final double spacing = 5;
    private final double startCoord = 0;

    public AdvancedLogicGameView() {
        setPadding(new Insets(20));

        // Panele boczne
        setLeft(createToolbox("BRAMKI", new String[]{"and", "or", "xor", "nand", "nor"}, true));
        setRight(createToolbox("PINY", new String[]{"1", "0"}, false));

        // Główny kontener gry
        Group gameGroup = new Group();

        // WAŻNE: matrixLayer NIE może być transparentny, bo kafelki są jego dziećmi.
        // Ustawiamy tylko pickOnBounds na false, by puste miejsca nie kradły kliknięć.
        matrixLayer.setPickOnBounds(false);

        // 1. Rysujemy tło (magistrale)
        drawGridMatrix();

        // 2. Dodajemy kafelki i switche do tej samej warstwy (będą nad magistralami)
        setupComponents(matrixLayer);

        gameGroup.getChildren().add(matrixLayer);

        StackPane centerContainer = new StackPane(gameGroup);
        centerContainer.setAlignment(Pos.CENTER);

        VBox centerArea = new VBox(20, statusLabel, centerContainer);
        centerArea.setAlignment(Pos.CENTER);

        updateStatusStyle();
        setCenter(centerArea);

        setupFooter();
    }

    private void drawGridMatrix() {
        double bundleWidth = 8 * spacing;

        // 1. LINIE POZIOME (H-Bus)
        for (int bundle = 0; bundle < 4; bundle++) {
            double bundleCenterY = startCoord + (bundle * (slotSize + gap)) - (gap / 2);

            for (int bit = 0; bit < 9; bit++) {
                double y = bundleCenterY - (bundleWidth / 2) + (bit * spacing);

                double hStart = (startCoord + 0 * (slotSize + gap) - gap/2) - (bundleWidth/2) + (bit * spacing);
                double hEnd = (startCoord + 3 * (slotSize + gap) - gap/2) - (bundleWidth/2) + (bit * spacing);

                Line hLine = new Line(hStart, y, hEnd, y);
                applyBusStyle(hLine, bit); // Tu ustawiamy przezroczystość dla myszy samej linii
                matrixLayer.getChildren().add(hLine);
            }
        }

        // 2. LINIE PIONOWE (V-Bus)
        for (int bundle = 0; bundle < 4; bundle++) {
            double bundleCenterX = startCoord + (bundle * (slotSize + gap)) - (gap / 2);

            for (int bit = 0; bit < 9; bit++) {
                double x = bundleCenterX - (bundleWidth / 2) + (bit * spacing);

                double vStart = (startCoord + 0 * (slotSize + gap) - gap/2) - (bundleWidth/2) + (bit * spacing);
                double vEnd = (startCoord + 3 * (slotSize + gap) - gap/2) - (bundleWidth/2) + (bit * spacing);

                Line vLine = new Line(x, vStart, x, vEnd);
                applyBusStyle(vLine, bit); // Tu ustawiamy przezroczystość dla myszy samej linii
                matrixLayer.getChildren().add(vLine);

                // 3. KROPKI
                for (int hBundle = 0; hBundle < 4; hBundle++) {
                    double hCenterY = startCoord + (hBundle * (slotSize + gap)) - (gap / 2);
                    double hY = hCenterY - (bundleWidth / 2) + (bit * spacing);

                    Circle dot = new Circle(x, hY, 2.5);
                    styleDot(dot, bit); // Tu ustawiamy przezroczystość dla myszy samej kropki
                    matrixLayer.getChildren().add(dot);
                }
            }
        }
    }

    private void applyBusStyle(Line line, int bitIdx) {
        line.setStrokeWidth(1.5);
        line.setStroke(Color.web("#333"));
        // Sama linia nie powinna reagować na mysz, by nie blokować kafelków pod nią
        line.setMouseTransparent(true);

        busManager.addListener((idx, val) -> {
            if (idx == bitIdx) {
                line.setStroke(val == null ? Color.web("#333") :
                        (val == 1 ? Color.web("#2ecc71") : Color.web("#ff4444")));
            }
        });
    }

    private void styleDot(Circle dot, int bitIdx) {
        dot.setFill(Color.web("#333"));
        // Kropka również ignoruje mysz
        dot.setMouseTransparent(true);

        busManager.addListener((idx, val) -> {
            if (idx == bitIdx) {
                dot.setFill(val == null ? Color.web("#333") :
                        (val == 1 ? Color.web("#2ecc71") : Color.web("#ff4444")));
            }
        });
    }

    private void setupComponents(Pane parentPane) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                tiles[row][col] = new LogicTile(row, col, t -> onActionMade());

                double x = startCoord + col * (slotSize + gap);
                double y = startCoord + row * (slotSize + gap);

                tiles[row][col].setLayoutX(x);
                tiles[row][col].setLayoutY(y);

                parentPane.getChildren().add(tiles[row][col]);
            }
        }
        setupSwitches(parentPane, startCoord, slotSize, gap);
    }

    private void setupSwitches(Pane pane, double start, double size, double gap) {
        double fullSize = 2 * (size + gap) + size;
        double mid = start + (fullSize / 2) - 35;

        VBox swTop = createWorldSwitch(0);
        swTop.setLayoutX(mid); swTop.setLayoutY(start - gap - 40);

        VBox swBottom = createWorldSwitch(3);
        swBottom.setLayoutX(mid); swBottom.setLayoutY(start + fullSize + 30);

        VBox swLeft = createWorldSwitch(1);
        swLeft.setLayoutX(start - gap - 60); swLeft.setLayoutY(mid);

        VBox swRight = createWorldSwitch(2);
        swRight.setLayoutX(start + fullSize + 30); swRight.setLayoutY(mid);

        pane.getChildren().addAll(swTop, swBottom, swLeft, swRight);
    }

    private VBox createWorldSwitch(int id) {
        VBox swBox = new VBox(2);
        swBox.setAlignment(Pos.CENTER);
        Button btn = new Button("SW " + id);
        btn.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-font-weight: bold;");

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
                btn.setStyle("-fx-background-color: " + (next == 1 ? "#2ecc71" : "#ff4444") + "; -fx-text-fill: white; -fx-font-weight: bold;");
                onActionMade();
            }
        });
        swBox.getChildren().addAll(btn, bitSel);
        return swBox;
    }

    private VBox createToolbox(String title, String[] items, boolean isGate) {
        VBox box = new VBox(15);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: #252526; -fx-border-color: #333; -fx-border-radius: 5;");
        Label l = new Label(title);
        l.setStyle("-fx-text-fill: #007acc; -fx-font-weight: bold;");
        box.getChildren().add(l);
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

    private void onActionMade() {
        if (!gameOver) {
            checkWin();
            if (!gameOver) {
                isGreenTurn = !isGreenTurn;
                updateStatusStyle();
            }
        }
    }

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
        HBox f = new HBox(20, reset, back);
        f.setAlignment(Pos.CENTER);
        f.setPadding(new Insets(20));
        setBottom(f);
    }
}