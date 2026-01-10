package me.filip_jakubowski.bytelab.education;

import java.util.List;

public class LessonRepository {
    public record Lesson(String title, String content) {}

    private static final List<Lesson> LESSONS = List.of(
            new Lesson("System Binarny",
                    "W systemie binarnym każda pozycja to kolejna potęga liczby 2. W podstawowym wariancie (bez znaku) wszystkie bity mają wartości dodatnie.\n\n" +
                            "Poniżej znajduje się model 8-bitowy. Spróbuj ułożyć liczbę 10 (8+2) lub 255 (wszystkie bity):\n" +
                            "[BINARY]\n\n" +
                            "Kiedy jednak musimy zapisać liczby ujemne, stosujemy system U2 (Uzupełnień do dwóch). W tym systemie pierwszy bit od lewej ma wagę ujemną (-128).\n\n" +
                            "Spójrz na poniższy przykład. Jeśli ustawisz pierwszy bit na 1, a pozostałe na 0, otrzymasz -128. Jeśli ustawisz wszystkie na 1, otrzymasz -1:\n" +
                            "[BINARY:U2]"),

            new Lesson("Bramki Logiczne",
                    "Bramki to podstawowe klocki, z których buduje się procesory. Oto ich zestawienie:\n\n" +
                            "1. AND - Wynik 1, gdy oba wejścia to 1.\n[GATE:and]\n" +
                            "2. OR - Wynik 1, gdy przynajmniej jedno wejście to 1.\n[GATE:or]\n" +
                            "3. XOR - Wynik 1 tylko gdy wejścia się różnią.\n[GATE:xor]\n" +
                            "4. NAND - Przeciwieństwo AND.\n[GATE:nand]\n" +
                            "5. NOR - Przeciwieństwo OR.\n[GATE:nor]\n" +
                            "6. XNOR - Wynik 1 tylko gdy wejścia są identyczne.\n[GATE:xnor]"),

            new Lesson("Rejestry i Pamięć",
                    "Procesor przechowuje dane w rejestrach. Są to bardzo szybkie komórki pamięci, na których jednostka ALU wykonuje operacje.\n\n" +
                            "W naszym emulatorze rejestry A, B, C i D są 8-bitowe, co oznacza, że mogą przechowywać wartości od 0 do 255 (lub od -128 do 127 w systemie U2).")
    );

    public static Lesson getLesson(int id) {
        return (id >= 0 && id < LESSONS.size()) ? LESSONS.get(id) : null;
    }

    public static List<String> getTitles() {
        return LESSONS.stream().map(Lesson::title).toList();
    }

    public static int size() { return LESSONS.size(); }
}