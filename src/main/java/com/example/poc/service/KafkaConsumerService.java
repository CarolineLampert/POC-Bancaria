// package com.example.poc.service;

// import org.springframework.stereotype.Service;

// import com.example.poc.model.Transacao;

// @Service
// public class KafkaConsumerService {

//     private final AuditoriaFraudeConsumerService auditoriaFraudeConsumerService;

//     public KafkaConsumerService(AuditoriaFraudeConsumerService auditoriaFraudeConsumerService) {
//         this.auditoriaFraudeConsumerService = auditoriaFraudeConsumerService;
//     }

//     public void listen(Transacao event) throws Exception {
//         System.out.println("Received message: " + event.toString());
//         auditoriaFraudeConsumerService.consumirMensagem(event); // Chama o método de auditoria
//     }
// }
