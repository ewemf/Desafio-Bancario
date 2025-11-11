package org.example.banktransfer.dto;

import org.example.banktransfer.enums.TipoUsuario;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;


public record UsuarioRequest(
        String nomeCompleto,
        String cpfOuCnpj,
        @Email String email,
        String senha,
        @NotNull TipoUsuario tipoUsuario,
        @DecimalMin(value = "0.00") BigDecimal saldoInicial
) {}