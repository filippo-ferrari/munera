package com.application.munera.services;

import com.application.munera.data.BadgeMessage;
import com.application.munera.data.Expense;
import com.application.munera.data.ExpenseType;
import com.vaadin.flow.component.html.Span;
import org.springframework.stereotype.Service;

@Service
public class ViewService {

    private final ExpenseService expenseService;

    public ViewService(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    public Span createBadge(final Expense expense) {
        final var isExpensePaid = Boolean.TRUE.equals(this.expenseService.isExpensePaid(expense));
        final var badgeMessage = determineBadgeMessage(expense.getExpenseType(), isExpensePaid);

        final var badge = new Span();
        badge.setText(badgeMessage.getText());
        badge.getElement().getThemeList().add(badgeMessage.getTheme());

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

