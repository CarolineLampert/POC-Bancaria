package com.example.poc.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.example.poc.model.Cliente;
import com.example.poc.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository){
        this.clienteRepository = clienteRepository;
    }
    
    public int salvar(Cliente cliente){
        return this.clienteRepository.salvar(cliente);
    }

    public int atualizar(Cliente cliente, int id){
        return this.clienteRepository.atualizar(cliente, id);
    }

    public List<Cliente> buscarPorNomeOuCpf(String nome, String cpf){
        return this.clienteRepository.buscarPorNomeOuCpf(nome, cpf);
    }

    public List<Cliente> buscar(){
        return this.clienteRepository.buscar();
    }

    public void excluirCliente(int id) {
        if (clienteRepository.temContasAtreladas(id)) {
            throw new IllegalArgumentException("Não é possível excluir o cliente, pois ele possui contas atreladas.");
        }
        clienteRepository.excluirCliente(id);
    }
    
}
