package org.example.banktransfer.controller;

import org.example.banktransfer.dto.*;
import org.example.banktransfer.services.TransferenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/transferencias")
public class TransferenciaController {
    private final TransferenciaService transferenciaService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransferenciaResponse transferir(@Valid @RequestBody TransferenciaRequest req) {
        return transferenciaService.transferir(req);
    }

    @GetMapping
    public List<TransferenciaResponse> listar() {
        return transferenciaService.listar();
    }
}