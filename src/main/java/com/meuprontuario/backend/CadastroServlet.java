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

        // SQL ATUALIZADO para usar aspas duplas em "userType", uma boa prática no PostgreSQL
        String sql = "INSERT INTO usuarios (\"userType\", name, cpf, password, age, sexo, alergias, historico_vacinacao, medicamentos_uso_continuo, necessita_insulina) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, (String) data.get("userType"));
            stmt.setString(2, (String) data.get("name"));
            stmt.setString(3, (String) data.get("cpf"));
            stmt.setString(4, (String) data.get("password"));

            // CORREÇÃO: Trata o campo 'age' de forma segura, evitando erros se estiver vazio
            String ageStr = (String) data.get("age");
            if (ageStr != null && !ageStr.isEmpty()) {
                stmt.setInt(5, Integer.parseInt(ageStr));
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setString(6, (String) data.get("sexo"));
            stmt.setString(7, (String) data.getOrDefault("alergias", null));
            stmt.setString(8, (String) data.getOrDefault("historico_vacinacao", null));
            stmt.setString(9, (String) data.getOrDefault("medicamentos_uso_continuo", null));
            
            // CORREÇÃO: Trata o booleano de forma segura
            Object insulinObj = data.get("necessita_insulina");
            stmt.setBoolean(10, "true".equalsIgnoreCase(String.valueOf(insulinObj)));

            stmt.executeUpdate();
            resp.getWriter().write(gson.toJson(Map.of("success", true, "message", "Cadastro realizado com sucesso!")));
        } catch (Exception e) {
            e.printStackTrace(); // Isso imprime o erro completo nos logs do Render
            resp.setStatus(500);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Erro interno no servidor: " + e.getMessage())));
        }
    }
}