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
import java.sql.Statement;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        Map<String, String> data = gson.fromJson(req.getReader(), Map.class);
        String sql = "SELECT * FROM usuarios WHERE cpf = ? AND password = ?";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, data.get("cpf"));
            stmt.setString(2, data.get("password"));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("id", rs.getInt("id"));
                user.put("userType", rs.getString("userType"));
                user.put("name", rs.getString("name"));
                // Adicione outros campos se precisar no front-end
                try (PreparedStatement logStmt = conn.prepareStatement("INSERT INTO logs_acesso (usuario_id, data_hora_acesso, ip_acesso) VALUES (?, NOW(), ?)")) {
                    logStmt.setInt(1, rs.getInt("id"));
                    logStmt.setString(2, req.getRemoteAddr()); // Pega o IP do usuário
                    logStmt.executeUpdate();
                }
                resp.getWriter().write(gson.toJson(Map.of("success", true, "user", user)));
            } else {
                resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "CPF ou senha inválidos.")));
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Erro: " + e.getMessage())));
        }
    }
}