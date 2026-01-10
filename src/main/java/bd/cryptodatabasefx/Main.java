package bd.cryptodatabasefx;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Objects;

public class Main extends Application {

    // --- CONFIGURARE BAZA DE DATE ---
    static final String DB_URL = "jdbc:postgresql://localhost:5432/BD_Platforma_Crypto";
    static final String USER = "postgres";
    static final String PASS = "1q2w3e"; // <--- PAROLA TA ACTUALIZATA

    private Stage primaryStage;
    private String cssPath;

    // Componente refolosibile
    private TableView<ObservableList<String>> table;
    private Label statusLabel;
    private TextArea sqlEditor; // Zona unde scrii cod manual

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle("Crypto Platform Manager 3.0");

        try {
            cssPath = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
        } catch (Exception e) {
            System.out.println("Eroare incarcare CSS. Verifica folderul resources.");
        }

        showWelcomeScreen();
        primaryStage.show();
    }

    // --- SCENA 1: PAGINA DE START ---
    private void showWelcomeScreen() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("welcome-root");

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(600);
        contentBox.getStyleClass().add("welcome-container");

        Label title = new Label("Crypto Manager Platform");
        title.getStyleClass().add("welcome-title");

        Label subtitle = new Label("Sistem Avansat de Gestiune & Raportare");
        subtitle.getStyleClass().add("welcome-subtitle");

        Button btnDemo = new Button("Alege Interogari Predefinite");
        btnDemo.getStyleClass().add("start-btn");
        btnDemo.setOnAction(e -> showDashboard());

        Button btnCreate = new Button("Editor SQL (Interogare Custom)");
        btnCreate.getStyleClass().add("start-btn");
        // ACUM BUTONUL DUCE LA ECRANUL DE EDITOR
        btnCreate.setOnAction(e -> showCustomQueryScreen());

        contentBox.getChildren().addAll(title, subtitle, btnDemo, btnCreate);
        root.getChildren().add(contentBox);

        Scene welcomeScene = new Scene(root, 1000, 600);
        if(cssPath != null) welcomeScene.getStylesheets().add(cssPath);
        primaryStage.setScene(welcomeScene);
    }

    // --- SCENA 2: DASHBOARD (MENIU + TABEL) ---
    private void showDashboard() {
        VBox menuBox = new VBox(5);
        menuBox.getStyleClass().add("sidebar");

        Label menuTitle = new Label("RAPOARTE DISPONIBILE");
        menuTitle.getStyleClass().add("menu-title");
        menuBox.getChildren().add(menuTitle);

        Button btnBack = new Button("Inapoi la Start");
        btnBack.getStyleClass().add("menu-button");
        btnBack.getStyleClass().add("back-button");
        btnBack.setOnAction(e -> showWelcomeScreen());
        menuBox.getChildren().add(btnBack);

        ScrollPane scrollPane = new ScrollPane(menuBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setMinWidth(300); // Mai lat pentru titlurile lungi
        scrollPane.setStyle("-fx-background: #f4f4f4; -fx-border-color: transparent;");

        // --- INTEROGARI SIMPLE (OLD) ---
        addSectionLabel(menuBox, "--- DE BAZA (JOIN) ---");

        addButton(menuBox, "Utilizatori & Portofele",
                "SELECT u.nume_complet, u.email, w.adresa_wallet, w.tip_wallet FROM utilizatori u JOIN wallet w ON u.id_utilizator = w.id_utilizator");

        addButton(menuBox, "Balante Detaliate",
                "SELECT u.username, c.simbol, s.cantitate_disponibila FROM solduri s JOIN utilizatori u ON s.id_utilizator = u.id_utilizator JOIN criptomonede c ON s.id_criptomoneda = c.id_moneda");

        addButton(menuBox, "Top Tranzactii BUY",
                "SELECT u.username, c.simbol, t.cantitate, t.pret FROM tranzactii t JOIN utilizatori u ON t.id_utilizator = u.id_utilizator JOIN criptomonede c ON t.id_criptomoneda = c.id_moneda WHERE t.tip_tranzactie = 'BUY' ORDER BY t.cantitate DESC LIMIT 10");

        // --- INTEROGARI AVANSATE (NEW - SUBQUERIES) ---
        addSectionLabel(menuBox, "--- AVANSATE (SUBQUERIES) ---");

        addButton(menuBox, "1. Utilizatori 'Whales' (Depuneri > Medie)",
                "SELECT u.username, SUM(o.suma) as total_depus FROM utilizatori u JOIN operatiuni_financiare o ON u.id_utilizator = o.id_utilizator WHERE o.tip_operatiune = 'DEPUNERE' GROUP BY u.username HAVING SUM(o.suma) > (SELECT AVG(suma) FROM operatiuni_financiare WHERE tip_operatiune = 'DEPUNERE')");

        addButton(menuBox, "2. Monede fara tranzactii (Nevandute)",
                "SELECT denumire_completa, simbol FROM criptomonede WHERE id_moneda NOT IN (SELECT DISTINCT id_criptomoneda FROM tranzactii)");

        addButton(menuBox, "3. Cel mai bogat utilizator (Max Sold)",
                "SELECT u.username, u.email, s.valoarea_totala FROM utilizatori u JOIN solduri s ON u.id_utilizator = s.id_utilizator WHERE s.valoarea_totala = (SELECT MAX(valoarea_totala) FROM solduri)");

        addButton(menuBox, "4. Cine a cumparat cea mai scumpa moneda?",
                "SELECT DISTINCT u.username, c.simbol FROM utilizatori u JOIN tranzactii t ON u.id_utilizator = t.id_utilizator JOIN criptomonede c ON t.id_criptomoneda = c.id_moneda WHERE c.pret_curent = (SELECT MAX(pret_curent) FROM criptomonede)");

        addButton(menuBox, "5. Portofelele userilor aprobati KYC",
                "SELECT w.adresa_wallet, w.tip_wallet, u.username FROM wallet w JOIN utilizatori u ON w.id_utilizator = u.id_utilizator WHERE w.id_utilizator IN (SELECT id_utilizator FROM verificare_kyc WHERE status_verificare = 'aprobat')");

        addButton(menuBox, "6. Monede 'Premium' (Pret > Medie)",
                "SELECT simbol, pret_curent FROM criptomonede WHERE pret_curent > (SELECT AVG(pret_curent) FROM criptomonede) ORDER BY pret_curent DESC");

        addButton(menuBox, "7. Utilizatori care NU au retras bani",
                "SELECT username, email FROM utilizatori WHERE id_utilizator NOT IN (SELECT DISTINCT id_utilizator FROM operatiuni_financiare WHERE tip_operatiune = 'RETRAGERE')");

        addButton(menuBox, "8. Useri VIP fara tranzactii",
                "SELECT username FROM utilizatori WHERE tip_utilizator = 'VIP' AND id_utilizator NOT IN (SELECT DISTINCT id_utilizator FROM tranzactii)");

        addButton(menuBox, "9. Monede populare (Detinute > Medie)",
                "SELECT c.simbol, SUM(s.cantitate_disponibila) as total_detinut FROM solduri s JOIN criptomonede c ON s.id_criptomoneda = c.id_moneda GROUP BY c.simbol HAVING SUM(s.cantitate_disponibila) > (SELECT AVG(cantitate_disponibila) FROM solduri)");

        addButton(menuBox, "10. Useri inregistrati recent (Dupa ultima tranzactie)",
                "SELECT username, data_inregistrarii FROM utilizatori WHERE data_inregistrarii > (SELECT MIN(data_si_ora) FROM tranzactii) LIMIT 5");


        // Init Table
        table = new TableView<>();
        table.setPlaceholder(new Label("Selecteaza un raport din stanga..."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        statusLabel = new Label("Dashboard activ.");
        statusLabel.getStyleClass().add("status-bar");
        statusLabel.setMaxWidth(Double.MAX_VALUE);

        BorderPane layout = new BorderPane();
        layout.setLeft(scrollPane);
        layout.setCenter(table);
        layout.setBottom(statusLabel);

        Scene scene = new Scene(layout, 1200, 700);
        if(cssPath != null) scene.getStylesheets().add(cssPath);
        primaryStage.setScene(scene);
    }

    // --- SCENA 3: EDITOR SQL CUSTOM ---
    private void showCustomQueryScreen() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("editor-container");

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Button btnBack = new Button("Inapoi");
        btnBack.getStyleClass().add("menu-button");
        btnBack.getStyleClass().add("back-button"); // Rosu
        btnBack.setOnAction(e -> showWelcomeScreen());

        Label lblTitle = new Label("EDITOR SQL AVANSAT");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #333;");

        header.getChildren().addAll(btnBack, lblTitle);

        // Zona de text input
        sqlEditor = new TextArea();
        sqlEditor.setPromptText("Scrie comanda ta SQL aici... (ex: SELECT * FROM utilizatori)");
        sqlEditor.setPrefHeight(150);
        sqlEditor.getStyleClass().add("sql-text-area");

        // Buton Executa
        Button btnRun = new Button("Executa Comanda SQL");
        btnRun.getStyleClass().add("start-btn"); // Stilul albastru
        btnRun.setStyle("-fx-background-color: #28a745;"); // Il facem verde
        btnRun.setMaxWidth(200);
        btnRun.setOnAction(e -> {
            String customSql = sqlEditor.getText();
            if(customSql.trim().isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Scrie intai o comanda SQL!");
                a.show();
                return;
            }
            executaInterogare(customSql, "Custom Query");
        });

        // Tabelul pentru rezultate (il refacem aici)
        table = new TableView<>();
        table.setPlaceholder(new Label("Rezultatele vor aparea aici..."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        statusLabel = new Label("Asteptare comanda...");
        statusLabel.getStyleClass().add("status-bar");
        statusLabel.setMaxWidth(Double.MAX_VALUE);

        root.getChildren().addAll(header, sqlEditor, btnRun, table, statusLabel);

        Scene scene = new Scene(root, 1000, 700);
        if(cssPath != null) scene.getStylesheets().add(cssPath);
        primaryStage.setScene(scene);
    }

    // --- Helper Methods ---

    private void addSectionLabel(VBox container, String text) {
        Label l = new Label(text);
        l.getStyleClass().add("section-label");
        container.getChildren().add(l);
    }

    private void addButton(VBox container, String title, String sqlQuery) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("menu-button");
        btn.setOnAction(e -> executaInterogare(sqlQuery, title));
        container.getChildren().add(btn);
    }

    private void executaInterogare(String sql, String titlu) {
        table.getColumns().clear();
        table.getItems().clear();
        statusLabel.setText("Se executa: " + titlu + "...");

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Verificam daca e SELECT sau altceva (INSERT/UPDATE)
            boolean isResultSet = pstmt.execute();

            if(isResultSet) {
                try(ResultSet rs = pstmt.getResultSet()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 0; i < columnCount; i++) {
                        final int j = i;
                        String columnName = metaData.getColumnLabel(i + 1).toUpperCase();
                        TableColumn<ObservableList<String>, String> col = new TableColumn<>(columnName);
                        col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j)));
                        table.getColumns().add(col);
                    }

                    while (rs.next()) {
                        ObservableList<String> row = FXCollections.observableArrayList();
                        for (int i = 1; i <= columnCount; i++) {
                            String val = rs.getString(i);
                            row.add(val != null ? val : "-");
                        }
                        data.add(row);
                    }
                    table.setItems(data);
                    statusLabel.setText("Succes: " + data.size() + " randuri returnate.");
                }
            } else {
                // Pentru INSERT/UPDATE/DELETE
                int rows = pstmt.getUpdateCount();
                statusLabel.setText("Comanda executata cu succes. Randuri afectate: " + rows);
                table.setPlaceholder(new Label("Comanda de modificare executata cu succes!"));
            }

        } catch (SQLException e) {
            // Nu arata popup pentru orice eroare mica, doar status
            statusLabel.setText("Eroare SQL: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Eroare SQL");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}