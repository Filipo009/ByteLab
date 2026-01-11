package me.filip_jakubowski.bytelab.education;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class TheoryArchView extends VBox {
    private boolean isHarvard = false;
    private final Pane drawPane = new Pane();
    private final Text statusText = new Text(" ");
    private final CheckBox dataToggle = new CheckBox("Dodatkowe pobieranie danych (Operand)");
    private final List<StackPane> nodes = new ArrayList<>();

    public TheoryArchView() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new Insets(20));
        setMaxWidth(650);
        setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #444; -fx-border-radius: 10;");

        HBox controls = new HBox(15);
        controls.setAlignment(Pos.CENTER);
        Button btnVN = new Button("Von Neumann");
        Button btnHV = new Button("Harvard");
        Button btnSim = new Button("▶ SYMULUJ CYKL");

        btnVN.setOnAction(e -> { isHarvard = false; redraw(); });
        btnHV.setOnAction(e -> { isHarvard = true; redraw(); });
        btnSim.setOnAction(e -> runSimulation());

        dataToggle.setStyle("-fx-text-fill: white; -fx-font-family: 'Consolas';");
        dataToggle.setSelected(true);

        controls.getChildren().addAll(btnVN, btnHV, btnSim);
        drawPane.setPrefSize(600, 320);
        statusText.setFill(Color.web("#3498db"));
        statusText.setFont(Font.font("Consolas", FontWeight.BOLD, 14));

        getChildren().addAll(controls, dataToggle, drawPane, statusText);
        redraw();
    }

    private void redraw() {
        drawPane.getChildren().clear();
        nodes.clear();
        statusText.setText("Gotowy do symulacji");
        double centerX = 300;

        if (!isHarvard) {
            drawBus(centerX, 90, centerX, 230);
        } else {
            drawBus(centerX - 130, 90, centerX - 130, 230);
            drawBus(centerX + 130, 90, centerX + 130, 230);
        }

        nodes.add(drawNode(centerX, 60, 380, isHarvard ? "PROCESOR (CPU - HARVARD)" : "PROCESOR (CPU - VON NEUMANN)", Color.web("#3498db")));

        if (!isHarvard) {
            nodes.add(drawNode(centerX, 260, 180, "WSPÓLNY RAM\n(INSTR + DATA)", Color.web("#2ecc71")));
        } else {
            nodes.add(drawNode(centerX - 130, 260, 140, "PAMIĘĆ PROG", Color.web("#e67e22")));
            nodes.add(drawNode(centerX + 130, 260, 140, "PAMIĘĆ DANYCH", Color.web("#9b59b6")));
        }
    }

    private void runSimulation() {
        drawPane.getChildren().removeIf(n -> n instanceof Circle);
        statusText.setText(" ");
        boolean includeData = dataToggle.isSelected();
        double centerX = 300;

        if (!isHarvard) {
            animateManual(centerX, 250, centerX, 70, Color.YELLOW, 0, "FETCH: Pobieranie instrukcji...");
            if (includeData) {
                animateManual(centerX, 250, centerX, 70, Color.CYAN, 1.5, "DATA: Pobieranie danych (Kolejka!)");
            }
        } else {
            animateManual(centerX - 130, 250, centerX - 130, 70, Color.YELLOW, 0, "FETCH: Instrukcja...");
            if (includeData) {
                animateManual(centerX + 130, 250, centerX + 130, 70, Color.CYAN, 0.0, "FETCH + DATA: Praca równoległa!");
            }
        }
    }

    private void animateManual(double x1, double y1, double x2, double y2, Color c, double delay, String msg) {
        Circle bit = new Circle(8, c);
        bit.setCenterX(x1);
        bit.setCenterY(y1);

        drawPane.getChildren().add(bit);
        // Przesuwamy pod bloki tekstowe
        for(StackPane node : nodes) node.toFront();

        Timeline timeline = new Timeline();
        timeline.setDelay(Duration.seconds(delay));

        KeyFrame startFrame = new KeyFrame(Duration.ZERO, e -> statusText.setText(msg));
        KeyFrame moveFrame = new KeyFrame(Duration.seconds(1.2),
                new KeyValue(bit.centerYProperty(), y2, Interpolator.EASE_BOTH));

        timeline.getKeyFrames().addAll(startFrame, moveFrame);

        timeline.setOnFinished(e -> {
            drawPane.getChildren().remove(bit);
            // Jeśli nie ma już innych kulek, wyczyść napis
            if (drawPane.getChildren().stream().noneMatch(n -> n instanceof Circle)) {
                statusText.setText("Cykl zakończony");
            }
        });

        timeline.play();
    }

    private StackPane drawNode(double x, double y, double width, String label, Color c) {
        Rectangle r = new Rectangle(width, 70);
        r.setFill(Color.web("#222"));
        r.setStroke(c);
        r.setStrokeWidth(3);
        r.setArcWidth(15); r.setArcHeight(15);

        Text t = new Text(label);
        t.setFill(Color.WHITE);
        t.setFont(Font.font("Consolas", FontWeight.BOLD, 11));
        t.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        StackPane sp = new StackPane(r, t);
        sp.setLayoutX(x - width/2);
        sp.setLayoutY(y - 35);

        drawPane.getChildren().add(sp);
        return sp;
    }

    private void drawBus(double x1, double y1, double x2, double y2) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(Color.web("#333"));
        l.setStrokeWidth(18);
        drawPane.getChildren().add(l);
    }
}