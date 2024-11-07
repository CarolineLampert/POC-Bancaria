package com.example.poc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.List;

import com.example.poc.model.Extrato;
import com.example.poc.service.ExtratoService;

@RestController
@RequestMapping("/extrato")
public class ExtratoController {

    @Autowired
    private ExtratoService extratoService;

    @GetMapping("/{numeroConta}")
    public List<Extrato> obterExtrato(@PathVariable String numeroConta,@RequestParam("dataInicio") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataInicial, 
                                @RequestParam("dataFim") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataFinal, @RequestParam int idCliente) {
                                    
        return extratoService.obterExtrato(dataInicial, dataFinal, idCliente, numeroConta);
    }

    //@GetMapping("/{numeroConta}")
    // public List<Transacao> listarTransacoes(@PathVariable String numeroConta, @RequestParam("dataInicio") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataInicio,    
    //                                         @RequestParam("dataFim") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataFim, @RequestParam int idCliente)   {
    //     return extratoService.listarTransacoes(dataInicio, dataFim, idCliente, numeroConta);
    // }
    
}
