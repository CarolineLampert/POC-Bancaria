package com.example.poc.service;

import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

import com.example.poc.model.Extrato;
import com.example.poc.model.Transacao;
import com.example.poc.repository.ExtratoRepository;

@Service
public class ExtratoService {

    private final ExtratoRepository extratoRepository;

    public ExtratoService (ExtratoRepository extratoRepository){
        this.extratoRepository = extratoRepository;
    }

    public List<Extrato> obterExtrato(Date dataInicial, Date dataFinal, int idCliente, String numeroConta) {
        return extratoRepository.obterExtrato( numeroConta, dataInicial, dataFinal, idCliente);
    }

    public List<Transacao> listarTransacoes(Date dataInicial, Date dataFinal, int idCliente, String numeroConta){
        return extratoRepository.obterTransacoes(dataInicial, dataFinal, idCliente, numeroConta);
    }
}
