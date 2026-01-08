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
    private final SplitPane splitPane = new SplitPane();
    private int currentLessonId;
    private double lastDividerPosition = 0.25;

    public EducationView(int startLessonId) {
        this.currentLessonId = startLessonId;
        getStyleClass().add("root");

        // --- PRZYCISK ROZWIJANIA (Widoczny tylko gdy schowane) ---
        Button expandBtn = new Button(">");
        expandBtn.setPrefSize(30, 30);
        expandBtn.setVisible(false);
        StackPane.setAlignment(expandBtn, Pos.TOP_LEFT);
        StackPane.setMargin(expandBtn, new Insets(10, 0, 0, 5));

        // --- SIDEBAR (Lewa strona) ---
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(15));
        sidebar.setMinWidth(0);

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

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);
        Button prevBtn = new Button("<");
        Button menuBtn = new Button("Menu");
        Button nextBtn = new Button(">");
        prevBtn.setPrefWidth(40); menuBtn.setPrefWidth(80); nextBtn.setPrefWidth(40);

        prevBtn.setOnAction(e -> navigate(-1));
        menuBtn.setOnAction(e -> MainApp.getNavigationManager().showStartScreen());
        nextBtn.setOnAction(e -> navigate(1));
        controls.getChildren().addAll(prevBtn, menuBtn, nextBtn);

        sidebar.getChildren().addAll(sidebarHeader, lessonList, controls);

        // --- CONTENT (Prawa strona) ---
        VBox contentArea = new VBox(25);
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setPadding(new Insets(40));
        contentArea.setMinWidth(300);

        titleText.setFont(Font.font("Consolas", 32));
        titleText.setStyle("-fx-fill: #007acc; -fx-font-weight: bold;");

        contentText.setFont(Font.font("Consolas", 18));
        contentText.setStyle("-fx-fill: #e0e0e0;");
        contentText.setWrappingWidth(550);
        contentText.setTextAlignment(TextAlignment.CENTER);
        contentArea.getChildren().addAll(titleText, contentText);

        splitPane.getItems().addAll(sidebar, contentArea);
        splitPane.setDividerPositions(0.25);

        // --- LOGIKA PRZYCISKÓW I BLOKOWANIA SUWAKA ---
        collapseBtn.setOnAction(e -> {
            lastDividerPosition = splitPane.getDividerPositions()[0];
            splitPane.setDividerPositions(0);
            expandBtn.setVisible(true);

            // Wyłączenie możliwości ręcznego przeciągania paska
            sidebar.setMouseTransparent(true);
            sidebar.setManaged(false); // Powoduje, że contentArea zajmuje całą przestrzeń
        });

        expandBtn.setOnAction(e -> {
            sidebar.setManaged(true);
            sidebar.setMouseTransparent(false);
            splitPane.setDividerPositions(lastDividerPosition);
            expandBtn.setVisible(false);
        });

        getChildren().addAll(splitPane, expandBtn);

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
                if (lessonList.getSelectionModel().getSelectedIndex() != id) {
                    lessonList.getSelectionModel().select(id);
                }
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