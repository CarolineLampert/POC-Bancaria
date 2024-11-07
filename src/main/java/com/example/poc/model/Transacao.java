package com.example.poc.model;

import lombok.Data;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Data
public class Transacao {
    
    private int idTransacao;
    private String numeroConta;
    private String numeroContaDestino;
    private String tipoTransacao;
    private Float valor;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date dataHora;
    private String descricao;
    private int idCliente;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransacaoRequest{
        private String numeroConta;
        private String numeroContaDestino;
        private Float valor;
        private String descricao;
        private Transacao transacao;
        private String tipoTransacao;
        private int idCliente;

        public String getNumeroConta() {
            return numeroConta;
        }
   
        public void setNumeroConta(String numeroConta) {
            this.numeroConta = numeroConta;
        }
   
        public String getNumeroContaDestino() {
            return numeroContaDestino;
        }
   
        public void setNumeroContaDestino(String numeroContaDestino) {
            this.numeroContaDestino = numeroContaDestino;
        }
   
        public Float getValor() {
            return valor;
        }
   
        public void setValor(Float valor) {
            this.valor = valor;
        }
   
        public String getDescricao() {
            return descricao;
        }
   
        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public Transacao getTransacao() {
            return transacao;
        }

        public void setIdCliente(int idCliente){
            this.idCliente = idCliente;
        }

        public int getIdCliente(){
            return idCliente;
        }
        
        public String getTipoTransacao() {
            return tipoTransacao;
        }
    
        public void setTipoTransacao(String tipoTransacao) {
            this.tipoTransacao = tipoTransacao;
        }
    }

    public Transacao(int idTransacao, String numeroConta, String numeroContaDestino, String tipoTransacao, Float valor, Date dataHora, String descricao, int idCliente) {
        this.idTransacao = idTransacao;
        this.numeroConta = numeroConta;
        this.numeroContaDestino = numeroContaDestino;
        this.tipoTransacao = tipoTransacao;
        this.valor = valor;
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.idCliente = idCliente;
    }

    public Transacao(){

    }
    
}
