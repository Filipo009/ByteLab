package me.filip_jakubowski.bytelab.education;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import me.filip_jakubowski.bytelab.MainApp;

public class EducationView extends StackPane {

    private final Text titleText = new Text();
    private final Text contentText = new Text();
    private final ListView<String> lessonList = new ListView<>();

    private final HBox mainLayout = new HBox();
    private final VBox sidebar = new VBox(10);
    private final VBox contentArea = new VBox(25);

    // Przyciski sterujące (wyciągnięte jako pola klasy, by mieć do nich dostęp w loadLesson)
    private final Button prevBtn = new Button("<");
    private final Button nextBtn = new Button(">");

    private int currentLessonId;

    public EducationView(int startLessonId) {
        this.currentLessonId = startLessonId;
        getStyleClass().add("root");

        // --- PRZYCISK ROZWIJANIA ---
        Button expandBtn = new Button(">");
        expandBtn.setPrefSize(30, 30);
        expandBtn.setVisible(false);
        StackPane.setAlignment(expandBtn, Pos.TOP_LEFT);
        StackPane.setMargin(expandBtn, new Insets(10, 0, 0, 10));

        // --- SIDEBAR ---
        sidebar.setPadding(new Insets(15));
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);

        HBox sidebarHeader = new HBox();
        sidebarHeader.setAlignment(Pos.CENTER_LEFT);
        Label sidebarLabel = new Label("SPIS TREŚCI");
        sidebarLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #888888;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button collapseBtn = new Button("<");
        collapseBtn.setPrefSize(30, 30);

        sidebarHeader.getChildren().addAll(sidebarLabel, spacer, collapseBtn);

        lessonList.setItems(FXCollections.observableArrayList(LessonRepository.getTitles()));
        lessonList.getSelectionModel().select(currentLessonId);
        VBox.setVgrow(lessonList, Priority.ALWAYS);

        // Dolne przyciski nawigacji
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);
        Button menuBtn = new Button("Menu");

        prevBtn.setPrefWidth(40);
        menuBtn.setPrefWidth(80);
        nextBtn.setPrefWidth(40);

        prevBtn.setOnAction(e -> navigate(-1));
        menuBtn.setOnAction(e -> MainApp.getNavigationManager().showStartScreen());
        nextBtn.setOnAction(e -> navigate(1));

        controls.getChildren().addAll(prevBtn, menuBtn, nextBtn);
        sidebar.getChildren().addAll(sidebarHeader, lessonList, controls);

        // --- CONTENT ---
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setPadding(new Insets(40));
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        titleText.setFont(Font.font("Consolas", 32));
        titleText.setStyle("-fx-fill: #007acc; -fx-font-weight: bold;");

        contentText.setFont(Font.font("Consolas", 18));
        contentText.setStyle("-fx-fill: #e0e0e0;");
        contentText.setWrappingWidth(600);
        contentText.setTextAlignment(TextAlignment.CENTER);
        contentArea.getChildren().addAll(titleText, contentText);

        mainLayout.getChildren().addAll(sidebar, contentArea);

        // --- LOGIKA ---
        collapseBtn.setOnAction(e -> {
            mainLayout.getChildren().remove(sidebar);
            expandBtn.setVisible(true);
        });

        expandBtn.setOnAction(e -> {
            mainLayout.getChildren().add(0, sidebar);
            expandBtn.setVisible(false);
        });

        getChildren().addAll(mainLayout, expandBtn);

        lessonList.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() != -1) loadLesson(newVal.intValue());
        });

        loadLesson(currentLessonId);
    }

    private void loadLesson(int id) {
        if (id >= 0 && id < LessonRepository.size()) {
            this.currentLessonId = id;
            LessonRepository.Lesson lesson = LessonRepository.getLesson(id);
            if (lesson != null) {
                titleText.setText(lesson.title().toUpperCase());
                contentText.setText(lesson.content());

                // Synchronizacja z listą
                if (lessonList.getSelectionModel().getSelectedIndex() != id) {
                    lessonList.getSelectionModel().select(id);
                }

                // BLOKADA PRZYCISKÓW:
                // Wyłącz "Poprzednia", jeśli to pierwsza lekcja (id == 0)
                prevBtn.setDisable(id == 0);

                // Wyłącz "Następna", jeśli to ostatnia lekcja (id == rozmiar - 1)
                nextBtn.setDisable(id == LessonRepository.size() - 1);
            }
        }
    }

    private void navigate(int delta) {
        int target = currentLessonId + delta;
        if (target >= 0 && target < LessonRepository.size()) {
            loadLesson(target);
        }
    }
}