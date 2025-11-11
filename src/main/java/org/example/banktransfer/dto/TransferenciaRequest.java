package org.example.banktransfer.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;


public record TransferenciaRequest(
        @DecimalMin("0.01") BigDecimal valor,
        Long pagadorId,
        Long recebedorId
) {}