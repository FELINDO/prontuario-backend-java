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

@WebServlet("/api/buscarPacientes")
public class BuscarPacientesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String termoBusca = req.getParameter("termo");
        if (termoBusca == null || termoBusca.trim().isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"Termo de busca é obrigatório.\"}");
            return;
        }

        String sql = "SELECT id, name, cpf FROM usuarios WHERE userType = 'Paciente' AND (name LIKE ? OR cpf LIKE ?)";
        List<Map<String, Object>> pacientes = new ArrayList<>();
        Gson gson = new Gson();

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            String parametroLike = "%" + termoBusca + "%";
            stmt.setString(1, parametroLike);
            stmt.setString(2, parametroLike);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> paciente = new HashMap<>();
                paciente.put("id", rs.getInt("id"));
                paciente.put("name", rs.getString("name"));
                paciente.put("cpf", rs.getString("cpf"));
                pacientes.add(paciente);
            }
            resp.getWriter().write(gson.toJson(pacientes));
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"Erro no servidor.\"}");
            e.printStackTrace();
        }
    }
}