package me.filip_jakubowski.bytelab.education;

import java.util.List;

public class LessonRepository {
    public record Lesson(String title, String content) {}

    private static final List<Lesson> LESSONS = List.of(
            new Lesson("Podstawy procesora", "Architektura von Neumanna to model komputera, w którym dane i programy są przechowywane w tej samej pamięci."),
            new Lesson("System binarny", "Komputery operują na stanach 0 i 1. Każdy taki stan to 1 bit. 8 bitów tworzy 1 bajt."),
            new Lesson("Rejestry", "Rejestry (np. REG A) to miejsca wewnątrz procesora, gdzie dane są przetwarzane błyskawicznie.")
    );

    public static Lesson getLesson(int id) {
        return (id >= 0 && id < LESSONS.size()) ? LESSONS.get(id) : null;
    }

    public static List<String> getTitles() {
        return LESSONS.stream().map(Lesson::title).toList();
    }

    public static int size() { return LESSONS.size(); }
}