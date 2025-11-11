package org.example.banktransfer.services;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.banktransfer.dto.*;
import org.example.banktransfer.entities.Transferencia;
import org.example.banktransfer.entities.Usuario;
import org.example.banktransfer.enums.StatusTransferencia;
import org.example.banktransfer.enums.TipoUsuario;
import org.example.banktransfer.exception.BusinessException;
import org.example.banktransfer.exception.ExternalServiceException;
import org.example.banktransfer.exception.ForbiddenOperationException;
import org.example.banktransfer.exception.NotFoundException;
import org.example.banktransfer.repositories.TransferenciaRepository;
import org.example.banktransfer.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TransferenciaService {
    private final UsuarioRepository usuarioRepository;
    private final TransferenciaRepository transferenciaRepository;
    private final AutorizadorClient autorizadorClient;
    private final NotificacaoClient notificacaoClient;


    @Transactional
    public TransferenciaResponse transferir(TransferenciaRequest req) {
        if (req.pagadorId().equals(req.recebedorId())) {
            throw new BusinessException("Pagador e recebedor não podem ser o mesmo usuário");
        }


        if (req.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Valor deve ser maior que zero");
        }


        Usuario pagador = usuarioRepository.findByIdForUpdate(req.pagadorId())
                .orElseThrow(() -> new NotFoundException("Pagador não encontrado"));
        Usuario recebedor = usuarioRepository.findByIdForUpdate(req.recebedorId())
                .orElseThrow(() -> new NotFoundException("Recebedor não encontrado"));


        if (pagador.getTipoUsuario() == TipoUsuario.LOJISTA) {
            throw new ForbiddenOperationException("Lojista não pode enviar transferências");
        }


        if (pagador.getSaldo().compareTo(req.valor()) < 0) {
            throw new BusinessException("Saldo insuficiente");
        }

        boolean ok = autorizadorClient.autorizado();
        if (!ok) {
            throw new ExternalServiceException("Transferência não autorizada pelo serviço externo");
        }

        pagador.setSaldo(pagador.getSaldo().subtract(req.valor()));
        recebedor.setSaldo(recebedor.getSaldo().add(req.valor()));

        Transferencia t = Transferencia.builder()
                .valor(req.valor())
                .pagador(pagador)
                .recebedor(recebedor)
                .dataCriacao(OffsetDateTime.now())
                .status(StatusTransferencia.CONCLUIDA)
                .build();


        transferenciaRepository.save(t);

        notificacaoClient.notificarRecebimento(
                recebedor.getId(),
                "Você recebeu uma transferência de R$ " + req.valor()
        );


        return new TransferenciaResponse(
                t.getId(), t.getValor(), pagador.getId(), recebedor.getId(), t.getStatus().name(), t.getDataCriacao()
        );
    }

    @Transactional(readOnly = true)
    public List<TransferenciaResponse> listar() {
        return transferenciaRepository.findAllWithUsuarios()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TransferenciaResponse toResponse(Transferencia t) {
        return new TransferenciaResponse(
                t.getId(),
                t.getValor(),
                t.getPagador().getId(),
                t.getRecebedor().getId(),
                t.getStatus().name(),
                t.getDataCriacao()
        );
    }
}