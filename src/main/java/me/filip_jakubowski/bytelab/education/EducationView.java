package me.filip_jakubowski.bytelab.education;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import me.filip_jakubowski.bytelab.MainApp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.text.TextFlow;
import javafx.scene.text.FontWeight;
import me.filip_jakubowski.bytelab.logicgame.LogicGameView;

public class EducationView extends StackPane {
    private final Text titleText = new Text();
    private final VBox lessonContentBox = new VBox(25);
    private final VBox sidebarListContainer = new VBox(2);
    private final ScrollPane scrollPane = new ScrollPane();
    private final HBox mainLayout = new HBox();
    private final VBox sidebar = new VBox(10);
    private final VBox contentArea = new VBox(25);
    private final Button prevBtn = new Button("<");
    private final Button nextBtn = new Button(">");
    private int currentLessonId;

    // Stałe kolorystyczne dla podświetlenia
    private final String STYLE_NORMAL = "-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-cursor: hand; -fx-padding: 5 10; -fx-alignment: CENTER_LEFT;";
    private final String STYLE_ACTIVE = "-fx-background-color: #34495e; -fx-text-fill: #3498db; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 5 10; -fx-alignment: CENTER_LEFT;";

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

        ScrollPane sidebarScroll = new ScrollPane(sidebarListContainer);
        sidebarScroll.setFitToWidth(true);
        sidebarScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(sidebarScroll, Priority.ALWAYS);

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

        sidebar.getChildren().addAll(sidebarHeader, sidebarScroll, controls);

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

