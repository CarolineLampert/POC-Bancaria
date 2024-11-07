package com.example.poc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.poc.model.Conta;
import com.example.poc.service.ContaService;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/contas")
public class ContaController {
    private ContaService contaService;

    public ContaController(ContaService contaService){
        this.contaService = contaService;
    }

    @PostMapping
    public ResponseEntity<String> cadastrarConta(@RequestBody Conta conta) {
    ResponseEntity<String> response = contaService.cadastrarConta(conta);
    return response;
}

    
    @GetMapping
    public List<Conta> consultarContas() {
        return this.contaService.consultarContas();
    }

    @GetMapping("/consulta")
    public List<Conta> opcoesDeConsultas(@RequestParam(required = false)Integer idCliente,@RequestParam(required = false) String numeroConta,
    @RequestParam(required = false) String tipoConta) {
        // try {
            return this.contaService.opcoesDeConsulta(idCliente, numeroConta, tipoConta);
        // } catch (Exception e) {
            // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao consultar opções: " + e.getMessage());
        // }
    }

    @PutMapping("/atualizar-saldo/{idConta}")
    public ResponseEntity<String> atualizarSaldo(@PathVariable Long idConta, @RequestBody Conta conta){
        try {
            contaService.alterarSaldo(idConta, conta);
            return ResponseEntity.ok("Saldo atualizado com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("encerrar-conta/{idConta}")
    public ResponseEntity<String> encerrarConta(@PathVariable Long idConta) {
        boolean encerramento = contaService.encerrarConta(idConta);
        if(encerramento){
            return ResponseEntity.ok("Conta encerrada com sucesso");
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não foi possível encerrar a conta.");
        }
    }

    @DeleteMapping("/excluir/{idConta}")
    public ResponseEntity<String> excluirConta(@PathVariable Long idConta){
        boolean excluida = contaService.excluirConta(idConta);
        if (excluida){
            return ResponseEntity.ok("Conta excluída com sucesso.");
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não foi possível excluir a conta. Verifique se o saldo é zero.");
        }
    }
}
