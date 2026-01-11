package me.filip_jakubowski.bytelab.education;

import java.util.ArrayList;
import java.util.List;

public class ChallengeRepository {
    // Record z 4 polami
    public record Challenge(String title, String description, String steps, String solution) {}

    private static final List<Challenge> challenges = new ArrayList<>();

    static {
        challenges.add(new Challenge(
                "Dodawanie i Rejestr D", // Title
                "Twoim zadaniem jest wprowadzenie dwóch dowolnych liczb do rejestrów A i B, dodanie ich do siebie, a następnie zapisanie wyniku w rejestrze REGD.", // Description
                "1. Użyj instrukcji IN dla REGA.\n2. Użyj instrukcji IN dla REGB.\n3. Wykonaj ADD wskazując REGD jako cel.", // STEPS (Wskazówka)
                "IN 0x1 REGA\nIN 0x5 REGB\nADD ---- REGD" // SOLUTION (Kod)
        ));

        // Przykład drugiego zadania
        challenges.add(new Challenge(
                "Czyszczenie rejestrów",
                "Wprowadź wartość do REGA, a następnie wyczyść go, używając do tego Rejestru Zerowego (REG0).",
                "1. Wprowadź dowolną wartość do REGA.\n2. Użyj instrukcji MOV.\n3. Skopiuj wartość z REG0 do REGA.",
                "IN 0x10 REGA\nMOV REG0 REGA"
        ));

        challenges.add(new Challenge(
                "Logika XOR i wykrywanie zmian",
                "Twoim zadaniem jest zbadanie operacji XOR. Pamiętaj, że XOR zwraca 0 tylko wtedy, gdy obie liczby są identyczne. Wykonaj operację dwukrotnie: najpierw na różnych liczbach, a potem na takich samych.",
                "1. Wprowadź wartość 3 do REGA i 5 do REGB.\n" +
                        "2. Wprowadź 3 do REGC (przygotowanie do porównania).\n" +
                        "3. Wykonaj XOR (REGA i REGB) i zapisz do REGD (wynik będzie różny od zera).\n" +
                        "4. Przenieś (MOV) wartość z REGC do REGB.\n" +
                        "5. Ponownie wykonaj XOR (REGA i REGB) do REGD. Tym razem 3 XOR 3 da wynik 0.",
                "IN 0x3 REGA\n" +
                        "IN 0x5 REGB\n" +
                        "IN 0x3 REGC\n" +
                        "XOR ---- REGD\n" +
                        "MOV REGC REGB\n" +
                        "XOR ---- REGD"
        ));

        challenges.add(new Challenge(
                "Nieskończona pętla i kontrola przepływu",
                "W tym zadaniu stworzysz program, który wykonuje serię operacji, a następnie wykorzystuje flagę Zero i instrukcję JZ (Jump if Zero), aby wrócić na sam początek (adres 0x00). To fundament działania każdego systemu operacyjnego!",
                "1. Wprowadź dane do REGA i REGC.\n" +
                        "2. Wykonaj dodawanie do REGB i przemieszczaj wartości między rejestrami A, C i D.\n" +
                        "3. Użyj XOR z rejestrem REG0, aby wpłynąć na flagę Zero.\n" +
                        "4. Wykorzystaj JZ 0, aby zmusić Program Counter (PC) do powrotu na adres startowy 0x00.",
                "IN 0x2 REGA\n" +
                        "IN 0x4 REGC\n" +
                        "ADD ---- REGB\n" +
                        "MOV REGA REGD\n" +
                        "MOV REGC REGA\n" +
                        "XOR REGB REG0\n" +
                        "JZ 0x0\n" +
                        "MOV REGD REGA\n" +
                        "ADD ---- REGB\n" +
                        "MOV REGC REGA\n" +
                        "XOR ---- REG0\n" +
                        "JZ 0x0"
        ));

        challenges.add(new Challenge(
                "Implementacja pętli sterowanej",
                "Twoim celem jest stworzenie programu, który będzie dodawał wartości w pętli. Wykorzystamy instrukcję JUMP do powrotu oraz JZ do przerwania pętli i wypisania wyniku na wyjście (OUT), gdy osiągniemy określony stan.",
                "1. Zainicjalizuj rejestry B i C wartościami początkowymi (licznik i krok).\n" +
                        "2. Wykonaj operację ADD, która będzie kumulować wynik w REGA.\n" +
                        "3. Użyj rejestru D jako tymczasowego schowka (MOV), aby móc manipulować danymi w REGB.\n" +
                        "4. Wykonaj test logiczny XOR z REG0, aby sprawdzić stan procesora.\n" +
                        "5. Użyj JZ 0x9, aby wyskoczyć z pętli do instrukcji OUT, lub JUMP 0x2, aby powtórzyć dodawanie.",
                "IN 0x1 REGB\n" +
                        "IN 0xA REGC\n" +
                        "ADD ---- REGA\n" +
                        "MOV REGB REGD\n" +
                        "MOV REGC REGB\n" +
                        "XOR ---- REG0\n" +
                        "MOV REGD REGB\n" +
                        "JZ 0x9\n" +
                        "JUMP 0x2\n" +
                        "OUT REGA"
        ));
    }

    public static Challenge getChallenge(int id) { return challenges.get(id); }
    public static int size() { return challenges.size(); }
}