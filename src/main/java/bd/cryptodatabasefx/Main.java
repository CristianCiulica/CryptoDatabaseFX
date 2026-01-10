package bd.cryptodatabasefx; // Asigură-te că pachetul corespunde cu folderul tău

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class Main extends Application {

    // --- 1. CONFIGURARE BAZA DE DATE ---
    // Numele bazei tale este BD_Platforma_Crypto
    static final String DB_URL = "jdbc:postgresql://localhost:5432/BD_Platforma_Crypto";
    static final String USER = "postgres";

    // !!! ATENȚIE: PUNE PAROLA TA AICI ÎNTRE GHILIMELE !!!
    static final String PASS = "1q2w3e";

    // Componentele grafice
    private TableView<ObservableList<String>> table;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Crypto Platform - Proiect Baze de Date");

        // --- ZONA DIN STÂNGA (MENIUL CU BUTOANE) ---
        VBox menuBox = new VBox(10); // Spațiu de 10px între butoane
        menuBox.setPadding(new Insets(15));
        menuBox.setStyle("-fx-background-color: #e0e0e0;"); // Fundal gri deschis

        // ScrollPane pentru cazul în care fereastra e mică
        ScrollPane scrollPane = new ScrollPane(menuBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setMinWidth(280);

        // Adăugăm un titlu mic în meniu
        Label menuTitle = new Label("Interogări Disponibile:");
        menuTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        menuBox.getChildren().add(menuTitle);

        // --- CELE 10 INTEROGĂRI COMPLEXE ---

        // 1. Join simplu intre Utilizatori si Wallet
        addButton(menuBox, "1. Utilizatori și Portofele",
                "SELECT u.nume_complet, u.email, w.adresa_wallet, w.tip_wallet, w.status " +
                        "FROM utilizatori u " +
                        "JOIN wallet w ON u.id_utilizator = w.id_utilizator");

        // 2. Join Multiplu (Utilizator - Sold - Criptomonedă)
        addButton(menuBox, "2. Balanțe Detaliate (Cine ce are)",
                "SELECT u.username, c.simbol, s.cantitate_disponibila, c.pret_curent " +
                        "FROM solduri s " +
                        "JOIN utilizatori u ON s.id_utilizator = u.id_utilizator " +
                        "JOIN criptomonede c ON s.id_criptomoneda = c.id_moneda " +
                        "ORDER BY u.username ASC");

        // 3. Filtrare cu Join (Utilizatori verificați KYC)
        addButton(menuBox, "3. Utilizatori Verificați (KYC Aprobat)",
                "SELECT u.nume_complet, k.cnp, k.tip_document, k.status_verificare " +
                        "FROM utilizatori u " +
                        "JOIN verificare_kyc k ON u.id_utilizator = k.id_utilizator " +
                        "WHERE k.status_verificare = 'aprobat'");

        // 4. Calcul și Ordonare (Cele mai mari cumpărări ca valoare)
        addButton(menuBox, "4. Top Tranzacții BUY (după Valoare)",
                "SELECT u.username, c.simbol, t.cantitate, t.pret, (t.cantitate * t.pret) AS valoare_totala " +
                        "FROM tranzactii t " +
                        "JOIN utilizatori u ON t.id_utilizator = u.id_utilizator " +
                        "JOIN criptomonede c ON t.id_criptomoneda = c.id_moneda " +
                        "WHERE t.tip_tranzactie = 'BUY' " +
                        "ORDER BY valoare_totala DESC");

        // 5. Join M:N (Lista de Favorite)
        addButton(menuBox, "5. Ce monede urmăresc utilizatorii?",
                "SELECT u.username, c.denumire_completa AS moneda_favorita, c.pret_curent " +
                        "FROM lista_favorite f " +
                        "JOIN utilizatori u ON f.id_utilizator = u.id_utilizator " +
                        "JOIN criptomonede c ON f.id_moneda = c.id_moneda");

        // 6. Funcție de Agregare SUM și GROUP BY (Total Depuneri)
        addButton(menuBox, "6. Raport Financiar (Total Depuneri)",
                "SELECT moneda_fiat, SUM(suma) AS total_incasat " +
                        "FROM operatiuni_financiare " +
                        "WHERE tip_operatiune = 'DEPUNERE' AND status = 'complet' " +
                        "GROUP BY moneda_fiat");

        // 7. Funcție de Agregare COUNT (Activitate Admini)
        addButton(menuBox, "7. Performanță Administratori",
                "SELECT a.nume_admin, COUNT(o.id_operatiune) AS cereri_aprobate " +
                        "FROM administrator a " +
                        "JOIN operatiuni_financiare o ON a.id_admin = o.id_admin_aprobare " +
                        "GROUP BY a.nume_admin");

        // 8. Agregare cu Filtrare (Cât Bitcoin există în total la useri)
        addButton(menuBox, "8. Total Bitcoin deținut de clienți",
                "SELECT c.simbol, SUM(s.cantitate_disponibila) AS total_btc_platforma " +
                        "FROM solduri s " +
                        "JOIN criptomonede c ON s.id_criptomoneda = c.id_moneda " +
                        "WHERE c.simbol = 'BTC' " +
                        "GROUP BY c.simbol");

        // 9. Ordonare Temporală (Istoric preț Ethereum)
        addButton(menuBox, "9. Evoluție Preț Ethereum",
                "SELECT c.simbol, i.data_si_ora_exacta, i.pret_deschidere, i.pret_inchidere " +
                        "FROM istoric_pret i " +
                        "JOIN criptomonede c ON i.id_moneda = c.id_moneda " +
                        "WHERE c.simbol = 'ETH' " +
                        "ORDER BY i.data_si_ora_exacta DESC");

        // 10. Left Join și NULL (Utilizatori inactivi / fără tranzacții)
        addButton(menuBox, "10. Utilizatori Inactivi (Fără Tranzacții)",
                "SELECT u.username, u.email, u.data_inregistrarii " +
                        "FROM utilizatori u " +
                        "LEFT JOIN tranzactii t ON u.id_utilizator = t.id_utilizator " +
                        "WHERE t.id_tranzactie IS NULL");


        // --- ZONA CENTRALĂ (TABELUL) ---
        table = new TableView<>();
        table.setPlaceholder(new Label("Selectează o interogare din meniul din stânga pentru a vedea datele."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Coloanele se lățesc automat

        // --- ZONA DE JOS (STATUS BAR) ---
        statusLabel = new Label("Conectat la: BD_Platforma_Crypto");
        statusLabel.setPadding(new Insets(5));
        statusLabel.setStyle("-fx-text-fill: #333; -fx-background-color: #ccc;");
        statusLabel.setMaxWidth(Double.MAX_VALUE);

        // --- ASAMBLAREA LAYOUT-ULUI ---
        BorderPane layout = new BorderPane();
        layout.setLeft(scrollPane);
        layout.setCenter(table);
        layout.setBottom(statusLabel);

        Scene scene = new Scene(layout, 1100, 650); // Dimensiunea ferestrei
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- METODĂ PENTRU CREAREA UNUI BUTON ---
    private void addButton(VBox container, String title, String sqlQuery) {
        Button btn = new Button(title);
        btn.setMaxWidth(Double.MAX_VALUE); // Butonul se întinde pe toată lățimea
        btn.setPadding(new Insets(10));
        btn.setStyle("-fx-alignment: CENTER-LEFT; -fx-base: #4CAF50; -fx-text-fill: black;"); // Stil vizual

        // Acțiunea la click
        btn.setOnAction(e -> executaInterogare(sqlQuery, title));

        container.getChildren().add(btn);
    }

    // --- LOGICA DE EXECUTARE SQL DINAMIC ---
    private void executaInterogare(String sql, String titlu) {
        table.getColumns().clear(); // Ștergem coloanele vechi
        table.getItems().clear();   // Ștergem datele vechi
        statusLabel.setText("Se execută: " + titlu + "...");

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // 1. Construim coloanele dinamic pe baza rezultatului SQL
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 0; i < columnCount; i++) {
                final int j = i;
                String columnName = metaData.getColumnLabel(i + 1); // Numele coloanei din SQL

                TableColumn<ObservableList<String>, String> col = new TableColumn<>(columnName);

                // Setăm cum să ia datele (ca String)
                col.setCellValueFactory(param ->
                        new SimpleStringProperty(param.getValue().get(j))
                );

                table.getColumns().add(col);
            }

            // 2. Extragem datele rând cu rând
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    String val = rs.getString(i);
                    // Dacă valoarea e null, afișăm textul "-NULL-"
                    row.add(val != null ? val : "-");
                }
                data.add(row);
            }

            table.setItems(data);
            statusLabel.setText("Succes! Interogare: '" + titlu + "' a returnat " + data.size() + " rezultate.");

        } catch (SQLException e) {
            e.printStackTrace();
            // Afișăm eroarea într-o fereastră pop-up
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Eroare Bază de Date");
            alert.setHeaderText("Nu s-a putut executa interogarea.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            statusLabel.setText("Eroare: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}