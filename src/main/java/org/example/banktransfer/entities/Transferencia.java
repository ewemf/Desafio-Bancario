package org.example.banktransfer.entities;

import org.example.banktransfer.enums.StatusTransferencia;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Entity
@Table(name = "transferencias")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transferencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pagador_id")
    private Usuario pagador;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recebedor_id")
    private Usuario recebedor;


    @Column(nullable = false)
    private OffsetDateTime dataCriacao;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransferencia status;
}