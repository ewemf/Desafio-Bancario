package org.example.banktransfer.services;

import lombok.RequiredArgsConstructor;
import org.example.banktransfer.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificacaoClient {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${app.external.notify-url}")
    private String notifyUrl;

    public void notificarRecebimento(Long recebedorId, String mensagem) {
        Map<String, Object> payload = Map.of("to", recebedorId, "message", mensagem);
        try {
            ResponseEntity<String> resp = restTemplate.postForEntity(notifyUrl, payload, String.class);

            if (!resp.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("Falha ao enviar notificação: HTTP " + resp.getStatusCodeValue());
            }

            if (resp.getStatusCode().value() == 204) return;

            String body = resp.getBody();
            if (body != null && !body.isBlank()) {
                try {
                    Map<?,?> m = mapper.readValue(body, Map.class);
                    Object status = m.get("status");
                    if (status != null) {
                        String s = String.valueOf(status).toLowerCase();
                        if (s.contains("error") || s.contains("fail")) {
                            Object msg = m.get("message");
                            throw new ExternalServiceException("Falha ao enviar notificação: " + (msg != null ? msg : "status=error"));
                        }
                    }
                } catch (Exception ignore) {
                }
            }
        } catch (RestClientResponseException ex) {
            throw new ExternalServiceException("Falha ao enviar notificação: HTTP " + ex.getRawStatusCode());
        } catch (ExternalServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalServiceException("Erro ao interpretar resposta do serviço de notificação");
        }
    }
}
