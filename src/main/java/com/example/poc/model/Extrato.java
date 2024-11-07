package com.example.poc.model;

import java.util.Date;
import java.util.List;

public class Extrato {
    private String nomeCompleto;
    private String numeroConta;
    private List<Transacao> transacoes;

    public Extrato(String nomeCompleto, String numeroConta, Date dataHora, List<Transacao> transacoes) {
        this.nomeCompleto = nomeCompleto;
        this.numeroConta = numeroConta;
        this.transacoes = transacoes;
    }

    public Extrato(){

    }
    
    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
    }
    
}
