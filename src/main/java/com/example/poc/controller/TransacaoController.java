package com.example.poc.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.poc.model.Conta;
import com.example.poc.model.Transacao;
import com.example.poc.model.Transacao.TransacaoRequest;
import com.example.poc.service.AuditoriaFraudeConsumerService;
import com.example.poc.service.TransacaoService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;
    private final AuditoriaFraudeConsumerService auditoriaFraudeConsumerService;

    
    public TransacaoController(TransacaoService transacaoService, AuditoriaFraudeConsumerService auditoriaFraudeConsumerService){
        this.transacaoService = transacaoService;
        this.auditoriaFraudeConsumerService = auditoriaFraudeConsumerService;
    }

    @PostMapping("/deposito")
    public ResponseEntity<String> realizarDeposito(@RequestBody TransacaoRequest request, Conta conta) {
        try {
            conta.setNumeroConta(request.getNumeroConta());

            transacaoService.deposito(request.getTransacao(), conta);
            return ResponseEntity.ok("Depósito realizado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Registra a pilha de chamadas
            return ResponseEntity.status(500).body("Erro ao realizar o depósito: " + e.getMessage());
        }
    }
    
    @PostMapping("/saque")
    public ResponseEntity<String> realizarSaque(@RequestBody TransacaoRequest request, Conta conta) {
        try {
        Transacao transacao = request.getTransacao();
        transacao.setNumeroConta(request.getNumeroConta());
        transacao.setTipoTransacao("saque");
        
        String mensagemFraude = auditoriaFraudeConsumerService.detectarFraude(transacao);
        if (mensagemFraude != null) {
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagemFraude);
        }
        
        conta.setNumeroConta(request.getNumeroConta());
        transacaoService.saque(transacao, conta);

        return ResponseEntity.ok("Saque realizado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Registra a pilha de chamadas
            return ResponseEntity.status(500).body("Erro ao realizar o saque: " + e.getMessage());
        }
    }

    //transferencia
    @PostMapping("/transferencia")
    public ResponseEntity<String> realizarTransferencia(@RequestBody TransacaoRequest request) {
        try {
            Transacao transacao = new Transacao();
            transacao.setNumeroConta(request.getNumeroConta());
            transacao.setNumeroContaDestino(request.getNumeroContaDestino());
            transacao.setTipoTransacao("Transferencia");
            transacao.setValor(request.getValor());
            transacao.setDescricao(request.getDescricao());

            String mensagemFraude = auditoriaFraudeConsumerService.detectarFraude(transacao);
            if (mensagemFraude != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagemFraude);
            }
    
            transacaoService.transferencia(transacao);
            return ResponseEntity.ok("Transferência realizada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Registra a pilha de chamadas
            return ResponseEntity.status(500).body("Erro ao realizar a transferência: " + e.getMessage());
        }
    }

}
