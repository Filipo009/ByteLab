package me.filip_jakubowski.bytelab.logicgame;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import java.util.HashSet;
import java.util.Set;

public class BusLine extends Line {
    private final String busId;
    private Integer currentValue = null;
    private final Set<Object> sources = new HashSet<>(); // Źródła sygnału (switche/bramki)

    public BusLine(String id, double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2);
        this.busId = id;
        setStrokeWidth(5);
        setStroke(Color.GRAY); // Kolor neutralny (brak sygnału)
    }

    public boolean tryWrite(Object source, int value) {
        // Sprawdzenie konfliktu: jeśli inne źródło już nadaje inny sygnał
        for (Object s : sources) {
            if (s != source && currentValue != null && currentValue != value) {
                return false; // Konflikt na szynie!
            }
        }
        sources.add(source);
        currentValue = value;
        updateVisuals();
        return true;
    }

    private void updateVisuals() {
        if (currentValue == null) setStroke(Color.GRAY);
        else setStroke(currentValue == 1 ? Color.web("#2ecc71") : Color.web("#ff4444"));
    }

    public Integer getValue() { return currentValue; }
    public String getBusId() { return busId; }
}