package com.meuprontuario.backend;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        Map<String, String> loginData = gson.fromJson(req.getReader(), Map.class);

        // SQL CORRIGIDO: Seleciona colunas explicitamente, usando aspas duplas em "userType"
        String sql = "SELECT id, \"userType\", name, cpf, age, sexo FROM usuarios WHERE cpf = ? AND password = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, loginData.get("cpf"));
            stmt.setString(2, loginData.get("password"));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Monta o objeto de usuário para retornar ao front-end
                Map<String, Object> user = new HashMap<>();
                user.put("id", rs.getInt("id"));
                user.put("userType", rs.getString("userType"));
                user.put("name", rs.getString("name"));
                user.put("cpf", rs.getString("cpf"));
                
                // Grava o log de acesso
                try (PreparedStatement logStmt = conn.prepareStatement("INSERT INTO logs_acesso (usuario_id, data_hora_acesso, ip_acesso) VALUES (?, NOW(), ?)")) {
                    logStmt.setInt(1, rs.getInt("id"));
                    logStmt.setString(2, req.getRemoteAddr());
                    logStmt.executeUpdate();
                }

                resp.getWriter().write(gson.toJson(Map.of("success", true, "user", user)));
            } else {
                resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "CPF ou senha inválidos.")));
            }

        } catch (Exception e) {
            e.printStackTrace(); // A causa exata do erro 500 será impressa nos logs do Render
            resp.setStatus(500);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Erro interno no servidor.")));
        }
    }
}