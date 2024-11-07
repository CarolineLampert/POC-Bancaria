package com.example.poc.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class Conta {

    private int id;
    private Integer idCliente;
    private String numeroConta;
    private String tipoConta;
    private Float saldo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date dataEncerramento;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date dataAbertura;
    private String status;
    private String nomeCliente;

    public Conta (int id, Integer idCliente, String numeroConta, String tipoConta, Float saldo, Date dataEncerramento, Date dataAbertura, String status, String nomeCliente){
        this.id = id;
        this.idCliente = idCliente;
        this.numeroConta = numeroConta;
        this.tipoConta = tipoConta;
        this.saldo = saldo;
        this.dataAbertura = dataAbertura;
        this.dataEncerramento = dataEncerramento;
        this.status = status;
        this.nomeCliente = nomeCliente;
    }

    public Conta(){
        
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
