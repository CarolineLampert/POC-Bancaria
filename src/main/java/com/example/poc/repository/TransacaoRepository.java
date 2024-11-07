package com.example.poc.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Date;


import com.example.poc.model.Conta;
import com.example.poc.model.Transacao;

import jakarta.transaction.Transactional;
import lombok.NonNull;

@Configuration
@Repository
public class TransacaoRepository {

    private JdbcTemplate jdbcTemplate;

    public TransacaoRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void deposito(Transacao transacao, Conta conta){

        verificaConta(conta.getNumeroConta());
        obterStatusConta(conta.getNumeroConta());

        String sqlTransacao = "Insert into transacao (numeroConta, TipoTransacao, Valor, Descricao) values ( ?, ?, ?, ?)";
        jdbcTemplate.update(sqlTransacao, transacao.getNumeroConta(), transacao.getTipoTransacao() , transacao.getValor(), transacao.getDescricao());

        String sqlVerificaValor = "SELECT Valor FROM transacao WHERE numeroConta = ? ORDER BY idTransacao DESC LIMIT 1";
        @SuppressWarnings("deprecation")
        Float valorTransacao = jdbcTemplate.queryForObject(sqlVerificaValor, new Object[]{transacao.getNumeroConta()}, Float.class);

        if (valorTransacao != null) {
            String sqlConta = "UPDATE conta SET Saldo = Saldo + ? WHERE NumeroConta = ?";
            jdbcTemplate.update(sqlConta, valorTransacao, conta.getNumeroConta());
        }
    }

        @Transactional
    public void saque(Transacao transacao, Conta conta) {

        verificaConta(conta.getNumeroConta());
        obterStatusConta(conta.getNumeroConta());
        
        String sqlTransacao = "INSERT INTO transacao (numeroConta, TipoTransacao, Valor) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlTransacao, transacao.getNumeroConta(), transacao.getTipoTransacao(), transacao.getValor());

        String sqlVerificaValor = "SELECT Valor FROM transacao WHERE numeroConta = ? ORDER BY idTransacao DESC LIMIT 1";
        @SuppressWarnings("deprecation")
        Float valorTransacao = jdbcTemplate.queryForObject(sqlVerificaValor, new Object[]{transacao.getNumeroConta()}, Float.class);
        
        System.out.println("O valor da transacao é " + valorTransacao);


        String sqlSaldo = "Select saldo from conta where numeroConta = ?";
        @SuppressWarnings("deprecation")
        Float saldo = jdbcTemplate.queryForObject(sqlSaldo, new Object[]{conta.getNumeroConta()}, Float.class);

        if (valorTransacao == null || saldo == null || saldo < 0 || saldo < valorTransacao){
            throw new IllegalArgumentException("Você não tem saldo o suficiente para realizar o saque");
        }else {
            String sqlConta = "UPDATE conta SET Saldo = Saldo - ? WHERE numeroConta = ?";
            jdbcTemplate.update(sqlConta, valorTransacao, conta.getNumeroConta());
        }

        
    }

    @Transactional
    public void transferencia(Transacao transacao) {
        if (transacao.getNumeroConta().equals(transacao.getNumeroContaDestino())) {
            throw new IllegalArgumentException("As contas de origem e destino devem ser diferentes.");
        }
    
        verificaConta(transacao.getNumeroConta());
        verificaConta(transacao.getNumeroContaDestino());
    
        String sqlSaldoOrigem = "SELECT Saldo FROM conta WHERE NumeroConta = ?";
        Float saldoOrigem = jdbcTemplate.queryForObject(sqlSaldoOrigem, Float.class, transacao.getNumeroConta());
        if (saldoOrigem == null || saldoOrigem < transacao.getValor()) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar a transferência.");
        }
    
        // Debitar valor da conta de origem
        String sqlAtualizaOrigem = "UPDATE conta SET Saldo = Saldo - ? WHERE NumeroConta = ?";
        jdbcTemplate.update(sqlAtualizaOrigem, transacao.getValor(), transacao.getNumeroConta());
    
        // Creditar valor na conta de destino
        String sqlAtualizaDestino = "UPDATE conta SET Saldo = Saldo + ? WHERE NumeroConta = ?";
        jdbcTemplate.update(sqlAtualizaDestino, transacao.getValor(), transacao.getNumeroContaDestino());

        String sqlTransacao = "INSERT INTO transacao (numeroConta, numeroContaDestino, TipoTransacao, Valor, Descricao) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlTransacao, transacao.getNumeroConta(), transacao.getNumeroContaDestino(), transacao.getTipoTransacao(), transacao.getValor(), transacao.getDescricao());
        jdbcTemplate.update(sqlTransacao, transacao.getNumeroContaDestino(), null, "Transferencia Recebida", transacao.getValor(), transacao.getDescricao());

    }

    @SuppressWarnings("deprecation")
    public Conta obterConta(String numeroConta) {
        String sql = "SELECT * FROM conta WHERE NumeroConta = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{numeroConta}, new RowMapper<Conta>() {
            @Override
            public Conta mapRow(@SuppressWarnings("null") ResultSet rs, int rowNum) throws SQLException {
                Conta conta = new Conta();
                conta.setNumeroConta(rs.getString("NumeroConta"));
                conta.setSaldo(rs.getFloat("Saldo"));
                conta.setStatus(rs.getString("Status"));
                // Adicione outros campos conforme necessário
                return conta;
            }
        });
    }

    @SuppressWarnings("deprecation")
    public String obterStatusConta(String numeroConta) {
        String sql = "SELECT Status FROM conta WHERE NumeroConta = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{numeroConta}, String.class);
    }
    
    public boolean verificaConta(String numeroConta) {
        String sqlVerificaConta = "SELECT COUNT(*) FROM conta WHERE NumeroConta = ?";
        @SuppressWarnings("deprecation")
        Integer count = jdbcTemplate.queryForObject(sqlVerificaConta, new Object[]{numeroConta}, Integer.class);
        return count != null && count > 0;
    }

    //Extrato Bancário
    @SuppressWarnings("deprecation")
    public List<Transacao> extratoBancario(String numeroConta, Date dataInicio, Date dataFim){
        String sql = "SELECT c.NomeCompleto, DATE(t.DataHora) AS DataSimples, t.TipoTransacao, t.Valor, t.Descricao FROM Transacao t JOIN Conta co ON t.NumeroConta = co.NumeroConta\n" + //
                        "JOIN Cliente c ON co.idCliente = c.idCliente WHERE (co.NumeroConta = ?) AND (t.DataHora BETWEEN ? AND ?) ORDER BY t.DataHora DESC";
        try {
            return jdbcTemplate.query(sql, new Object[]{numeroConta, dataInicio, dataFim}, new TransacaoRowMapper());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao obter o extrato bancário: " + e.getMessage());
        }
    }

    public class TransacaoRowMapper implements RowMapper<Transacao> {
        @SuppressWarnings("null")
        @Override
        public Transacao mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
            Transacao transacao = new Transacao();
            transacao.setNumeroConta(rs.getString("numeroConta"));
            transacao.setTipoTransacao(rs.getString("TipoTransacao"));
            transacao.setValor(rs.getFloat("Valor"));
            transacao.setDescricao(rs.getString("Descricao"));
            return transacao;
        }
    }
}
