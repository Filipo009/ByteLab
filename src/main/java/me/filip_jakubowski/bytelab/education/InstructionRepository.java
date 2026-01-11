package me.filip_jakubowski.bytelab.education;

import java.util.ArrayList;
import java.util.List;

public class InstructionRepository {
    public record Manual(String title, String content) {}
    private static final List<Manual> manuals = new ArrayList<>();

    static {
        manuals.add(new Manual(
                "Gra: Zera i Jedynki",
                "**ZASADY GRY**\n\n" +
                        "To logiczna wersja kółka i krzyżyka. Aby wygrać, musisz ułożyć trzy '1' lub trzy '0' w linii.\n\n" +
                        "1. Przeciągnij **bramkę** na pole.\n" +
                        "2. Przeciągnij **piny (0/1)** na wejścia bramki.\n" +
                        "3. Wynik obliczy się sam!\n\n" +
                        "[LOGIC_GAME:MODULE]\n\n" +
                        "**WSKAZÓWKA:** Każdy ruch (nawet dodanie pinu) kończy turę gracza!"
        ));
    }

    public static Manual getManual(int id) { return manuals.get(id); }
    public static int size() { return manuals.size(); }
}