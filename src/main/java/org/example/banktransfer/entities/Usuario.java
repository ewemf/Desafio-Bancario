package org.example.banktransfer.entities;

import org.example.banktransfer.enums.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;


@Entity
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cpf_cnpj", columnNames = "cpf_ou_cnpj"),
        @UniqueConstraint(name = "uk_email", columnNames = "email")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "nome_completo", nullable = false)
    private String nomeCompleto;


    @Column(name = "cpf_ou_cnpj", nullable = false, length = 20)
    private String cpfOuCnpj;


    @Column(name = "email", nullable = false)
    private String email;


    @Column(name = "senha", nullable = false)
    private String senha;


    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario;


    @Column(name = "saldo", nullable = false, precision = 19, scale = 2)
    private BigDecimal saldo;
}
