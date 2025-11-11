package org.example.banktransfer.services;

import org.example.banktransfer.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AutorizadorClient {

    private final RestTemplate restTemplate;

    @Value("${app.external.auth-url}")
    private String authUrl;

    @Value("${app.external.auth-bypass:false}")
    private boolean authBypass;

    public boolean autorizado() {
        if (authBypass) return true;

        try {
            Map<?, ?> body = restTemplate.getForObject(authUrl, Map.class);
            if (body == null) return false;

            Object message = body.get("message");
            if (message != null && String.valueOf(message).toLowerCase().contains("autoriz")) return true;

            Object status = body.get("status");
            if (status != null && "success".equalsIgnoreCase(String.valueOf(status))) return true;

            Object authorized = body.get("authorized");
            if (authorized instanceof Boolean b) return b;

            Object data = body.get("data");
            if (data instanceof Map<?, ?> m && Boolean.TRUE.equals(m.get("authorization"))) return true;

            return false;

        } catch (RestClientResponseException ex) {
            throw new ExternalServiceException("Falha ao consultar autorizador: HTTP " + ex.getRawStatusCode());
        } catch (Exception e) {
            throw new ExternalServiceException("Erro ao interpretar resposta do autorizador");
        }
    }
}
