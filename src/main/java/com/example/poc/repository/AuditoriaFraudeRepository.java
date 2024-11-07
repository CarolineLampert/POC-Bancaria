package com.example.poc.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.poc.model.Transacao;
import com.example.poc.model.Conta;

import jakarta.transaction.Transactional;

@Repository
public class AuditoriaFraudeRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuditoriaFraudeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void registrarEvento(Transacao transacao, Conta conta, String mensagemFraude) {
        System.out.println("Registrando evento de fraude: " + mensagemFraude);
        String sql = "INSERT INTO auditoria_fraude (numeroConta, tipoTransacao, statusConta, valor, descricaoFraude, dataHora) VALUES (?, ?, ?, ?, ?, NOW())";
        jdbcTemplate.update(sql,  transacao.getNumeroConta(), transacao.getTipoTransacao(), conta.getStatus(), transacao.getValor(), mensagemFraude);
    }
}
