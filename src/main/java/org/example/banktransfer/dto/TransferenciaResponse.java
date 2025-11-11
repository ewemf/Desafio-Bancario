package org.example.banktransfer.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


public record TransferenciaResponse(
        Long id,
        BigDecimal valor,
        Long pagadorId,
        Long recebedorId,
        String status,
        OffsetDateTime data
) {}