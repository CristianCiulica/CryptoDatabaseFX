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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Objects;

public class Main extends Application {

    private Stage primaryStage;
    private String cssPath;

    private TableView<ObservableList<String>> table;
    private Label statusLabel;
    private TextArea sqlEditor;
    private TextArea sqlDisplayArea;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle("Platformă Crypto - Proiect BD");

        try {
            cssPath = Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm();
        } catch (Exception e) {
            System.out.println("Eroare încărcare CSS.");
        }

        StackPane root = new StackPane();
        Scene scene = new Scene(root, 1200, 750);
        if(cssPath != null) scene.getStylesheets().add(cssPath);

        primaryStage.setScene(scene);
        showWelcomeScreen();
        primaryStage.show();
    }

    // --- ECRAN START ---
    private void showWelcomeScreen() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("welcome-root");

        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(750);
        contentBox.getStyleClass().add("welcome-container");

        Label title = new Label("Platformă de Tranzacționare a Criptomonedelor");
        title.getStyleClass().add("welcome-title");
        title.setWrapText(true);
        title.setTextAlignment(TextAlignment.CENTER);

        Label subtitle = new Label("Proiect Baze de Date");
        subtitle.getStyleClass().add("welcome-subtitle");

        Button btnDemo = new Button("Rapoarte Predefinite");
        btnDemo.getStyleClass().add("start-btn");
        btnDemo.setOnAction(e -> showDashboard());

        Button btnCreate = new Button("Editor SQL Custom");
        btnCreate.getStyleClass().add("start-btn");
        btnCreate.setOnAction(e -> showCustomQueryScreen());

        contentBox.getChildren().addAll(title, subtitle, btnDemo, btnCreate);
        root.getChildren().add(contentBox);

        primaryStage.getScene().setRoot(root);
    }

    // --- DASHBOARD ---
    private void showDashboard() {
        VBox menuBox = new VBox(5);
        menuBox.getStyleClass().add("sidebar");

        Label menuTitle = new Label("MENIU ADMINISTRARE");
        menuTitle.getStyleClass().add("menu-title");
        menuBox.getChildren().add(menuTitle);

        Button btnBack = new Button("Înapoi la Start");
        btnBack.getStyleClass().add("menu-button");
        btnBack.getStyleClass().add("back-button");
        btnBack.setOnAction(e -> showWelcomeScreen());
        menuBox.getChildren().add(btnBack);

        ScrollPane scrollPane = new ScrollPane(menuBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setMinWidth(320);
        scrollPane.setStyle("-fx-background: #f8f9fa; -fx-border-color: transparent;");

        // --- BUTOANE ---
        addSectionLabel(menuBox, "INTEROGĂRI DE BAZĂ");
        addButton(menuBox, "Utilizatori & Portofele", "SELECT u.nume_complet, u.email, w.adresa_wallet, w.tip_wallet \nFROM utilizatori u \nJOIN wallet w ON u.id_utilizator = w.id_utilizator");
        addButton(menuBox, "Balanțe Detaliate", "SELECT u.username, c.simbol, s.cantitate_disponibila \nFROM solduri s \nJOIN utilizatori u ON s.id_utilizator = u.id_utilizator \nJOIN criptomonede c ON s.id_criptomoneda = c.id_moneda");
        addButton(menuBox, "Top Tranzacții BUY", "SELECT u.username, c.simbol, t.cantitate, t.pret \nFROM tranzactii t \nJOIN utilizatori u ON t.id_utilizator = u.id_utilizator \nJOIN criptomonede c ON t.id_criptomoneda = c.id_moneda \nWHERE t.tip_tranzactie = 'BUY' \nORDER BY t.cantitate DESC LIMIT 10");

        addSectionLabel(menuBox, "LOGICĂ COMPLEXĂ & ANALIZĂ");
        addButton(menuBox, "1. Utilizatori 'Whales'", "SELECT u.username, SUM(o.suma) as total_depus \nFROM utilizatori u \nJOIN operatiuni_financiare o ON u.id_utilizator = o.id_utilizator \nWHERE o.tip_operatiune = 'DEPUNERE' \nGROUP BY u.username \nHAVING SUM(o.suma) > (SELECT AVG(suma) FROM operatiuni_financiare WHERE tip_operatiune = 'DEPUNERE')");
        addButton(menuBox, "2. Monede fără tranzacții", "SELECT denumire_completa, simbol \nFROM criptomonede \nWHERE id_moneda NOT IN (SELECT DISTINCT id_criptomoneda FROM tranzactii)");
        addButton(menuBox, "3. Cel mai bogat utilizator", "SELECT u.username, u.email, s.valoarea_totala \nFROM utilizatori u \nJOIN solduri s ON u.id_utilizator = s.id_utilizator \nWHERE s.valoarea_totala = (SELECT MAX(valoarea_totala) FROM solduri)");
        addButton(menuBox, "4. Cea mai scumpă achiziție", "SELECT DISTINCT u.username, c.simbol, t.pret \nFROM utilizatori u \nJOIN tranzactii t ON u.id_utilizator = t.id_utilizator \nJOIN criptomonede c ON t.id_criptomoneda = c.id_moneda \nWHERE c.pret_curent = (SELECT MAX(pret_curent) FROM criptomonede)");
        addButton(menuBox, "5. Portofele KYC Aprobat", "SELECT w.adresa_wallet, w.tip_wallet, u.username \nFROM wallet w \nJOIN utilizatori u ON w.id_utilizator = u.id_utilizator \nWHERE w.id_utilizator IN (SELECT id_utilizator FROM verificare_kyc WHERE status_verificare = 'aprobat')");
        addButton(menuBox, "6. Monede Premium", "SELECT simbol, pret_curent \nFROM criptomonede \nWHERE pret_curent > (SELECT AVG(pret_curent) FROM criptomonede) \nORDER BY pret_curent DESC");
        addButton(menuBox, "7. Fără retrageri (HODL)", "SELECT username, email \nFROM utilizatori \nWHERE id_utilizator NOT IN (SELECT DISTINCT id_utilizator FROM operatiuni_financiare WHERE tip_operatiune = 'RETRAGERE')");
        addButton(menuBox, "8. VIP Inactivi", "SELECT username \nFROM utilizatori \nWHERE tip_utilizator = 'VIP' \nAND id_utilizator NOT IN (SELECT DISTINCT id_utilizator FROM tranzactii)");
        addButton(menuBox, "9. Monede Populare", "SELECT c.simbol, SUM(s.cantitate_disponibila) as total_detinut \nFROM solduri s \nJOIN criptomonede c ON s.id_criptomoneda = c.id_moneda \nGROUP BY c.simbol \nHAVING SUM(s.cantitate_disponibila) > (SELECT AVG(cantitate_disponibila) FROM solduri)");
        addButton(menuBox, "10. Useri Noi", "SELECT username, data_inregistrarii \nFROM utilizatori \nWHERE data_inregistrarii > (SELECT MIN(data_si_ora) FROM tranzactii) LIMIT 5");

        sqlDisplayArea = new TextArea();
        sqlDisplayArea.setEditable(false);
        sqlDisplayArea.setWrapText(true);
        sqlDisplayArea.setPrefHeight(120);
        sqlDisplayArea.setPromptText("Selectează un raport...");
        sqlDisplayArea.getStyleClass().add("sql-console");

        table = new TableView<>();
        table.setPlaceholder(new Label("Selectează o interogare din meniu..."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox centerBox = new VBox(0, sqlDisplayArea, table);

        statusLabel = new Label("Sistem conectat.");
        statusLabel.getStyleClass().add("status-bar");
        statusLabel.setMaxWidth(Double.MAX_VALUE);

        BorderPane layout = new BorderPane();
        layout.setLeft(scrollPane);
        layout.setCenter(centerBox);
        layout.setBottom(statusLabel);

        primaryStage.getScene().setRoot(layout);
    }

    // --- EDITOR CUSTOM ---
    private void showCustomQueryScreen() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("editor-container");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Button btnBack = new Button("Înapoi");
        btnBack.getStyleClass().add("menu-button");
        btnBack.getStyleClass().add("back-button");
        btnBack.setOnAction(e -> showWelcomeScreen());

        Label lblTitle = new Label("EDITOR SQL AVANSAT");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #333;");
        header.getChildren().addAll(btnBack, lblTitle);

        sqlEditor = new TextArea();
        sqlEditor.setPromptText("Exemplu: SELECT * FROM utilizatori");
        sqlEditor.setPrefHeight(180);
        sqlEditor.getStyleClass().add("sql-text-area");

        Button btnRun = new Button("Execută Comanda");
        btnRun.getStyleClass().add("start-btn");
        btnRun.setMaxWidth(200);
        btnRun.setOnAction(e -> {
            String customSql = sqlEditor.getText();
            if(!customSql.trim().isEmpty()) executaInterogare(customSql, "Custom Query");
        });

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        statusLabel = new Label("Așteptare...");
        statusLabel.getStyleClass().add("status-bar");
        statusLabel.setMaxWidth(Double.MAX_VALUE);

        root.getChildren().addAll(header, sqlEditor, btnRun, table, statusLabel);

        primaryStage.getScene().setRoot(root);
    }

    private void addSectionLabel(VBox container, String text) {
        Label l = new Label(text);
        l.setMaxWidth(Double.MAX_VALUE);
        l.getStyleClass().add("section-label");
        container.getChildren().add(l);
    }

    private void addButton(VBox container, String title, String sqlQuery) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("menu-button");
        btn.setOnAction(e -> {
            if(sqlDisplayArea != null) sqlDisplayArea.setText(sqlQuery);
            executaInterogare(sqlQuery, title);
        });
        container.getChildren().add(btn);
    }

    // --- AICI AM REPARAT SORTAREA ---
    private void executaInterogare(String sql, String titlu) {
        table.getColumns().clear();
        table.getItems().clear();
        statusLabel.setText("Procesare: " + titlu + "...");
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

                        // --- FIX PENTRU SORTARE NUMERICĂ ---
                        col.setComparator((s1, s2) -> {
                            if (s1 == null && s2 == null) return 0;
                            if (s1 == null) return -1;
                            if (s2 == null) return 1;
                            // Încercăm să le comparăm ca numere
                            try {
                                double d1 = Double.parseDouble(s1);
                                double d2 = Double.parseDouble(s2);
                                return Double.compare(d1, d2);
                            } catch (NumberFormatException e) {
                                // Dacă nu sunt numere (ex: nume, email), le comparăm ca text normal
                                return s1.compareToIgnoreCase(s2);
                            }
                        });

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
                    statusLabel.setText("✅ Rezultate: " + data.size());
                }
            } else {
                int rows = pstmt.getUpdateCount();
                statusLabel.setText("✅ Comandă executată. Rânduri afectate: " + rows);
            }
        } catch (SQLException e) {
            statusLabel.setText("❌ Eroare: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}