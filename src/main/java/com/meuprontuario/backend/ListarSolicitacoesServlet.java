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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/listarSolicitacoes")
public class ListarSolicitacoesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        // Este SQL junta as tabelas de solicitações e de usuários para pegar os dados do paciente
        String sql = "SELECT s.id as solicitacao_id, s.data_solicitacao, s.motivo_paciente, u.* " +
                     "FROM solicitacoes_consulta s " +
                     "JOIN usuarios u ON s.paciente_id = u.id " +
                     "WHERE s.status = 'Pendente' " +
                     "ORDER BY s.data_solicitacao ASC";
        
        List<Map<String, Object>> solicitacoes = new ArrayList<>();
        Gson gson = new Gson();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> solicitacao = new HashMap<>();
                solicitacao.put("solicitacaoId", rs.getInt("solicitacao_id"));
                solicitacao.put("dataSolicitacao", rs.getTimestamp("data_solicitacao"));
                solicitacao.put("motivoPaciente", rs.getString("motivo_paciente"));
                
                // Dados do paciente que fez a solicitação
                solicitacao.put("pacienteId", rs.getInt("id"));
                solicitacao.put("pacienteNome", rs.getString("name"));
                solicitacao.put("pacienteCpf", rs.getString("cpf"));
                solicitacao.put("pacienteIdade", rs.getInt("age"));
                
                solicitacoes.add(solicitacao);
            }
            
            resp.getWriter().write(gson.toJson(solicitacoes));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("error", "Erro ao buscar solicitações: " + e.getMessage())));
        }
    }
}