package com.lbs.user.card.controller;

import javax.smartcardio.Card;

public interface CardService {
    Card createCard(Card card);
    Card readCard(Long id);
    Card updateCard(Card card);
    Card deleteCard(Long id);
}
