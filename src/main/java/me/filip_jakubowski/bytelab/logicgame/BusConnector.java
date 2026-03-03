package me.filip_jakubowski.bytelab.logicgame;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

public class BusConnector extends Pane {

    public void drawConnection(double startX, double startY, double busY, Integer value) {
        // Czyścimy stare połączenie dla tego konkretnego wejścia (opcjonalnie)
        //getChildren().clear();

        Polyline line = new Polyline();
        // Rysujemy od wejścia bramki (startX, startY)
        // pionowo do poziomu magistrali (busY)
        line.getPoints().addAll(
                startX, startY,
                startX, busY
        );

        Color statusColor = (value == null) ? Color.GRAY :
                (value == 1 ? Color.web("#2ecc71") : Color.web("#ff4444"));

        line.setStroke(statusColor);
        line.setStrokeWidth(3);

        // Kropka na połączeniu z magistralą (*)
        Circle dot = new Circle(startX, busY, 5, statusColor);

        getChildren().addAll(line, dot);
    }
}