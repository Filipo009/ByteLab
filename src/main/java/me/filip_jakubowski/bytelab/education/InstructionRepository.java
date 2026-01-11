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

        manuals.add(new Manual(
                "Emulator i Kreator Instrukcji",
                "**WPROWADZENIE**\n" +
                        "Emulator ByteLab pozwala na pisanie, zapisywanie i testowanie programów w czasie rzeczywistym. " +
                        "Składa się z dwóch głównych modułów: **Kreatora** (po lewej) oraz **Symulatora** (po prawej).\n\n" +

                        "**1. KREATOR INSTRUKCJI**\n" +
                        "Nie musisz znać kodów binarnych na pamięć. Kreator prowadzi Cię za rękę:\n" +
                        "* **Etap 1:** Wybierz rozkaz (np. ADD, MOV).\n" +
                        "* **Etap 2:** Wybierz źródło danych lub rejestr wejściowy.\n" +
                        "* **Etap 3:** Wybierz rejestr docelowy.\n\n" +

                        "**2. PAMIĘĆ PROGRAMU I ADRESOWANIE**\n" +
                        "Użyj pola **Adres**, aby wstawić instrukcję w konkretne miejsce w pamięci (format HEX, np. 0x0005). " +
                        "Jeśli wstawisz instrukcję pod daleki adres, emulator automatycznie wypełni luki rozkazami NOP (No Operation).\n\n" +

                        "**3. SYMULACJA (RUN / STEP)**\n" +
                        "Gdy program jest gotowy, użyj panelu symulacji:\n" +
                        "* **STEP:** Wykonuje tylko jedną linię kodu. Idealne do debugowania.\n" +
                        "* **RUN:** Uruchamia program z wybraną częstotliwością (Hz).\n" +
                        "* **KOLORY:** Rejestry, które zmieniły wartość w ostatnim cyklu, podświetlają się na **czerwono**.\n\n" +

                        "**TABELA REJESTRÓW SYMULATORA**\n" +
                        "[TABLE:REGISTERS_SHORT]\n\n" +

                        "**WSKAZÓWKA:** Kliknij przycisk **Schemat**, aby otworzyć mapę połączeń procesora w osobnym oknie. " +
                        "Możesz je przeciągnąć na drugi monitor, aby widzieć, jak dane płyną między ALU a rejestrami!"
        ));
    }

    public static Manual getManual(int id) { return manuals.get(id); }
    public static int size() { return manuals.size(); }
}