package me.filip_jakubowski.bytelab.education;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import me.filip_jakubowski.bytelab.MainApp;

public class LessonView extends BorderPane {

    private final int lessonId;

    public LessonView(int lessonId) {
        this.lessonId = lessonId;

        String content = LessonRepository.getLesson(lessonId);

        Text text = new Text(content);
        text.setFont(Font.font(22));
        setCenter(text);
        BorderPane.setAlignment(text, Pos.CENTER);

        HBox bottom = new HBox();
        bottom.setSpacing(20);
        bottom.setPadding(new Insets(15));
        bottom.setAlignment(Pos.CENTER);

        Button prev = new Button("Poprzednia");
        Button next = new Button("Następna");
        Button exit = new Button("Wyjdź do Menu Głównego");

        prev.setOnAction(e -> navigateTo(lessonId - 1));
        next.setOnAction(e -> navigateTo(lessonId + 1));
        exit.setOnAction(e -> MainApp.getNavigationManager().showStartScreen());

        bottom.getChildren().addAll(prev, exit, next);
        setBottom(bottom);
    }

    private void navigateTo(int id) {
        if (LessonRepository.exists(id)) {
            MainApp.getNavigationManager().showLesson(id);
        }
    }
}
