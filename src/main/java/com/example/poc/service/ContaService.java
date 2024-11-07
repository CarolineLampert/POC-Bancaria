package com.example.poc.service;

import com.example.poc.model.Conta;
import com.example.poc.repository.ContaRepository;
import com.example.poc.repository.TransacaoRepository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ContaService {

    private static Random random = new Random();

    @Autowired
    private final ContaRepository contaRepository;
    private TransacaoRepository transacaoRepository;

    public ContaService(ContaRepository contaRepository, TransacaoRepository transacaoRepository){
        this.contaRepository = contaRepository;
        this.transacaoRepository = transacaoRepository;
    }
    
    public ResponseEntity<String> cadastrarConta(Conta conta) {
        if (!contaRepository.clienteExiste(conta.getIdCliente())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não é possível criar a conta, pois o cliente não existe.");
        }

        conta.setNumeroConta(gerarNumeroConta());

        if (conta.getSaldo() < 0) {
            conta.setSaldo(0.00f);
        }

        try {
            contaRepository.cadastrarConta(conta);
            return ResponseEntity.ok("Conta cadastrada com sucesso.");
        } catch (DataAccessException e) {
            System.err.println("Erro ao cadastrar conta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar a conta.");
        }
    }


    public String gerarNumeroConta() {
        String numeroConta;
        do {
            int numero = 10000000 + random.nextInt(90000000);
            numeroConta = String.valueOf(numero);
        } while (contaRepository.numeroContaExiste(numeroConta)); 
        
        return numeroConta;
    }

    public List<Conta> consultarContas(){
        return this.contaRepository.consultarContas();
    }

    public List<Conta> opcoesDeConsulta(Integer idCliente, String numeroConta, String tipoConta){
        contaRepository.numeroContaExiste(numeroConta);
        try {
            return contaRepository.opcoesDeConsulta(idCliente, numeroConta, tipoConta);
        } catch (DataAccessException e) {
            throw new RuntimeException("Erro ao consultar opções de conta", e);
        }
    }
    
    public void alterarSaldo(Long idConta, Conta conta) {
        try {
            int rowsAffected = contaRepository.atualizarSaldo(idConta, conta);
            if (rowsAffected == 0) {
                throw new IllegalArgumentException("Conta não encontrada.");
            }
        } catch (DataAccessException e) {
            throw new RuntimeException("Erro ao atualizar saldo: " + e.getMessage(), e);
        }
    }
    
    public boolean encerrarConta(Long idConta){
        return this.contaRepository.encerrarConta(idConta);
    }

    public boolean excluirConta(Long idConta){
        String numeroConta = contaRepository.obterNumeroContaPorId(idConta);
        
        if (transacaoRepository.verificaConta(numeroConta)){
            throw new IllegalArgumentException("Não é possível deletar a conta, existem transações associadas.");
        }
        return this.contaRepository.excluirConta(idConta);
    }
}
