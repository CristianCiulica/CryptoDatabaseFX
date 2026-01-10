package bd.cryptodatabasefx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/BD_Platforma_Crypto";
    private static final String USER = "postgres";
    private static final String PASS = "1q2w3e";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}