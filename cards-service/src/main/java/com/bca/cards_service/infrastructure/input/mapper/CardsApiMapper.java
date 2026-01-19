package com.bca.cards_service.infrastructure.input.mapper;

import java.time.ZoneOffset;

import com.bca.cards_service.domain.model.card.Card;
import com.bca.cards_service.dto.CardResponse;

public class CardsApiMapper {

    private CardsApiMapper() {
        throw new IllegalStateException("Utility class");
    }
    
    public static CardResponse mapToCardResponse(Card card) {
        CardResponse response = new CardResponse();
        response.setId(card.getId());
        response.setStatus(CardResponse.StatusEnum.valueOf(card.getStatus().name()));
        response.setType(CardResponse.TypeEnum.valueOf(card.getType().name())); 
        response.setCustomerId(card.getCustomerId() != null ? card.getCustomerId() : null);
        response.setMaskedNumber(card.getMaskedNumber());
        if (card.getCreatedAt() != null) {
            response.setCreatedAt(card.getCreatedAt().atZone(ZoneOffset.UTC).toOffsetDateTime());
        }

        response.setAvailableLimit(card.getAvailableLimit().floatValue());
        
        return response;
    }
}
