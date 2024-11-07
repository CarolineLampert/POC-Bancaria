package com.example.poc.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.poc.model.Cliente;

import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;

@Configuration
@Repository
public class ClienteRepository {
    
    private final JdbcTemplate jdbcTemplate;

    public ClienteRepository (JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public int salvar(Cliente cliente){
        String sql = "insert into cliente (NomeCompleto, CPF, Email, DataNasc, telefone) values (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, cliente.getNomeCompleto(), cliente.getCpf(), cliente.getEmail(), cliente.getDataNasc(), cliente.getTelefone());
    }

    @SuppressWarnings("deprecation")
    public List<Cliente> buscarPorNomeOuCpf(String nome, String cpf) {
    String sql = "SELECT * FROM cliente WHERE NomeCompleto = ? OR CPF = ?";
    return jdbcTemplate.query(sql, new Object[]{nome, cpf}, new ClienteRowMapper());
    }

    public int atualizar(Cliente cliente, int id){
        String sql = "update cliente set NomeCompleto = ?, CPF = ?, Email = ?, DataNasc = ?, telefone = ? where idCliente = ?";
        return jdbcTemplate.update(sql, cliente.getNomeCompleto(), cliente.getCpf(), cliente.getEmail(), cliente.getDataNasc(), cliente.getTelefone(), id);
    }

    public List<Cliente> buscar(){
        String sql = "select * from cliente";
        return jdbcTemplate.query(sql, new ClienteRowMapper());
    }

    public boolean temContasAtreladas(int id) {
        String sql = "SELECT COUNT(*) FROM conta WHERE idCliente = ?";
        @SuppressWarnings("deprecation")
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class);
        return count != null && count > 0;
    }

    public int excluirCliente(int id){
        String sql = "delete from cliente where idCliente = ?";
        return jdbcTemplate.update(sql, id);
    }


    private static class ClienteRowMapper implements RowMapper<Cliente>{

        @SuppressWarnings("null")
        @Override
        @Nullable
        public Cliente mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException{
            Cliente cliente = new Cliente();
            cliente.setId(rs.getInt("idCliente"));
            cliente.setNomeCompleto(rs.getString("NomeCompleto"));
            cliente.setEmail(rs.getString("email"));
            cliente.setCpf(rs.getString("CPF"));
            cliente.setDataNasc(rs.getDate("DataNasc"));
            cliente.setTelefone(rs.getNString("Telefone"));

            return cliente;
        }
    }

}
