package com.odevpedro.yugiohcollections.proxy.adapter.in.rest;

import com.odevpedro.yugiohcollections.proxy.application.service.ProxyPdfService;
import com.odevpedro.yugiohcollections.shared.constants.ApiRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiRoutes.PROXY_BASE)
@RequiredArgsConstructor
public class ProxyController {

    private final ProxyPdfService proxyPdfService;

    @GetMapping(ApiRoutes.PROXY_BY_ID)
    public ResponseEntity<byte[]> generateProxy(@PathVariable("id") Long deckId) {
        byte[] pdf = proxyPdfService.generateProxyPdf(deckId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"deck-" + deckId + "-proxy.pdf\"")
                .body(pdf);
    }
}
