package com.example.poc.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.poc.model.Transacao;



@Service
public class KafkaProducerService {
    
    private final KafkaTemplate<String, Transacao> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Transacao> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produzirMensagem(Transacao transacao) {
        this.kafkaTemplate.send("mentoria_poc", transacao);
    }
}
