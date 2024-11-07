package com.example.poc.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.poc.model.Transacao;
import com.example.poc.repository.AuditoriaFraudeRepository;
import com.example.poc.repository.TransacaoRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuditoriaFraudeConsumerService {

    private TransacaoRepository transacaoRepository;
    private AuditoriaFraudeRepository auditoriaFraudeRepository;
    private final Map<String, Integer> saqueContador = new HashMap<>();
    private final Map<String, LocalDateTime> saqueTempo = new HashMap<>();

    
    public AuditoriaFraudeConsumerService(TransacaoRepository transacaoRepository, AuditoriaFraudeRepository auditoriaFraudeRepository) {
        this.transacaoRepository = transacaoRepository;
        this.auditoriaFraudeRepository = auditoriaFraudeRepository;
    }

    @KafkaListener(topics = "mentoria_poc", groupId = "transacao-group")
    public void consumirMensagem(Transacao transacao) {

        System.out.println("Transação consumida: " + transacao);

    }

    public String detectarFraude(Transacao transacao) {
        String numeroConta = transacao.getNumeroConta();
        double valor = transacao.getValor();

        if ("transferencia".equalsIgnoreCase(transacao.getTipoTransacao()) && valor > 50000) {
            auditoriaFraudeRepository.registrarEvento(transacao, transacaoRepository.obterConta(numeroConta), "Transferência acima do limite permitido.");
            throw new IllegalArgumentException("Transferência acima do limite permitido.");
        }

        if ("saque".equalsIgnoreCase(transacao.getTipoTransacao())) {
            LocalDateTime agora = LocalDateTime.now();
            saqueContador.putIfAbsent(numeroConta, 0);
            saqueTempo.putIfAbsent(numeroConta, agora);

            System.out.println(saqueContador + " Primeiro Contador");
            if (saqueTempo.get(numeroConta).plusMinutes(10).isAfter(agora)) {
                saqueContador.put(numeroConta, saqueContador.get(numeroConta) + 1);
                System.out.println(saqueContador + "Contador");
            } else {
                saqueContador.put(numeroConta, 1);
                saqueTempo.put(numeroConta, agora);
            }

            if (saqueContador.get(numeroConta) > 3) {
                auditoriaFraudeRepository.registrarEvento(transacao, transacaoRepository.obterConta(numeroConta), "Múltiplos saques em um curto período de tempo.");
                throw new IllegalArgumentException("Múltiplos saques em um curto período de tempo.");
            }
        }
        
        String status = transacaoRepository.obterStatusConta(numeroConta);
        if ("inativo".equalsIgnoreCase(status) || "bloqueado".equalsIgnoreCase(status)) {
            auditoriaFraudeRepository.registrarEvento(transacao, transacaoRepository.obterConta(numeroConta),"Conta inativa ou bloqueada.");
        }

        return null;
    }

}
