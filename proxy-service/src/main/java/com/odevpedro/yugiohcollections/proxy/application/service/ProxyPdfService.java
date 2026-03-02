package com.odevpedro.yugiohcollections.proxy.application.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.odevpedro.yugiohcollections.proxy.adapter.out.external.CardSummaryDTO;
import com.odevpedro.yugiohcollections.proxy.adapter.out.external.DeckFeignClient;
import com.odevpedro.yugiohcollections.proxy.adapter.out.external.DeckViewDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyPdfService {

    // Tamanho oficial Yu-Gi-Oh
    private static final float CARD_WIDTH  = 59 * 2.8346f; // ~167pt
    private static final float CARD_HEIGHT = 86 * 2.8346f; // ~244pt

    private static final int   COLS        = 3;
    private static final int   ROWS        = 3;
    private static final float MARGIN      = 20f;
    private static final float GAP         = 5f;

    private final DeckFeignClient deckFeignClient;

    public byte[] generateProxyPdf(Long deckId) {
        DeckViewDTO deck = deckFeignClient.getDeckWithCards(deckId);

        List<CardSummaryDTO> allCards = new ArrayList<>();
        if (deck.getCards() != null) allCards.addAll(deck.getCards());

        // Expande pela quantity — 3x Blue-Eyes = 3 imagens
        List<CardSummaryDTO> expanded = new ArrayList<>();
        for (CardSummaryDTO card : allCards) {
            for (int i = 0; i < card.getQuantity(); i++) {
                expanded.add(card);
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, MARGIN, MARGIN, MARGIN, MARGIN);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            float x = MARGIN;
            float y = PageSize.A4.getHeight() - MARGIN - CARD_HEIGHT;
            int col = 0;
            int row = 0;

            for (CardSummaryDTO card : expanded) {
                if (row >= ROWS) {
                    document.newPage();
                    x = MARGIN;
                    y = PageSize.A4.getHeight() - MARGIN - CARD_HEIGHT;
                    col = 0;
                    row = 0;
                }

                placeCard(document, card, x, y);

                col++;
                x += CARD_WIDTH + GAP;

                if (col >= COLS) {
                    col = 0;
                    row++;
                    x = MARGIN;
                    y -= CARD_HEIGHT + GAP;
                }
            }

            document.close();
        } catch (Exception e) {
            log.error("Erro ao gerar PDF para deck {}", deckId, e);
            throw new RuntimeException("Falha ao gerar PDF", e);
        }

        return out.toByteArray();
    }

    private void placeCard(Document document, CardSummaryDTO card, float x, float y) {
        try (InputStream is = new URL(card.getImageUrl()).openStream()) {
            byte[] imageBytes = is.readAllBytes();
            Image image = Image.getInstance(imageBytes);
            image.setAbsolutePosition(x, y);
            image.scaleAbsolute(CARD_WIDTH, CARD_HEIGHT);
            document.add(image);
        } catch (Exception e) {
            log.warn("Falha ao baixar imagem da carta '{}' — usando placeholder", card.getName());
            // Fallback: retângulo vazio com nome da carta
            try {
                com.lowagie.text.Rectangle rect = new com.lowagie.text.Rectangle(x, y, x + CARD_WIDTH, y + CARD_HEIGHT);
                rect.setBorder(com.lowagie.text.Rectangle.BOX);
                rect.setBorderWidth(1f);
                document.add(rect);
            } catch (Exception ex) {
                log.error("Falha ao renderizar placeholder para carta '{}'", card.getName(), ex);
            }
        }
    }
}