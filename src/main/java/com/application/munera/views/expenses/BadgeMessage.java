package com.application.munera.views.expenses;

public enum BadgeMessage {
    PAID_TO_SOMEONE("Paid to someone", "badge success"),
    PAID_TO_YOU("Paid to you", "badge success"),
    PAID("Paid", "badge success"),
    OWED_BY_SOMEONE("Owed by someone", "badge warning"),
    OWED_TO_YOU("Owed to you", "badge warning"),
    NOT_PAID("Not paid", "badge warning"),
    UNKNOWN("Unknown status", "badge error");

    private final String text;
    private final String theme;

    BadgeMessage(String text, String theme) {
        this.text = text;
        this.theme = theme;
    }

    public String getText() {
        return text;
    }

    public String getTheme() {
        return theme;
    }
}
