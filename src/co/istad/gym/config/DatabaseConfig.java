package co.istad.gym.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class  DatabaseConfig {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "qwer";

    public static Connection getConnection() throws Exception {

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void testConnection() {
    }

    public static void closeConnection() {
    }
}
