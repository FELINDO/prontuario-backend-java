package com.meuprontuario.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String HOST = System.getenv("DB_HOST");
    private static final String DBNAME = System.getenv("DB_NAME");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");
    
    // URL de conexão final e robusta para o Neon
    private static final String URL = String.format(
        "jdbc:postgresql://%s/%s?sslmode=require&connectTimeout=30&socketTimeout=30",
        HOST,
        DBNAME
    );

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC do PostgreSQL não encontrado. Verifique o pom.xml.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
