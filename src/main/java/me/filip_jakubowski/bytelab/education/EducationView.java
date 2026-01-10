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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EducationView extends StackPane {
    private final Text titleText = new Text();
    private final VBox lessonContentBox = new VBox(25);
    private final ListView<String> lessonList = new ListView<>();
    private final ScrollPane scrollPane = new ScrollPane();
    private final HBox mainLayout = new HBox();
    private final VBox sidebar = new VBox(10);
    private final VBox contentArea = new VBox(25);
    private final Button prevBtn = new Button("<");
    private final Button nextBtn = new Button(">");
    private int currentLessonId;

    public EducationView(int startLessonId) {
        this.currentLessonId = startLessonId;
        getStyleClass().add("root");
        Button expandBtn = new Button(">");
        expandBtn.setPrefSize(30, 30);
        expandBtn.setVisible(false);
        StackPane.setAlignment(expandBtn, Pos.TOP_LEFT);
        StackPane.setMargin(expandBtn, new Insets(10, 0, 0, 10));

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
        contentArea.setAlignment(Pos.TOP_CENTER);
        contentArea.setPadding(new Insets(40));
        HBox.setHgrow(contentArea, Priority.ALWAYS);
        titleText.setFont(Font.font("Consolas", 32));
        titleText.setStyle("-fx-fill: #007acc; -fx-font-weight: bold;");
        lessonContentBox.setAlignment(Pos.TOP_CENTER);
        scrollPane.setContent(lessonContentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        contentArea.getChildren().addAll(titleText, scrollPane);
        mainLayout.getChildren().addAll(sidebar, contentArea);
        collapseBtn.setOnAction(e -> { mainLayout.getChildren().remove(sidebar); expandBtn.setVisible(true); });
        expandBtn.setOnAction(e -> { mainLayout.getChildren().add(0, sidebar); expandBtn.setVisible(false); });
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
                parseAndSetContent(lesson.content());
                lessonList.getSelectionModel().select(id);
                prevBtn.setDisable(id == 0);
                nextBtn.setDisable(id == LessonRepository.size() - 1);
            }
        }
    }

    private void parseAndSetContent(String content) {
        lessonContentBox.getChildren().clear();

        Pattern pattern = Pattern.compile("\\[(BINARY|BINARY:U2|ALU:ADDER|ALU:FULL|ALU:LOGIC|SHIFT:MODULE|REGISTER:MODULE|RAM:MODULE|BUS:MODULE|BUS:COMPLEX|INSTR:VIEW|GATE:[a-z]+)\\]");
        Matcher matcher = pattern.matcher(content);
        int lastEnd = 0;

        while (matcher.find()) {
            addTextSection(content.substring(lastEnd, matcher.start()));
            String tag = matcher.group(1);

            switch (tag) {
                case "BINARY" -> lessonContentBox.getChildren().add(new TheoryBinaryView(false));
                case "BINARY:U2" -> lessonContentBox.getChildren().add(new TheoryBinaryView(true));
                case "ALU:ADDER" -> lessonContentBox.getChildren().add(new TheoryALUView());
                case "ALU:FULL" -> lessonContentBox.getChildren().add(new TheoryALUFullView());
                case "ALU:LOGIC" -> lessonContentBox.getChildren().add(new TheoryALULogicView());
                case "SHIFT:MODULE" -> lessonContentBox.getChildren().add(new TheoryShiftView());
                case "REGISTER:MODULE" -> lessonContentBox.getChildren().add(new TheoryRegisterView());
                case "RAM:MODULE" -> lessonContentBox.getChildren().add(new TheoryRAMView());
                case "BUS:MODULE" -> lessonContentBox.getChildren().add(new TheoryBusView());
                case "BUS:COMPLEX" -> lessonContentBox.getChildren().add(new TheoryComplexBusView());
                case "INSTR:VIEW" -> lessonContentBox.getChildren().add(new TheoryInstructionView());
                default -> {
                    if (tag.startsWith("GATE:")) {
                        lessonContentBox.getChildren().add(new TheoryGateView(tag.split(":")[1]));
                    }
                }
            }
            lastEnd = matcher.end();
        }
        addTextSection(content.substring(lastEnd));
    }

    private void addTextSection(String text) {
        if (text == null || text.trim().isEmpty()) return;
        Text t = new Text(text.trim());
        t.setFont(Font.font("Consolas", 18));
        t.setStyle("-fx-fill: #e0e0e0;");
        t.setWrappingWidth(650);
        t.setTextAlignment(TextAlignment.CENTER);
        lessonContentBox.getChildren().add(t);
    }

    private void navigate(int delta) {
        int target = currentLessonId + delta;
        if (target >= 0 && target < LessonRepository.size()) loadLesson(target);
    }
}