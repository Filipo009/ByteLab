package me.filip_jakubowski.bytelab.logicgame;

import java.util.ArrayList;
import java.util.List;

// Interfejs powiadamiania o zmianach na magistrali
interface BusUpdateListener {
    void onBusUpdate(int busIndex, Integer newValue);
}

public class BusManager {
    // 9 magistral, każda może mieć stan null (brak), 0 lub 1
    private final Integer[] busStates = new Integer[9];
    private final List<BusUpdateListener> listeners = new ArrayList<>();

    public void addListener(BusUpdateListener listener) {
        listeners.add(listener);
    }

    public void setBusState(int busIndex, Integer value) {
        if (busIndex < 0 || busIndex >= busStates.length) return;
        busStates[busIndex] = value;
        notifyListeners(busIndex, value);
    }

    public Integer getBusState(int busIndex) {
        return busStates[busIndex];
    }

    private void notifyListeners(int busIndex, Integer value) {
        for (BusUpdateListener listener : listeners) {
            listener.onBusUpdate(busIndex, value);
        }
    }
}