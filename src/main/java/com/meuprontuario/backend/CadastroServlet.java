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

        // SQL com aspas duplas em "userType" para máxima compatibilidade com PostgreSQL
        String sql = "INSERT INTO usuarios (\"userType\", name, cpf, password, age, sexo, alergias, historico_vacinacao, medicamentos_uso_continuo, necessita_insulina) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, (String) data.get("userType"));
            stmt.setString(2, (String) data.get("name"));
            stmt.setString(3, (String) data.get("cpf"));
            stmt.setString(4, (String) data.get("password"));

            // --- INÍCIO DA CORREÇÃO DEFINITIVA PARA TIPOS DE DADOS ---
            
            // Trata o campo 'age' de forma 100% segura
            Object ageObj = data.get("age");
            if (ageObj != null && !String.valueOf(ageObj).isEmpty()) {
                // Converte para String primeiro, depois para Double, e finalmente para Int
                stmt.setInt(5, Double.valueOf(String.valueOf(ageObj)).intValue());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setString(6, (String) data.get("sexo"));
            stmt.setString(7, (String) data.get("alergias"));
            stmt.setString(8, (String) data.get("historico_vacinacao"));
            stmt.setString(9, (String) data.get("medicamentos_uso_continuo"));
            
            // Trata o booleano de forma segura
            stmt.setBoolean(10, "true".equalsIgnoreCase(String.valueOf(data.get("necessita_insulina"))));

            // --- FIM DA CORREÇÃO ---

            stmt.executeUpdate();
            resp.getWriter().write(gson.toJson(Map.of("success", true, "message", "Cadastro realizado com sucesso!")));

        } catch (Exception e) {
            // Se um erro acontecer, ele será impresso nos logs do Render
            e.printStackTrace(); 
            resp.setStatus(500);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Erro interno no servidor: " + e.getMessage())));
        }
    }
}