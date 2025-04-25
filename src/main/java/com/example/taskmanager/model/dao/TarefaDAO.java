package com.example.taskmanager.model.dao;

import com.example.taskmanager.model.Tarefa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class  TarefaDAO {

    public void salvar(Tarefa tarefa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();

            String sql = "INSERT INTO tarefas (titulo, descricao, data_entrega, status) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());

            if (tarefa.getDataEntrega() != null) {
                stmt.setDate(3, Date.valueOf(tarefa.getDataEntrega()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }

            stmt.setString(4, tarefa.getStatus());

            stmt.executeUpdate();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    public List<Tarefa> listarTodos() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Tarefa> tarefas = new ArrayList<>();

        try {
            conn = ConnectionFactory.getConnection();

            String sql = "SELECT * FROM tarefas ORDER BY id";
            stmt = conn.prepareStatement(sql);

            rs = stmt.executeQuery();

            while (rs.next()) {
                Tarefa tarefa = new Tarefa();

                tarefa.setId(rs.getInt("id"));
                tarefa.setTitulo(rs.getString("titulo"));
                tarefa.setDescricao(rs.getString("descricao"));

                Date dataEntrega = rs.getDate("data_entrega");
                if (dataEntrega != null) {
                    tarefa.setDataEntrega(dataEntrega.toLocalDate());
                }

                tarefa.setStatus(rs.getString("status"));

                tarefas.add(tarefa);
            }
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }

        return tarefas;
    }

    public void atualizar(Tarefa tarefa) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();

            String sql = "UPDATE tarefas SET titulo = ?, descricao = ?, data_entrega = ?, status = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());

            if (tarefa.getDataEntrega() != null) {
                stmt.setDate(3, Date.valueOf(tarefa.getDataEntrega()));
            } else {
                stmt.setNull(3, java.sql.Types.DATE);
            }

            stmt.setString(4, tarefa.getStatus());
            stmt.setInt(5, tarefa.getId());

            stmt.executeUpdate();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    public void excluir(Integer id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ConnectionFactory.getConnection();

            String sql = "DELETE FROM tarefas WHERE id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, id);

            stmt.executeUpdate();
        } finally {
            ConnectionFactory.closeConnection(conn, stmt);
        }
    }

    public Tarefa buscarPorId(Integer id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        Tarefa tarefa = null;

        try {
            conn = ConnectionFactory.getConnection();

            String sql = "SELECT * FROM tarefas WHERE id = ?";
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, id);

            rs = stmt.executeQuery();

            if (rs.next()) {
                tarefa = new Tarefa();

                tarefa.setId(rs.getInt("id"));
                tarefa.setTitulo(rs.getString("titulo"));
                tarefa.setDescricao(rs.getString("descricao"));

                Date dataEntrega = rs.getDate("data_entrega");
                if (dataEntrega != null) {
                    tarefa.setDataEntrega(dataEntrega.toLocalDate());
                }

                tarefa.setStatus(rs.getString("status"));
            }
        } finally {
            ConnectionFactory.closeConnection(conn, stmt, rs);
        }

        return tarefa;
    }
}