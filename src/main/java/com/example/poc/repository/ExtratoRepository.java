package com.example.poc.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import com.example.poc.model.Extrato;
import com.example.poc.model.Transacao;

@Configuration
@Repository
public class ExtratoRepository {

    private final JdbcTemplate jdbcTemplate;

    public ExtratoRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("deprecation")
    public List<Extrato> obterExtrato(String numeroConta, Date dataInicial, Date dataFinal, int idCliente) {
        String sqlExtrato = """
                            select t.numeroConta, c.NomeCompleto, c.idCliente
                            from  transacao t
                            Join conta cc on t.numeroConta = cc.numeroConta
                            join cliente c on cc.idCliente = c.idCliente
                            where t.numeroConta = ?
                        """;

            List<Extrato> extratos = jdbcTemplate.query(sqlExtrato, new Object[]{numeroConta}, new ExtratoRowMapper());

            if (!extratos.isEmpty()) {
                Extrato extrato = extratos.get(0); // Obter o primeiro (e único) extrato
                List<Transacao> transacoes = obterTransacoes(dataInicial, dataFinal, idCliente, numeroConta);
                extrato.setTransacoes(transacoes);
                return List.of(extrato); // Retornar a lista com um único extrato
            }
        
            return new ArrayList<>();
        }

        @SuppressWarnings("deprecation")
        public List<Transacao> obterTransacoes(Date dataInicial, Date dataFinal, int idCliente, String numeroConta){

        String sqlTransacoes = """
                        select t.numeroConta, c.NomeCompleto, t.TipoTransacao, t.DataHora, t.Valor, t.numeroContaDestino, t.idTransacao, t.Descricao, cc.idCliente 
                        from cliente c, transacao t, conta cc
                        where t.numeroConta = cc.numeroConta AND t.numeroConta = ?
                        AND DATE(t.DataHora) BETWEEN ? AND ? AND cc.idCliente = ? AND c.idCliente = cc.idCliente AND t.Valor
                        order by t.DataHora desc
            """;
            return jdbcTemplate.query(sqlTransacoes, new Object[]{numeroConta, dataInicial, dataFinal, idCliente}, new TransacaoRowMapper());
        }

        

    public class ExtratoRowMapper implements RowMapper<Extrato> {
        @SuppressWarnings("null")
        @Override
        public Extrato mapRow(ResultSet rs, int rowNum) throws SQLException {
            Extrato extrato = new Extrato();

            extrato.setNomeCompleto(rs.getString("NomeCompleto"));
            extrato.setNumeroConta(rs.getString("NumeroConta"));
            return extrato;
        }
    }

    public class TransacaoRowMapper implements RowMapper<Transacao> {
        @SuppressWarnings("null")
        @Override
        public Transacao mapRow(ResultSet rs, int rowNum) throws SQLException {

            Transacao transacao = new Transacao();
            
            transacao.setIdCliente(rs.getInt("idCliente"));
            transacao.setIdTransacao(rs.getInt("idTransacao"));
            transacao.setNumeroConta(rs.getString("numeroConta"));
            transacao.setNumeroContaDestino(rs.getString("numeroContaDestino"));
            transacao.setTipoTransacao(rs.getString("TipoTransacao"));
            transacao.setDataHora(rs.getTimestamp("DataHora"));
            transacao.setValor(rs.getFloat("Valor"));
            transacao.setDescricao(rs.getString("Descricao"));
            return transacao;
        }
    }
}
