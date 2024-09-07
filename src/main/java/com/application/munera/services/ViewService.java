package com.application.munera.services;

import com.application.munera.data.BadgeMessage;
import com.application.munera.data.Expense;
import com.application.munera.data.ExpenseType;
import com.vaadin.flow.component.html.Span;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ViewService {

    private final ExpenseService expenseService;

    public ViewService(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    public Span createExpenseBadge(final Expense expense) {
        final var isExpensePaid = Boolean.TRUE.equals(this.expenseService.isExpensePaid(expense));
        final var badgeMessage = determineBadgeMessage(expense.getExpenseType(), isExpensePaid);

        final var badge = new Span();
        badge.setText(badgeMessage.getText());
        badge.getElement().getThemeList().add(badgeMessage.getTheme());

        return badge;
    }

    public Span createPersonBadge(BigDecimal netBalance) {
        Span badge = new Span();
        if (netBalance.compareTo(BigDecimal.ZERO) < 0) {
            badge.setText("Credit");
            badge.getElement().getThemeList().add("badge success");
        } else if (netBalance.compareTo(BigDecimal.ZERO) > 0) {
            badge.setText("Debit");
            badge.getElement().getThemeList().add("badge error");
        } else {
            badge.setText("Clear");
            badge.getElement().getThemeList().add("badge contrast");
        }
        return badge;
    }

    private BadgeMessage determineBadgeMessage(ExpenseType type, boolean isPaid) {
        return switch (type) {
            case CREDIT -> isPaid ? BadgeMessage.PAID_TO_SOMEONE : BadgeMessage.OWED_BY_SOMEONE;
            case DEBIT -> isPaid ? BadgeMessage.PAID_TO_YOU : BadgeMessage.OWED_TO_YOU;
            case NONE -> isPaid ? BadgeMessage.PAID : BadgeMessage.NOT_PAID;
            default -> BadgeMessage.UNKNOWN;
        };
    }
}