        loadLesson(currentLessonId); // loadLesson wywoła refreshSidebar, więc kolejność ma znaczenie
    }

    private void refreshSidebar(boolean isLessonMode, int activeId) {
        sidebarListContainer.getChildren().clear();

        sidebarListContainer.getChildren().add(createSectionLabel("LEKCJE TEORETYCZNE"));
        for (int i = 0; i < LessonRepository.size(); i++) {
            Button btn = createMenuButton(LessonRepository.getLesson(i).title(), i, true);
            if (isLessonMode && i == activeId) btn.setStyle(STYLE_ACTIVE);
            sidebarListContainer.getChildren().add(btn);
        }

        sidebarListContainer.getChildren().add(createSectionLabel("INSTRUKCJE OBSŁUGI"));
        for (int i = 0; i < InstructionRepository.size(); i++) {
            Button btn = createMenuButton(InstructionRepository.getManual(i).title(), i, false);
            if (!isLessonMode && i == activeId) btn.setStyle(STYLE_ACTIVE);
            sidebarListContainer.getChildren().add(btn);
        }
    }

    private Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-padding: 15 5 5 5; -fx-font-size: 13px;");
        return label;
    }

    private Button createMenuButton(String title, int id, boolean isLesson) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(STYLE_NORMAL);
        btn.setOnAction(e -> {
            if (isLesson) loadLesson(id);
            else loadManual(id);
        });
        return btn;
    }

    private void loadLesson(int id) {
        if (id >= 0 && id < LessonRepository.size()) {
            this.currentLessonId = id;
            LessonRepository.Lesson lesson = LessonRepository.getLesson(id);
            if (lesson != null) {
                titleText.setText(lesson.title().toUpperCase());
                parseAndSetContent(lesson.content());
                prevBtn.setDisable(id == 0);
                nextBtn.setDisable(id == LessonRepository.size() - 1);
                refreshSidebar(true, id); // Aktualizacja podświetlenia
            }
        }
    }

    private void loadManual(int id) {
        InstructionRepository.Manual manual = InstructionRepository.getManual(id);
        if (manual != null) {
            titleText.setText(manual.title().toUpperCase());
            parseAndSetContent(manual.content());
            prevBtn.setDisable(true);
            nextBtn.setDisable(true);
            refreshSidebar(false, id); // Aktualizacja podświetlenia
        }
    }

    private void parseAndSetContent(String content) {
        lessonContentBox.getChildren().clear();

        Pattern pattern = Pattern.compile("\\[(BINARY|BINARY:U2|ALU:ADDER|ALU:FULL|ALU:LOGIC|SHIFT:MODULE|REGISTER:MODULE|RAM:MODULE|BUS:MODULE|BUS:COMPLEX|INSTR:VIEW|TABLE:ISA_OPCODES|TABLE:REGISTERS_SHORT|OPCODE:DECODER|LOGIC_GAME:MODULE|COMPILER:MODULE|ARCH:COMPARISON|PC:MODULE|ADVANCED_PC:MODULE|GATE:[a-z]+)\\]");
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
                case "TABLE:ISA_OPCODES" -> lessonContentBox.getChildren().add(createISATable());
                case "OPCODE:DECODER" -> lessonContentBox.getChildren().add(new TheoryOpcodeDecoderView());
                case "COMPILER:MODULE" -> lessonContentBox.getChildren().add(new TheoryInstructionCompilerView());
                case "ARCH:COMPARISON" -> lessonContentBox.getChildren().add(new TheoryArchView());
                case "PC:MODULE" -> lessonContentBox.getChildren().add(new TheoryPCView());
                case "TABLE:REGISTERS_SHORT" -> lessonContentBox.getChildren().add(createRegistersTable());
                case "ADVANCED_PC:MODULE" -> lessonContentBox.getChildren().add(new TheoryPCAdvancedView());
                case "LOGIC_GAME:MODULE" -> {
                    LogicGameView game = new LogicGameView();
                    game.setBottom(null);
                    Button resetBtn = new Button("RESETUJ PLANSZĘ");
                    resetBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-weight: bold;");
                    resetBtn.setOnAction(e -> game.resetGame());
                    VBox container = new VBox(15, game, resetBtn);
                    container.setAlignment(Pos.CENTER);
                    lessonContentBox.getChildren().add(container);
                }
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
        if (text == null || text.isEmpty()) return;
        TextFlow textFlow = new TextFlow();
        textFlow.setLineSpacing(5.0);
        textFlow.setPadding(new Insets(10, 0, 10, 0));
        String[] parts = text.split("\\*\\*");
        for (int i = 0; i < parts.length; i++) {
            Text segment = new Text(parts[i]);
            segment.setFill(Color.WHITE);
            segment.setFont(Font.font("System", i % 2 != 0 ? FontWeight.BOLD : FontWeight.NORMAL, i % 2 != 0 ? 17 : 16));
            textFlow.getChildren().add(segment);
        }
        lessonContentBox.getChildren().add(textFlow);
    }

    private VBox createISATable() {
        VBox table = new VBox(1);
        table.setStyle("-fx-border-color: #444; -fx-background-color: #1a1a1a; -fx-border-radius: 5;");
        table.setMaxWidth(750);
        table.setAlignment(Pos.CENTER);
        HBox header = new HBox(10,
                createTableCell("INSTRUKCJA", 100, true),
                createTableCell("OPCODE", 80, true),
                createTableCell("DANE", 120, true),
                createTableCell("CEL", 120, true),
                createTableCell("PRZYKŁAD", 200, true)
        );
        header.setStyle("-fx-background-color: #2c3e50; -fx-padding: 8; -fx-background-radius: 5 5 0 0;");
        header.setAlignment(Pos.CENTER_LEFT);
        table.getChildren().add(header);
        String[][] data = {
                {"NOP",  "0x0", "---", "---", "No Operation"},
                {"IN",   "0x1", "INPUT HEX", "REG A-E", "0xFF -> REG A"},
                {"OUT",  "0x2", "REG A-E", "---", "REG B -> OUT"},
                {"ADD",  "0x3", "---", "REG A-E", "A + B -> REG C"},
                {"SUB",  "0x4", "---", "REG A-E", "A - B -> REG A"},
                {"AND",  "0x5", "---", "REG A-E", "A AND B -> REG D"},
                {"NAND", "0x6", "---", "REG A-E", "A NAND B"},
                {"OR",   "0x7", "---", "REG A-E", "A OR B"},
                {"NOR",  "0x8", "---", "REG A-E", "A NOR B"},
                {"XOR",  "0x9", "---", "REG A-E", "A XOR B"},
                {"NOT",  "0xA", "REG A-B", "REG A-E", "NOT REG A"},
                {"MOV",  "0xB", "REG A-E", "REG A-E", "REG A -> REG B"},
                {"JUMP", "0xC", "---", "ADRES", "Jump 0x05"},
                {"JZ",   "0xD", "---", "ADRES", "Jump if Zero"}
        };
        for (String[] row : data) {
            HBox rowUI = new HBox(10,
                    createTableCell(row[0], 100, false),
                    createTableCell(row[1], 80, false),
                    createTableCell(row[2], 120, false),
                    createTableCell(row[3], 120, false),
                    createTableCell(row[4], 200, false)
            );
            rowUI.setStyle("-fx-padding: 5; -fx-border-color: #333; -fx-border-width: 0 0 1 0;");
            rowUI.setAlignment(Pos.CENTER_LEFT);
            table.getChildren().add(rowUI);
        }
        return table;
    }

    private VBox createRegistersTable() {
        VBox table = new VBox(1);
        table.setStyle("-fx-border-color: #444; -fx-background-color: #1a1a1a; -fx-border-radius: 5;");
        table.setMaxWidth(600);
        table.setAlignment(Pos.CENTER);

        double[] widths = {120, 180, 250};

        HBox header = new HBox(10,
                createTableCell("REJESTR", widths[0], true),
                createTableCell("NAZWA PEŁNA", widths[1], true),
                createTableCell("FUNKCJA", widths[2], true)
        );
        header.setStyle("-fx-background-color: #2c3e50; -fx-padding: 8; -fx-background-radius: 5 5 0 0;");
        table.getChildren().add(header);

        String[][] data = {
                {"REG 0", "Zero Register", "Zawsze zwraca 0x0000"},
                {"REG A", "Accumulator A", "Główny rejestr operacyjny"},
                {"REG B", "Accumulator B", "Drugi argument operacji ALU"},
                {"REG C", "General Purpose", "Przechowywanie danych"},
                {"REG D", "General Purpose", "Przechowywanie danych"},
                {"REG E", "General Purpose", "Przechowywanie danych"},
                {"PC",    "Program Counter", "Adres następnej instrukcji"},
                {"OUT",   "Output Reg", "Wyjście symulatora"}
        };

        for (String[] row : data) {
            HBox rowUI = new HBox(10,
                    createTableCell(row[0], widths[0], false),
                    createTableCell(row[1], widths[1], false),
                    createTableCell(row[2], widths[2], false)
            );
            rowUI.setStyle("-fx-padding: 5; -fx-border-color: #333; -fx-border-width: 0 0 1 0;");
            table.getChildren().add(rowUI);
        }
        return table;
    }

    private StackPane createTableCell(String val, double w, boolean header) {
        Text t = new Text(val);
        t.setFill(header ? Color.web("#3498db") : Color.WHITE);
        t.setFont(Font.font("Consolas", header ? FontWeight.BOLD : FontWeight.NORMAL, 13));

        StackPane sp = new StackPane(t);
        sp.setMinWidth(w);
        sp.setPrefWidth(w);
        sp.setMaxWidth(w);
        sp.setAlignment(Pos.CENTER_LEFT);

        return sp;
    }

    private void navigate(int delta) {
        int target = currentLessonId + delta;
        if (target >= 0 && target < LessonRepository.size()) loadLesson(target);
    }
}