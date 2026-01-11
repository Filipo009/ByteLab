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
                            "Przesunięcia bitowe (Shifts) to operacje, które dosłownie przesuwają wszystkie bity w lewo lub w prawo. Są one niezwykle szybkie i stanowią fundament optymalizacji matematycznej.\n\n" +
                            "--- Mnożenie i Dzielenie ---\n" +
                            "Przesunięcie w lewo o 1 pozycję (SHL) odpowiada pomnożeniu liczby przez 2. \n" +
                            "Przesunięcie w prawo o 1 pozycję (SHR) odpowiada dzieleniu całkowitemu przez 2.\n\n" +
                            "--- Rotacje ---\n" +
                            "W przypadku zwykłego przesunięcia, bit który 'wypada' z rejestru jest tracony, a w puste miejsce wskakuje 0. W przypadku rotacji (ROL/ROR), bit ten wraca drugą stroną.\n\n" +
                            "Ustaw bity wejściowe, a następnie wybierz operację, aby zobaczyć jak zmienia się wartość dziesiętna:\n" +
                            "[SHIFT:MODULE]"),

            new Lesson("Rejestry",
                    "Jednostka ALU wykonuje obliczenia, ale nie potrafi ich zapamiętać. Do tego służą Rejestry – bardzo szybkie komórki pamięci umieszczone tuż obok ALU.\n\n" +
                            "Rejestr posiada piny sterujące:\n" +
                            "1. **LOAD** – Gdy ten sygnał jest aktywny, rejestr kopiuje wartości z magistrali wejściowej do swojej pamięci.\n" +
                            "2. **CLEAR** – Natychmiast zeruje wszystkie bity w rejestrze.\n\n" +
                            "W naszym procesorze rejestry (np. REG A i REG B) są używane do trzymania argumentów operacji dla ALU. Przetestuj działanie zapisu danych w module poniżej:\n" +
                            "[REGISTER:MODULE]"),

            new Lesson("Pamięć RAM",
                    "Pamięć RAM (Random Access Memory) to zestaw wielu komórek (rejestrów) pod jednym wspólnym adresem. \n\n" +
                            "--- Jak działa adresowanie? ---\n" +
                            "Aby procesor nie musiał mieć osobnych kabli do każdej komórki, stosuje się **dekoder**. W naszym modelu mamy 4 bity adresu, co pozwala wybrać 1 z 16 komórek ($2^4 = 16$).\n\n" +
                            "--- Chip Select (CS) ---\n" +
                            "W komputerze jest wiele układów pamięci. Sygnał CS aktywuje konkretną kość. Gdy CS jest wyłączony, pamięć 'odłącza się' od szyny i nie reaguje na próby zapisu czy odczytu.\n\n" +
                            "--- Cykl pracy ---\n" +
                            "1. Ustaw adres komórki.\n" +
                            "2. Włącz Chip Select.\n" +
                            "3. Ustaw dane na kluczach wejściowych i kliknij **WRITE**, aby zapisać.\n" +
                            "4. Kliknij **READ**, aby przenieść zawartość komórki do rejestru wyjściowego.\n\n" +
                            "[RAM:MODULE]"),

            new Lesson("Magistrale (Buses)",
                    "Wewnątrz procesora dane nie poruszają się chaotycznie. Podróżują one po **Magistralach** (Buses) – czyli wiązkach przewodów łączących różne moduły.\n\n" +
                            "--- Sygnały i Kolory ---\n" +
                            "W elektronice magistrala nie ma 'koloru', ale ma napięcie. \n" +
                            "* **Stan Wysoki (1)**: Prąd płynie (w naszej symulacji kolor zielony).\n" +
                            "* **Stan Niski (0)**: Brak prądu (w naszej symulacji kolor czerwony).\n\n" +
                            "--- Przepływ danych ---\n" +
                            "Poniżej widzisz dwie magistrale wejściowe podłączone do addera. Wynik z addera trafia na trzecią magistralę, która prowadzi do rejestru. \n" +
                            "Zauważ, że mimo iż adder cały czas podaje wynik na magistralę wyjściową, rejestr 'zapamięta' go dopiero, gdy wyślesz sygnał **LOAD**.\n\n" +
                            "[BUS:MODULE]"),

            new Lesson("Architektura Datapath",
                    "Prawdziwa potęga procesora tkwi w tym, jak łączymy ze sobą moduły. W tej lekcji widzisz schemat przepływu danych zwany **Datapath**.\n\n" +
                            "--- Jak to działa? ---\n" +
                            "1. Mamy dwie magistrale wejściowe (A i B), które dostarczają liczby do **Addera**.\n" +
                            "2. Adder wykonuje obliczenia w czasie rzeczywistym – zwróć uwagę, jak linie wyjściowe (z prawej strony) zmieniają kolor, gdy zmieniasz bity wejściowe.\n" +
                            "3. Rejestr (po prawej) jest ustawiony pionowo, co jest częstym zabiegiem w projektowaniu procesorów, aby zaoszczędzić miejsce i ułatwić prowadzenie szyn danych.\n\n" +
                            "Spróbuj dodać dwie liczby i 'zatrzasnąć' wynik w pionowym rejestrze przyciskiem **LOAD**.\n\n" +
                            "[BUS:COMPLEX]"),

            new Lesson("Zestaw Instrukcji (ISA)",
                    "Procesor nie rozumie ludzkiej mowy, ale rozumie zestaw krótkich poleceń zwanych **Instrukcjami**.\n\n" +
                            "--- Anatomia Rozkazu ---\n" +
                            "Większość instrukcji w naszym systemie wygląda tak:\n" +
                            "`OPERACJA` | `ŹRÓDŁO` -> `CEL` \n\n" +
                            "* **ADD ---- -> REG 0**: Dodaj wartości z A i B, wyślij wynik do Rejestru 0.\n" +
                            "* **MOV REG A -> REG B**: Skopiuj zawartość Rejestru A do Rejestru B.\n\n" +
                            "--- Dlaczego '----'? ---\n" +
                            "W instrukcji `ADD` źródło jest domyślne. Wynika to z budowy hardware'u: Adder jest na stałe wpięty między magistrale wejściowe. Nie trzeba mu mówić, co ma dodać – on dodaje wszystko, co 'widzi' na wejściach.\n\n" +
                            "Kliknij poniższe przyciski, aby zobaczyć jak instrukcje konfigurują procesor:\n\n" +
                            "[INSTR:VIEW]"),

            new Lesson("Kod Maszynowy i Opcode",
                    "Komputer nie rozumie słów takich jak ADD czy MOV. Każda instrukcja musi być zapisana jako liczba binarna, którą procesor potrafi rozpoznać. " +
                            "Ten unikalny numer instrukcji nazywamy OPCODE.\n\n" +
                            "Poniższa tabela przedstawia zestaw instrukcji (ISA) naszego procesora wraz z ich kodami binarnymi i argumentami:\n\n" +
                            "[TABLE:ISA_OPCODES]\n\n" +
                            "Spróbuj teraz samodzielnie pobawić się dekoderem. Zmieniaj bity OPCODE (4 bity po lewej), aby zobaczyć, jaką instrukcję rozpozna jednostka sterująca:\n\n" +
                            "[OPCODE:DECODER]"),

            new Lesson("Kompilacja i Format Instrukcji",
                    "Skoro znamy już listę instrukcji (ISA), musimy dowiedzieć się, jak procesor odróżnia np. 'MOV REG A -> REG B' od 'MOV REG C -> REG D'.\n\n" +
                            "Rozwiązaniem jest **Format Instrukcji**. Jeden 8-bitowy bajt dzielimy na części (pola):\n" +
                            "* **Bity 7-4 (4 bity):** Opcode (numer operacji z tabeli poniżej).\n" +
                            "* **Bity 3-2 (2 bity):** Kod rejestru źródłowego (SRC).\n" +
                            "* **Bity 1-0 (2 bity):** Kod rejestru docelowego (DST).\n\n" +
                            "[TABLE:ISA_OPCODES]\n\n" + // <--- TA SAMA TABELKA TUTAJ
                            "--- Adresowanie Rejestrów A-D ---\n" +
                            "Dzięki temu, że mamy tylko 4 rejestry, wystarczą nam tylko 2 bity, aby wskazać dowolny z nich:\n\n" +
                            "[TABLE:REGISTERS_SHORT]\n\n" +
                            "Poniżej możesz przetestować, jak zmieniając bity, tworzysz gotowe polecenia dla procesora:\n\n" +
                            "[COMPILER:MODULE]")
    );

    public static Lesson getLesson(int id) {
        return (id >= 0 && id < LESSONS.size()) ? LESSONS.get(id) : null;
    }
    public static List<String> getTitles() {
        return LESSONS.stream().map(Lesson::title).toList();
    }
    public static int size() { return LESSONS.size(); }
}