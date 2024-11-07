package com.example.poc.model;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Cliente {

    private int id;
    private String nomeCompleto, cpf, email, telefone;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date dataNasc;

    
    public Cliente (int id, String nomeCompleto, String cpf, String email, Date dataNasc, String telefone){
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.cpf =  cpf;
        this.email = email;
        this.dataNasc = dataNasc;
        this.telefone = telefone;
    }


    public Cliente() {

    }

}
