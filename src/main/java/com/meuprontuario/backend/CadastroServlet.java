package com.meuprontuario.backend;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Map;

@WebServlet("/api/cadastrar")
public class CadastroServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        Map<String, Object> data = gson.fromJson(req.getReader(), Map.class);

        String sql = "INSERT INTO usuarios (\"userType\", name, cpf, password, age, sexo, alergias, historico_vacinacao, medicamentos_uso_continuo, necessita_insulina) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, (String) data.get("userType"));
            stmt.setString(2, (String) data.get("name"));
            stmt.setString(3, (String) data.get("cpf"));
            stmt.setString(4, (String) data.get("password"));

            Object ageObj = data.get("age");
            if (ageObj != null && !String.valueOf(ageObj).isEmpty()) {
                stmt.setInt(5, Double.valueOf(String.valueOf(ageObj)).intValue());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setString(6, (String) data.get("sexo"));
            stmt.setString(7, (String) data.getOrDefault("alergias", null));
            stmt.setString(8, (String) data.getOrDefault("historico_vacinacao", null));
            stmt.setString(9, (String) data.getOrDefault("medicamentos_uso_continuo", null));
            stmt.setBoolean(10, "true".equalsIgnoreCase(String.valueOf(data.get("necessita_insulina"))));

            stmt.executeUpdate();
            resp.getWriter().write(gson.toJson(Map.of("success", true, "message", "Cadastro realizado com sucesso!")));

        } catch (Exception e) {
            // --- MUDANÇA CRÍTICA AQUI TAMBÉM ---
            e.printStackTrace();
            resp.setStatus(500);
            // Enviamos o erro real no JSON
            String errorMessage = "ERRO NO SERVIDOR: " + e.getClass().getName() + " - " + e.getMessage();
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", errorMessage)));
        }
    }
}