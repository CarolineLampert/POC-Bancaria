package com.example.poc.service;


import org.springframework.stereotype.Service;

import com.example.poc.model.Conta;
import com.example.poc.model.Transacao;
import com.example.poc.repository.TransacaoRepository;

@Service
public class TransacaoService {

    private TransacaoRepository transacaoRepository;
    private KafkaProducerService kafkaProducerService;


    public TransacaoService(TransacaoRepository transacaoRepository, KafkaProducerService kafkaProducerService){
        this.transacaoRepository = transacaoRepository;
        this.kafkaProducerService = kafkaProducerService;
    }
    
    public void deposito(Transacao transacao, Conta conta){
        validarDeposito(transacao, conta);
        transacaoRepository.deposito(transacao, conta);
        kafkaProducerService.produzirMensagem(transacao);
    }

    public void saque(Transacao transacao, Conta conta){
        validarSaque(transacao, conta);
        kafkaProducerService.produzirMensagem(transacao);
        transacaoRepository.saque(transacao, conta);
    }

    public void transferencia(Transacao transacao) {
        Conta contaOrigem = new Conta();
        contaOrigem.setNumeroConta(transacao.getNumeroConta());

        Conta contaDestino = new Conta();
        contaDestino.setNumeroConta(transacao.getNumeroContaDestino());
        validarTransferencia(transacao, contaOrigem);
        validarSaque(transacao, contaDestino);
        transacaoRepository.transferencia(transacao);
        kafkaProducerService.produzirMensagem(transacao);
    }

    
    public void validarDeposito(Transacao transacao, Conta conta) {
        transacaoRepository.verificaConta(conta.getNumeroConta());
        String status = transacaoRepository.obterStatusConta(conta.getNumeroConta());
                
        if ("inativo".equalsIgnoreCase(status)){
            throw new IllegalArgumentException("Transação não permitida: a conta está encerrada");
        }
        if (transacao.getValor() <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser maior que zero.");
        }
        if (transacao.getTipoTransacao() == null || transacao.getTipoTransacao().isEmpty()) {
            throw new IllegalArgumentException("Tipo de transação não pode ser vazio.");
        }
    }

    public void validarSaque(Transacao transacao, Conta conta) {
        transacaoRepository.verificaConta(conta.getNumeroConta());
        String status = transacaoRepository.obterStatusConta(conta.getNumeroConta());
        
        if ("inativo".equalsIgnoreCase(status)){
            throw new IllegalArgumentException("Transação não permitida: a conta está encerrada");
        }
        if (transacao.getValor() < 2) {
            throw new IllegalArgumentException("O valor do saque deve ser maior que dois.");
        }
        if (transacao.getTipoTransacao() == null || transacao.getTipoTransacao().isEmpty()) {
            throw new IllegalArgumentException("Tipo de transação não pode ser vazio.");
        }
    }

    public void validarTransferencia(Transacao transacao, Conta conta) {
        transacaoRepository.verificaConta(conta.getNumeroConta());
        String status = transacaoRepository.obterStatusConta(conta.getNumeroConta());
        
        if ("inativo".equalsIgnoreCase(status)){
            throw new IllegalArgumentException("Transação não permitida: a conta está encerrada");
        }
        
        if (transacao.getTipoTransacao() == null || transacao.getTipoTransacao().isEmpty()) {
            throw new IllegalArgumentException("Tipo de transação não pode ser vazio.");
        }
    }

}

