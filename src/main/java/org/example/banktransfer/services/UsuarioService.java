package org.example.banktransfer.services;

import org.example.banktransfer.dto.*;
import org.example.banktransfer.entities.Usuario;
import org.example.banktransfer.enums.TipoUsuario;
import org.example.banktransfer.exception.BusinessException;
import org.example.banktransfer.exception.NotFoundException;
import org.example.banktransfer.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioResponse criar(UsuarioRequest req) {
        usuarioRepository.findByEmail(req.email()).ifPresent(u -> { throw new BusinessException("Email já cadastrado"); });
        usuarioRepository.findByCpfOuCnpj(req.cpfOuCnpj()).ifPresent(u -> { throw new BusinessException("CPF/CNPJ já cadastrado"); });


        Usuario u = Usuario.builder()
                .nomeCompleto(req.nomeCompleto())
                .cpfOuCnpj(req.cpfOuCnpj())
                .email(req.email())
                .senha(req.senha())
                .tipoUsuario(req.tipoUsuario())
                .saldo(req.saldoInicial() != null ? req.saldoInicial() : BigDecimal.ZERO)
                .build();


        Usuario saved = usuarioRepository.save(u);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Long id) {
        return toResponse(get(id));
    }

    @Transactional(readOnly = true)
    public Usuario get(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    private UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNomeCompleto(), u.getCpfOuCnpj(), u.getEmail(), u.getTipoUsuario(), u.getSaldo());
    }
}