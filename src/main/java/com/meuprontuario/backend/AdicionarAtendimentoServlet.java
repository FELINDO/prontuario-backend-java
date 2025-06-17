package com.meuprontuario.backend;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet("/api/adicionarAtendimento")
public class AdicionarAtendimentoServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        Map<String, Object> data = gson.fromJson(req.getReader(), Map.class);

        String sql = "INSERT INTO atendimentos (paciente_id, profissional_id, nome_profissional, data_atendimento, motivo_consulta, descricao, procedimentos_realizados, solicitacoes_exames, resultados_exames, risco) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Convertendo IDs de Double (padrão do GSON para números) para Integer
            int pacienteId = ((Double) data.get("pacienteId")).intValue();
            int profissionalId = ((Double) data.get("profissionalId")).intValue();

            stmt.setInt(1, pacienteId);
            stmt.setInt(2, profissionalId);
            stmt.setString(3, (String) data.get("nomeProfissional"));
            stmt.setString(4, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.setString(5, (String) data.get("motivoConsulta"));
            stmt.setString(6, (String) data.get("descricaoGeral")); // Supondo um campo de descrição geral
            stmt.setString(7, (String) data.get("procedimentosRealizados"));
            stmt.setString(8, (String) data.get("solicitacoesExames"));
            stmt.setString(9, (String) data.get("resultadosExames"));
            stmt.setString(10, (String) data.get("risco"));

            stmt.executeUpdate();
            resp.getWriter().write(gson.toJson(Map.of("success", true, "message", "Atendimento registrado com sucesso!")));
        } catch (Exception e) {
            e.printStackTrace(); // Ajuda a depurar no console do Tomcat
            resp.setStatus(500);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Erro no servidor: " + e.getMessage())));
        }
    }
}