package com.odevpedro.yugiohcollections.card.adapter.in.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class IpController {
    @GetMapping("/vote")
    public ResponseEntity<String> vote(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        System.out.println("IP resolvido: " + clientIp);
        return ResponseEntity.ok("Voto registrado de IP: " + clientIp);
    }

    private String getClientIp(HttpServletRequest request) {
        // Ordem de prioridade: headers comuns no Azure e proxies
        String[] headerCandidates = {
                "X-Forwarded-For",    // padrão de facto em Azure Front Door / App Gateway / App Service
                "X-Azure-ClientIP",   // às vezes presente no Azure
                "X-Real-IP"           // usado por alguns ingress NGINX
        };

        for (String header : headerCandidates) {
            String ipList = request.getHeader(header);
            if (ipList != null && !ipList.isBlank()) {
                // X-Forwarded-For pode conter vários IPs separados por vírgula
                String ip = ipList.split(",")[0].trim();
                return ip;
            }
        }

        // fallback para o IP do socket
        return request.getRemoteAddr();
    }

}
