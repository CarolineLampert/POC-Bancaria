package com.example.poc.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.poc.model.Cliente;
import com.example.poc.service.ClienteService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/cliente")
public class ClienteController {
    
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService){
        this.clienteService = clienteService;
    }
    
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @PostMapping
    public Cliente criarCliente(@RequestBody Cliente cliente){
        this.clienteService.salvar(cliente);
        return cliente;
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable int id, @RequestBody Cliente cliente) {
    int resultado = this.clienteService.atualizar(cliente, id);
    
        if (resultado > 0) {
            return ResponseEntity.ok("Cliente atualizado com sucesso!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado.");
        }
    }

    
    @GetMapping("/buscar")
    public List<Cliente> buscarPorOuNomeCpf(@RequestParam(required = false)String nome,@RequestParam(required = false) String cpf) {
        return this.clienteService.buscarPorNomeOuCpf(nome, cpf);
    }

    @GetMapping
    public List<Cliente> buscar() {
        return this.clienteService.buscar();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluirCliente(@PathVariable int id) {
        try {
            clienteService.excluirCliente(id);
            return ResponseEntity.ok("Cliente excluído com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    

}
