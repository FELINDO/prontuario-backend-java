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

@WebServlet("/api/solicitarConsulta")
public class SolicitarConsultaServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        Gson gson = new Gson();
        Map<String, Object> data = gson.fromJson(req.getReader(), Map.class);
        
        String sql = "INSERT INTO solicitacoes_consulta (paciente_id, data_solicitacao, motivo_paciente, status) VALUES (?, ?, ?, 'Pendente')";

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            int pacienteId = ((Double) data.get("pacienteId")).intValue();
            stmt.setInt(1, pacienteId);
            stmt.setString(2, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.setString(3, (String) data.get("motivo"));
            
            stmt.executeUpdate();
            resp.getWriter().write(gson.toJson(Map.of("success", true, "message", "Sua solicitação foi enviada com sucesso!")));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Erro ao enviar solicitação.")));
        }
    }
}