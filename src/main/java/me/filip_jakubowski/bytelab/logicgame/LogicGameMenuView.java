package me.filip_jakubowski.bytelab.logicgame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import me.filip_jakubowski.bytelab.MainApp;

public class LogicGameMenuView extends HBox {

    private final VBox sidebar = new VBox(10);
    private final VBox sidebarListContainer = new VBox(2);
    private final VBox contentArea = new VBox(25);
    private final Text titleText = new Text();
    private final VBox gameDescriptionBox = new VBox(25);
    private final ScrollPane scrollPane = new ScrollPane();

    private final Button prevBtn = new Button("<");
    private final Button nextBtn = new Button(">");
    private int currentGameId = 0;
    private final int TOTAL_GAMES = 2; // Ilość gier

    private final String STYLE_NORMAL = "-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-cursor: hand; -fx-padding: 5 10; -fx-alignment: CENTER_LEFT;";
    private final String STYLE_ACTIVE = "-fx-background-color: #34495e; -fx-text-fill: #3498db; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 5 10; -fx-alignment: CENTER_LEFT;";

    public LogicGameMenuView() {
        // --- SIDEBAR (Kopia układu z EducationView) ---
        sidebar.setPadding(new Insets(15));
        sidebar.setPrefWidth(250);
        sidebar.setMinWidth(250);
        sidebar.setStyle("-fx-background-color: #1e1e1e;");

        Label sidebarLabel = new Label("DOSTĘPNE GRY");
        sidebarLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #888888;");

        ScrollPane sidebarScroll = new ScrollPane(sidebarListContainer);
        sidebarScroll.setFitToWidth(true);
        sidebarScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(sidebarScroll, Priority.ALWAYS);

        // Dolne kontrolki (Menu + Strzałki)
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

        sidebar.getChildren().addAll(sidebarLabel, sidebarScroll, controls);

        // --- CONTENT AREA (Kopia układu z EducationView) ---
        contentArea.setAlignment(Pos.TOP_CENTER);
        contentArea.setPadding(new Insets(40));
        HBox.setHgrow(contentArea, Priority.ALWAYS);
        contentArea.setStyle("-fx-background-color: #252526;");

        titleText.setFont(Font.font("Consolas", 32));
        titleText.setStyle("-fx-fill: #007acc; -fx-font-weight: bold;");

        gameDescriptionBox.setAlignment(Pos.TOP_CENTER);
        scrollPane.setContent(gameDescriptionBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        contentArea.getChildren().addAll(titleText, scrollPane);

        getChildren().addAll(sidebar, contentArea);

        refreshSidebar();
        loadGameInfo(0);
    }

    private void refreshSidebar() {
        sidebarListContainer.getChildren().clear();

        // Sekcja gier
        Label sectionLabel = new Label("LOGIC CHALLENGES");
        sectionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-padding: 15 5 5 5; -fx-font-size: 13px;");
        sidebarListContainer.getChildren().add(sectionLabel);

        sidebarListContainer.getChildren().add(createMenuButton("Klasyczne 0 i 1", 0));
        sidebarListContainer.getChildren().add(createMenuButton("Magistrale (Beta)", 1));
    }

    private Button createMenuButton(String title, int id) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(id == currentGameId ? STYLE_ACTIVE : STYLE_NORMAL);
        btn.setOnAction(e -> loadGameInfo(id));
        return btn;
    }

    private void loadGameInfo(int id) {
        this.currentGameId = id;
        gameDescriptionBox.getChildren().clear();
        refreshSidebar();
        updateNavigationButtons();

        if (id == 0) {
            titleText.setText("KLASYCZNE ZERA I JEDYNKI");
            addTextSection("Tradycyjne **Kółko i Krzyżyk** przeniesione w świat bramek logicznych. Zamiast stawiać znaki, musisz je obliczyć!");
            addTextSection("**Zasady gry:**\n" +
                    "1. Wybierz bramkę z lewego panelu.\n" +
                    "2. Podaj dwa sygnały wejściowe (0 lub 1) z prawego panelu.\n" +
                    "3. Wynik operacji (0 lub 1) staje się Twoim symbolem na planszy.\n" +
                    "4. Wygrywa gracz, który pierwszy ułoży linię 3 takich samych wyników.");

            Button startBtn = createStartButton();
            startBtn.setOnAction(e -> MainApp.getNavigationManager().showLogicGame());
            gameDescriptionBox.getChildren().add(startBtn);

        } else if (id == 1) {
            titleText.setText("MAGISTRALE I PRZEŁĄCZNIKI");
            addTextSection("Wersja zaawansowana wprowadzająca pojęcie **szyny danych (Bus)** oraz **switchy**.");
            addTextSection("**Co nowego?**\n" +
                    "W tej wersji nie przeciągasz wartości bezpośrednio. Operujesz na magistralach, które mogą być współdzielone. " +
                    "Musisz zarządzać dostępem do szyny, aby uniknąć konfliktów logicznych.");

            Button startBtn = createStartButton();
            startBtn.setText("WKRÓTCE DOSTĘPNE");
            startBtn.setDisable(true);
            gameDescriptionBox.getChildren().add(startBtn);
        }
    }

    // Metoda pomocnicza identyczna jak w EducationView dla zachowania stylu tekstu
    private void addTextSection(String text) {
        TextFlow textFlow = new TextFlow();
        textFlow.setLineSpacing(5.0);
        textFlow.setPadding(new Insets(10, 0, 10, 0));

        String[] parts = text.split("\\*\\*");
        for (int i = 0; i < parts.length; i++) {
            Text segment = new Text(parts[i]);
            segment.setFill(Color.WHITE);
            segment.setFont(Font.font("System", i % 2 != 0 ? FontWeight.BOLD : FontWeight.NORMAL, 16));
            textFlow.getChildren().add(segment);
        }
        gameDescriptionBox.getChildren().add(textFlow);
    }

    private Button createStartButton() {
        Button btn = new Button("ROZPOCZNIJ GRĘ");
        btn.setPrefSize(250, 50);
        btn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-cursor: hand;");
        return btn;
    }

    private void navigate(int delta) {
        int target = currentGameId + delta;
        if (target >= 0 && target < TOTAL_GAMES) {
            loadGameInfo(target);
        }
    }

    private void updateNavigationButtons() {
        prevBtn.setDisable(currentGameId <= 0);
        nextBtn.setDisable(currentGameId >= TOTAL_GAMES - 1);
    }
}