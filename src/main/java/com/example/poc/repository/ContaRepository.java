package com.example.poc.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.example.poc.model.Conta;

import io.micrometer.common.lang.NonNull;
import io.micrometer.common.lang.Nullable;


@Configuration
@Repository
public class ContaRepository{
    
    private final JdbcTemplate jdbcTemplate;

    public ContaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
        
    public void cadastrarConta(Conta conta) {
        String sql = "INSERT INTO conta (idCliente, TipoConta, Saldo, NumeroConta) VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, conta.getIdCliente(), conta.getTipoConta(), conta.getSaldo(), conta.getNumeroConta());
        } catch (DataAccessException e) {
            System.err.println("Erro ao cadastrar conta: " + e.getMessage());
            throw e;
        }
    }
    
    public boolean numeroContaExiste(String numeroConta) {
        String sql = "SELECT COUNT(*) FROM conta WHERE NumeroConta = ?";
        @SuppressWarnings("deprecation")
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{numeroConta}, Integer.class);
        return count != null && count > 0;
    }

    @SuppressWarnings("deprecation")
    public boolean clienteExiste(int idCliente) {
        String sql = "SELECT COUNT(*) FROM cliente WHERE idCliente = ?";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, new Object[]{idCliente}, Integer.class);
            return count != null && count > 0;
        } catch (DataAccessException e) {
            System.err.println("Erro ao verificar se o cliente existe: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public List<Conta> opcoesDeConsulta(Integer idCliente, String numeroConta, String tipoConta) {
        String sql = "SELECT co.*, c.NomeCompleto FROM conta co " + 
                    "JOIN cliente c ON c.idCliente = co.idCliente " + 
                    "WHERE co.idCliente = ? OR co.NumeroConta = ? OR co.TipoConta = ?";
        return jdbcTemplate.query(sql, new Object[]{idCliente, numeroConta, tipoConta}, new ContaRowMapper());
    }
    
    public List<Conta> consultarContas() {
        String sql = "SELECT conta.*, cliente.NomeCompleto\n" + //
                        "FROM conta\n" + //
                        "JOIN cliente ON conta.idCliente = cliente.idCliente;";
        return jdbcTemplate.query(sql, new ContaRowMapper());
    }

    public int atualizarSaldo(Long id, Conta conta) {
        String sql = "UPDATE conta SET Saldo = ? WHERE idConta = ?";
        return jdbcTemplate.update(sql, conta.getSaldo(), id);
    }

    public boolean encerrarConta(Long idConta) {
        String sql = "UPDATE conta SET status = 'inativo', dataEncerramento = NOW() WHERE idConta = ?";
        int rowsAffected = jdbcTemplate.update(sql, idConta);
        return rowsAffected > 0;
    }

    public boolean excluirConta(Long idConta) {
        String sqlSaldo = "SELECT Saldo FROM conta WHERE idConta = ?";
        @SuppressWarnings("deprecation")
        Float saldo = jdbcTemplate.queryForObject(sqlSaldo, new Object[]{idConta}, Float.class);

        if (saldo != null && saldo == 0) {
            String sqlExcluir = "DELETE FROM conta WHERE idConta = ?";
            jdbcTemplate.update(sqlExcluir, idConta);
            return true;
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    public String obterNumeroContaPorId(Long idConta) {
        String sql = "SELECT NumeroConta FROM conta WHERE idConta = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{idConta}, String.class);
    }
    

    private static class ContaRowMapper implements RowMapper<Conta> {
        @SuppressWarnings("null")
        @Override
        @Nullable
        public Conta mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            Conta conta = new Conta();
            conta.setId(rs.getObject("idConta") != null ? rs.getInt("idConta") : null);
            conta.setIdCliente(rs.getInt("idCliente"));
            conta.setNumeroConta(rs.getString("NumeroConta"));
            conta.setTipoConta(rs.getString("TipoConta"));
            conta.setSaldo(rs.getFloat("Saldo"));
            conta.setStatus(rs.getString("Status"));
            conta.setDataAbertura(rs.getDate("dataAbertura"));
            conta.setDataEncerramento(rs.getDate("dataEncerramento"));
            conta.setNomeCliente(rs.getString("NomeCompleto"));
            return conta;
        }
    }
}
