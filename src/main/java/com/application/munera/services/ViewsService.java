package com.application.munera.services;

import com.application.munera.data.enums.BadgeMessage;
import com.application.munera.data.Category;
import com.application.munera.data.Expense;
import com.application.munera.data.enums.ExpenseType;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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

    /**
     * Creates a badge (Vaadin {@link Span}) based on the type of the expense and its payment status.
     *
     * @param expense the expense for which the badge is being created.
     * @return a {@link Span} object representing the expense status badge.
     */
    public Span createExpenseBadge(final Expense expense) {
        final var isExpensePaid = Boolean.TRUE.equals(this.expenseService.isExpensePaid(expense));
        final var badgeMessage = determineBadgeMessage(expense.getExpenseType(), isExpensePaid);

        final var badge = new Span();
        badge.setText(badgeMessage.getText());
        badge.getElement().getThemeList().add(badgeMessage.getTheme());

        return badge;
    }

    /**
     * Creates a badge (Vaadin {@link Span}) that reflects a person's financial balance status
     * based on the net balance provided.
     *
     * @param netBalance the net balance of the person.
     * @return a {@link Span} object representing the person's financial status badge.
     */
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

    /**
     * Applies a name-based filter on the expenses displayed in the provided grid.
     * If the filter value is empty, all expenses are displayed; otherwise, expenses
     * whose names match the filter are shown.
     *
     * @param nameFilter the {@link TextField} containing the name filter value.
     * @param userId     the ID of the user whose expenses are being filtered.
     * @param grid       the {@link PaginatedGrid} that displays the expenses.
     */
    public void applyNameFilter(TextField nameFilter, Long userId, PaginatedGrid<Expense, Objects> grid) {
        final var filterValue = nameFilter.getValue().trim();
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

    /**
     * Applies a category-based filter on the expenses displayed in the provided grid.
     * If no categories are selected, all expenses are shown; otherwise, only expenses
     * that match the selected categories are displayed.
     *
     * @param categoryFilter the {@link MultiSelectComboBox} containing the selected categories.
     * @param userId         the ID of the user whose expenses are being filtered.
     * @param grid           the {@link PaginatedGrid} that displays the expenses.
     */
    public void applyCategoryFilter(MultiSelectComboBox<Category> categoryFilter, Long userId, PaginatedGrid<Expense, Objects> grid) {
        final var selectedCategories = categoryFilter.getValue();
        List<Expense> filteredExpenses;
        if (selectedCategories.isEmpty()) filteredExpenses = expenseService.findAllOrderByDateDescending(userId); // If no categories are selected, return all expenses
        else {
            // Apply the filter by selected categories
            filteredExpenses = expenseService.findAllOrderByDateDescending(userId)
                    .stream()
                    .filter(expense1 -> selectedCategories.contains(expense1.getCategory()))
                    .toList();
        }
        grid.setItems(filteredExpenses);
    }

    /**
     * Determines the message and theme for a badge based on the expense type and its payment status.
     *
     * @param type    the type of the expense (CREDIT, DEBIT, or NONE).
     * @param isPaid  boolean indicating whether the expense has been paid.
     * @return a {@link BadgeMessage} object containing the text and theme for the badge.
     */
    private BadgeMessage determineBadgeMessage(ExpenseType type, boolean isPaid) {
        return switch (type) {
            case CREDIT -> isPaid ? BadgeMessage.PAID_TO_ME : BadgeMessage.OWED_TO_ME;
            case DEBIT -> isPaid ? BadgeMessage.PAID_BY_ME : BadgeMessage.OWED_BY_ME;
            case NONE -> isPaid ? BadgeMessage.PAID : BadgeMessage.NOT_PAID;
        };
    }
}

