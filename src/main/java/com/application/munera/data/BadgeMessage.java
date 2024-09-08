package com.application.munera.data;

import lombok.Getter;

@Getter
public enum BadgeMessage {
    PAID_TO_ME("Paid to me", "badge success"),
    PAID_BY_ME("Paid by me", "badge success"),
    PAID("Paid", "badge success"),
    OWED_TO_ME("Owed to me", "badge warning"),
    OWED_BY_ME("Owed by me", "badge warning"),
    NOT_PAID("Not paid", "badge warning"),
    UNKNOWN("Unknown status", "badge error");

    private final String text;
    private final String theme;

    BadgeMessage(String text, String theme) {
        this.text = text;
        this.theme = theme;
    }

}
