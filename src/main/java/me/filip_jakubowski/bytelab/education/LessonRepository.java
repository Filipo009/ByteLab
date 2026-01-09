package me.filip_jakubowski.bytelab.education;

import java.util.List;

public class LessonRepository {
    public record Lesson(String title, String content) {}

    private static final List<Lesson> LESSONS = List.of(
            new Lesson("System Binarny",
                    "Dlaczego komputery używają zer i jedynek? W świecie elektroniki najłatwiej operować na dwóch stanach: płynie prąd (1) lub nie płynie (0). To właśnie jest fundament systemu dwójkowego.\n\n" +
                            "W systemie dziesiętnym, którego używamy na co dzień, mamy cyfry 0-9. W binarnym mamy tylko 0 i 1. Każda pozycja w liczbie binarnej to kolejna potęga liczby 2, licząc od prawej strony.\n\n" +
                            "Poniżej znajduje się interaktywny model 8 bitów (czyli 1 Bajta). Klikaj w poszczególne bity, aby zobaczyć jak ich kombinacja tworzy liczbę dziesiętną:\n" +
                            "[BINARY]\n" +
                            "Zauważ, że największa liczba jaką można zapisać na 8 bitach to 255 (gdy wszystkie bity są ustawione na 1). Jeśli potrzebujemy większych liczb, procesor musi łączyć bajty w większe grupy, np. 16-bitowe lub 32-bitowe."),

            new Lesson("Bramki Logiczne",
                    "Skoro wiemy już, że dane to zera i jedynki, musimy wiedzieć, jak je przetwarzać. Służą do tego bramki logiczne – miniaturowe układy, które przyjmują sygnały wejściowe i na ich podstawie wystawiają wynik.\n\n" +
                            "1. Bramka AND (I) - Daje 1 tylko wtedy, gdy na obu wejściach jest 1. Działa jak szeregowe połączenie dwóch włączników.\n[GATE:and]\n" +
                            "2. Bramka OR (LUB) - Daje 1, jeśli przynajmniej na jednym wejściu jest 1.\n[GATE:or]\n" +
                            "3. Bramka XOR (Ekskluzywne LUB) - To jedna z najważniejszych bramek. Daje 1 tylko wtedy, gdy wejścia są RÓŻNE. Jest podstawą budowy sumatorów (układów dodających liczby).\n[GATE:xor]\n" +
                            "4. Bramka NAND - Odwrotność AND. Jest to bramka 'uniwersalna' - przy pomocy odpowiedniego połączenia samych bramek NAND można zbudować dowolny inny układ logiczny.\n[GATE:nand]\n" +
                            "Klikaj w cyfry przy wejściach bramek, aby przetestować ich tablice prawdy."),

            new Lesson("Rejestry i Pamięć",
                    "Procesor nie mógłby pracować, gdyby nie miał gdzie trzymać wyników swoich obliczeń. Rejestry to najszybszy rodzaj pamięci w komputerze.\n\n" +
                            "W naszym emulatorze znajdziesz rejestry takie jak REG A, B, C czy D. Są one połączone bezpośrednio z jednostką ALU (Arytmetyczno-Logiczną). Kiedy wykonujesz instrukcję ADD, procesor bierze wartości z rejestrów, przepuszcza je przez sieć bramek logicznych i zapisuje wynik z powrotem w rejestrze.\n\n" +
                            "PC (Program Counter) to specjalny rejestr, który wskazuje, którą linię kodu mamy aktualnie wykonać. Bez niego procesor nie wiedziałby, co robić w następnym kroku.")
    );

    public static Lesson getLesson(int id) {
        return (id >= 0 && id < LESSONS.size()) ? LESSONS.get(id) : null;
    }

    public static List<String> getTitles() {
        return LESSONS.stream().map(Lesson::title).toList();
    }

    public static int size() { return LESSONS.size(); }
}