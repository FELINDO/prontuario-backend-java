package com.meuprontuario.backend;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/carregarProntuario")
public class CarregarProntuarioServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        int pacienteId = Integer.parseInt(req.getParameter("id"));
        Map<String, Object> prontuarioCompleto = new HashMap<>();
        Gson gson = new Gson();

        try (Connection conn = DatabaseUtil.getConnection()) {
            // 1. Busca os dados do perfil do paciente
            String sqlPerfil = "SELECT * FROM usuarios WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPerfil)) {
                stmt.setInt(1, pacienteId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Map<String, Object> perfil = new HashMap<>();
                    perfil.put("id", rs.getInt("id"));
                    perfil.put("name", rs.getString("name"));
                    perfil.put("cpf", rs.getString("cpf"));
                    perfil.put("age", rs.getInt("age"));
                    perfil.put("sexo", rs.getString("sexo"));
                    perfil.put("alergias", rs.getString("alergias"));
                    perfil.put("historico_vacinacao", rs.getString("historico_vacinacao"));
                    perfil.put("medicamentos_uso_continuo", rs.getString("medicamentos_uso_continuo"));
                    perfil.put("necessita_insulina", rs.getBoolean("necessita_insulina"));
                    perfil.put("dados_sensiveis", rs.getString("dados_sensiveis"));
                    prontuarioCompleto.put("perfil", perfil);
                }
            }

            // 2. Busca o histórico de atendimentos
            String sqlAtendimentos = "SELECT * FROM atendimentos WHERE paciente_id = ? ORDER BY data_atendimento DESC";
            List<Map<String, Object>> atendimentos = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sqlAtendimentos)) {
                stmt.setInt(1, pacienteId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Map<String, Object> atendimento = new HashMap<>();
                    atendimento.put("data", rs.getTimestamp("data_atendimento").toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    atendimento.put("profissional", rs.getString("nome_profissional"));
                    atendimento.put("motivo", rs.getString("motivo_consulta"));
                    atendimento.put("descricao", rs.getString("descricao"));
                    // Adicione outros campos do atendimento se necessário
                    atendimentos.add(atendimento);
                }
            }
            prontuarioCompleto.put("atendimentos", atendimentos);
            
            resp.getWriter().write(gson.toJson(prontuarioCompleto));

        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"Erro no servidor.\"}");
            e.printStackTrace();
        }
    }
}