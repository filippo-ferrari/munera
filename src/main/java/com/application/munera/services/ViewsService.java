package com.application.munera.services;

import com.application.munera.data.BadgeMessage;
import com.application.munera.data.Expense;
import com.application.munera.data.ExpenseType;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.stereotype.Service;
import org.vaadin.klaudeta.PaginatedGrid;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class ViewsService {

    private final ExpenseService expenseService;

    public ViewsService(ExpenseService expenseService) {
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

    public void applyFilter(TextField nameFilter, Long userId, PaginatedGrid<Expense, Objects> grid) {
        String filterValue = nameFilter.getValue().trim();
        List<Expense> filteredExpenses;
        if (filterValue.isEmpty()) filteredExpenses = expenseService.findAllOrderByDateDescending(userId); // If the filter is empty, return all expenses
        else {
            // Apply the filter (e.g., by name)
            filteredExpenses = expenseService.findAllOrderByDateDescending(userId)
                    .stream()
                    .filter(expense -> expense.getName().toLowerCase().contains(filterValue.toLowerCase())).toList();
        }
        grid.setItems(filteredExpenses);
    }

    private BadgeMessage determineBadgeMessage(ExpenseType type, boolean isPaid) {
        return switch (type) {
            case CREDIT -> isPaid ? BadgeMessage.PAID_TO_ME : BadgeMessage.OWED_TO_ME;
            case DEBIT -> isPaid ? BadgeMessage.PAID_BY_ME : BadgeMessage.OWED_BY_ME;
            case NONE -> isPaid ? BadgeMessage.PAID : BadgeMessage.NOT_PAID;
            default -> BadgeMessage.UNKNOWN;
        };
    }
}

