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
                            "1. Negujemy wszystkie bity (zmieniając 0 na 1 i 1 na 0).\n" +
                            "2. Do wyniku dodajemy 1.\n\n" +
                            "Przetestuj to poniżej, ustawiając bity i klikając przycisk zmiany znaku:\n" +
                            "[BINARY:U2]"),

            new Lesson("Bramki Logiczne",
                    "Skoro wiemy już, że dane to zera i jedynki, musimy wiedzieć, jak je przetwarzać. Służą do tego bramki logiczne – miniaturowe układy, które przyjmują sygnały wejściowe i na ich podstawie wystawiają wynik.\n\n" +
                            "1. Bramka AND (I) - Daje 1 tylko wtedy, gdy na obu wejściach jest 1.\n[GATE:and]\n" +
                            "2. Bramka OR (LUB) - Daje 1, jeśli przynajmniej na jednym wejściu jest 1.\n[GATE:or]\n" +
                            "3. Bramka XOR (Ekskluzywne LUB) - Daje 1 tylko wtedy, gdy wejścia są RÓŻNE. Jest podstawą budowy sumatorów.\n[GATE:xor]\n" +
                            "4. Bramka NAND - Odwrotność AND. Jest to bramka 'uniwersalna'.\n[GATE:nand]\n" +
                            "5. Bramka NOR - Odwrotność OR. Daje 1 tylko, gdy oba wejścia są równe 0.\n[GATE:nor]\n" +
                            "6. Bramka XNOR - Odwrotność XOR. Daje 1, gdy oba wejścia są takie same.\n[GATE:xnor]"),

            new Lesson("Jednostka ALU",
                    "ALU (Arithmetic Logic Unit) to serce procesora. Odpowiada za obliczenia matematyczne oraz operacje logiczne. Kiedy wykonujesz instrukcję ADD, procesor bierze wartości z rejestrów, przepuszcza je przez sieć bramek i zapisuje wynik.\n\n" +
                            "Podstawą ALU jest sumator (Adder). Ponieważ bajt ma tylko 8 bitów, przy dodawaniu może dojść do przepełnienia – wtedy aktywowany jest bit Carry (przeniesienie).\n\n" +
                            "[ALU:ADDER]\n\n" +
                            "Komputer nie posiada osobnego układu do odejmowania. Zamiast tego wykonuje dodawanie liczby przeciwnej: $A - B = A + (-B)$.\n\n" +
                            "Dzięki systemowi U2, aby odjąć liczbę B, wystarczy ją zanegować, dodać 1 (co realizujemy ustawiając Carry-In na 1) i dodać do liczby A. Poniższy moduł pozwala przełączać się między dodawaniem a odejmowaniem:\n" +
                            "[ALU:FULL]"),

            new Lesson("Rejestry i Pamięć",
                    "Procesor nie mógłby pracować, gdyby nie miał gdzie trzymać wyników swoich obliczeń. Rejestry to najszybszy rodzaj pamięci w komputerze.\n\n" +
                            "W naszym emulatorze znajdziesz rejestry takie jak REG A, B, C czy D. Są one połączone bezpośrednio z jednostką ALU. PC (Program Counter) to specjalny rejestr, który wskazuje, którą linię kodu mamy aktualnie wykonać.")
    );

    public static Lesson getLesson(int id) {
        return (id >= 0 && id < LESSONS.size()) ? LESSONS.get(id) : null;
    }

    public static List<String> getTitles() {
        return LESSONS.stream().map(Lesson::title).toList();
    }

    public static int size() { return LESSONS.size(); }
}