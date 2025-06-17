package com.meuprontuario.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String HOST = System.getenv("DB_HOST");
    private static final String DBNAME = System.getenv("DB_NAME");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");
    
    // *** AQUI ESTÁ A CORREÇÃO ***
    // Adicionamos o parâmetro `?gssEncMode=disable` ao final da URL
    private static final String URL = String.format("jdbc:postgresql://%s/%s?gssEncMode=disable", HOST, DBNAME);

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Erro ao carregar o driver do PostgreSQL", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}