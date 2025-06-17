package com.meuprontuario.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    // As credenciais virão das variáveis de ambiente do servidor Render
    private static final String HOST = System.getenv("DB_HOST");
    private static final String DBNAME = System.getenv("DB_NAME");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");
    
    // *** URL DE CONEXÃO FINAL E ROBUSTA ***
    // Adiciona timeouts e a configuração de segurança completa para o Neon.
    private static final String URL = String.format(
        "jdbc:postgresql://%s/%s?sslmode=require&connectTimeout=30&socketTimeout=30",
        HOST,
        DBNAME
    );

    static {
        try {
            // Carrega o driver do PostgreSQL
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            // Se o driver não for encontrado, o erro será claro
            throw new RuntimeException("Driver JDBC do PostgreSQL não encontrado. Verifique o pom.xml.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        // Este método agora é mais paciente e robusto
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
