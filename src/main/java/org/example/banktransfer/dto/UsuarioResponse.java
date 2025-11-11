package org.example.banktransfer.dto;

import org.example.banktransfer.enums.TipoUsuario;
import java.math.BigDecimal;


public record UsuarioResponse(
        Long id,
        String nomeCompleto,
        String cpfOuCnpj,
        String email,
        TipoUsuario tipoUsuario,
        BigDecimal saldo
) {}