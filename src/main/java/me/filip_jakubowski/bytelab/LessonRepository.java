package me.filip_jakubowski.bytelab.education;

import java.util.List;

public class LessonRepository {

    private static final List<String> LESSONS = List.of(
            "Hello World – Lekcja 1",
            "Hello World – Lekcja 2",
            "Hello World – Lekcja 3"
    );

    public static String getLesson(int id) {
        return LESSONS.get(id);
    }

    public static boolean exists(int id) {
        return id >= 0 && id < LESSONS.size();
    }

    public static int size() {
        return LESSONS.size();
    }
}
