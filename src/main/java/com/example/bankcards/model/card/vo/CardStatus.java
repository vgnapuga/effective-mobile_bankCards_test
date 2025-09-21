package com.example.bankcards.model.card.vo;


import com.example.bankcards.model.BaseValueObject;


public final class CardStatus extends BaseValueObject<CardStatus.State> {

    public enum State {
        ACTIVE, BLOCKED, EXPIRED
    }

    public CardStatus(final State value) {
        super(value);
    }

    @Override
    protected void checkValidation(final State value) {
        return;
    }

}
