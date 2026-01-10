package me.filip_jakubowski.bytelab.education;

import java.util.List;

public class LessonRepository {
    public record Lesson(String title, String content) {}

    private static final List<Lesson> LESSONS = List.of(
            new Lesson("System Binarny",
                    "Dlaczego komputery używają zer i jedynek? W świecie elektroniki najłatwiej operować na dwóch stanach: płynie prąd (1) lub nie płynie (0). To właśnie jest fundament systemu dwójkowego.\n\n" +
                            "W systemie dziesiętnym mamy cyfry 0-9. W binarnym tylko 0 i 1. Każda pozycja w liczbie binarnej to kolejna potęga liczby 2, licząc od prawej strony.\n\n" +
                            "[BINARY]\n\n" +
                            "Aby komputer mógł zapisywać liczby ujemne, stosuje się system U2 (Uzupełnień do dwóch). Różni się on tym, że najbardziej znaczący bit (MSB) ma wagę ujemną (dla 8 bitów jest to -128).\n\n" +
                            "Kluczową operacją w U2 jest zmiana znaku liczby. Aby zamienić liczbę dodatnią na ujemną (lub odwrotnie), wykonujemy dwa kroki:\n" +
                            "1. Negujemy wszystkie bity (NOT).\n" +
                            "2. Do wyniku dodajemy 1.\n\n" +
                            "Przetestuj to poniżej, ustawiając bity i klikając przycisk zmiany znaku:\n" +
                            "[BINARY:U2]"),

            new Lesson("Bramki Logiczne",
                    "Skoro wiemy już, że dane to zera i jedynki, musimy wiedzieć, jak je przetwarzać. Służą do tego bramki logiczne – miniaturowe układy, które przyjmują sygnały wejściowe i na ich podstawie wystawiają wynik.\n\n" +
                            "1. Bramka AND (I) - Daje 1 tylko wtedy, gdy na obu wejściach jest 1.\n[GATE:and]\n" +
                            "2. Bramka OR (LUB) - Daje 1, jeśli przynajmniej na jednym wejściu jest 1.\n[GATE:or]\n" +
                            "3. Bramka XOR - Daje 1 tylko wtedy, gdy wejścia są RÓŻNE. Jest podstawą budowy sumatorów.\n[GATE:xor]\n" +
                            "4. Bramka NAND - Odwrotność AND. Jest to bramka 'uniwersalna'.\n[GATE:nand]\n" +
                            "5. Bramka NOR - Odwrotność OR.\n[GATE:nor]\n" +
                            "6. Bramka XNOR - Odwrotność XOR.\n[GATE:xnor]"),

            new Lesson("Jednostka ALU",
                    "ALU (Arithmetic Logic Unit) to serce procesora. Podstawową operacją jest dodawanie realizowane przez sumator:\n" +
                            "[ALU:ADDER]\n\n" +
                            "--- Flaga Zero (Z) ---\n" +
                            "Ważnym elementem ALU są flagi stanu. Flaga ZERO (Z) przyjmuje wartość 1, gdy wynik operacji to same zera. Pozwala to procesorowi sprawdzać np. czy dwie liczby są równe (wynik odejmowania to 0).\n\n" +
                            "--- Arytmetyka U2 i Odejmowanie ---\n" +
                            "Odejmowanie $A - B$ to dodawanie liczby przeciwnej: $A + (NOT B + 1)$. Sprawdź działanie flagi ZERO, gdy odejmiesz od siebie te same liczby:\n" +
                            "[ALU:FULL]\n\n" +
                            "--- Operacje Logiczne ---\n" +
                            "ALU wykonuje też operacje bitowe na całych bajtach:\n" +
                            "[ALU:LOGIC]\n\n" +
                            "--- Przesunięcia ---\n" +
                            "Ostatnim elementem są shifty (mnożenie/dzielenie przez 2):\n" +
                            "[SHIFT:MODULE]"),

            new Lesson("Rejestry i Pamięć",
                    "Rejestry to najszybszy rodzaj pamięci w komputerze. Znajdują się bezpośrednio w procesorze i są połączone z wejściami ALU.\n\n" +
                            "W naszym emulatorze rejestry A, B, C i D przechowują 8-bitowe wartości. Rejestr PC (Program Counter) śledzi postęp programu, wskazując na adres kolejnej instrukcji."),

            new Lesson("Przesunięcia Bitowe",
                      "Przesunięcia bitowe (Shifts) to operacje, które dosłownie przesuwają wszystkie bity w lewo lub w prawo. Są one niezwykle szybkie i stanowią fundament optymalizacji matematycznej.\n\n" +
                      "--- Mnożenie i Dzielenie ---\n" +
                      "Przesunięcie w lewo o 1 pozycję (SHL) odpowiada pomnożeniu liczby przez 2. \n" +
                      "Przesunięcie w prawo o 1 pozycję (SHR) odpowiada dzieleniu całkowitemu przez 2.\n\n" +
                      "--- Rotacje ---\n" +
                      "W przypadku zwykłego przesunięcia, bit który 'wypada' z rejestru jest tracony, a w puste miejsce wskakuje 0. W przypadku rotacji (ROL/ROR), bit ten wraca drugą stroną.\n\n" +
                      "Ustaw bity wejściowe, a następnie wybierz operację, aby zobaczyć jak zmienia się wartość dziesiętna:\n" +
                      "[SHIFT:MODULE]"),

            new Lesson("Rejestry i Pamięć",
                               "Rejestry to najszybszy rodzaj pamięci w komputerze. Znajdują się bezpośrednio w procesorze i są połączone z wejściami ALU.")
    );

    public static Lesson getLesson(int id) {
        return (id >= 0 && id < LESSONS.size()) ? LESSONS.get(id) : null;
    }
    public static List<String> getTitles() {
        return LESSONS.stream().map(Lesson::title).toList();
    }
    public static int size() { return LESSONS.size(); }
}