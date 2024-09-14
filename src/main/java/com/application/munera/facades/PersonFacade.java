package com.application.munera.facades;

import com.application.munera.data.Expense;
import com.application.munera.data.Person;
import com.application.munera.data.User;
import com.application.munera.services.ExpenseService;
import com.application.munera.services.PersonService;
import com.application.munera.services.UserService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.treegrid.TreeGrid;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class PersonFacade {

    private final ExpenseService expenseService;
    private final UserService userService;
    private final PersonService personService;

    public PersonFacade(ExpenseService expenseService, UserService userService, PersonService personService) {
        this.expenseService = expenseService;
        this.userService = userService;
        this.personService = personService;
    }

    /**
     * Fetches the {@code Person} entity associated with the currently logged-in user.
     *
     * @return the {@code Person} entity of the logged-in user, or {@code null} if not found
     */
    public Person getLoggedInPerson() {
        final var user = userService.getLoggedInUser();
        return Objects.requireNonNull(personService.findByUsername(user.getUsername()));
    }

    /**
     * Fetches all the people related to the user
     * @param userId the id of the user related to the people
     * @return the list of people found related to the id of the user
     */
    public List<Person> findAllByUserId(Long userId) {
        return this.personService.findAllByUserId(userId);
    }

    /**
     * Finds a {@link Person} entity by its ID.
     *
     * @param id the ID of the person to find
     * @return an {@link Optional} containing the person if found, or an empty {@link Optional} if not found
     */
    public Optional<Person> findById(Long id) {
        return this.personService.findById(id);
    }

    /**
     * Updates the details of a {@link Person} entity and associates it with a specific user.
     * This method will save the updated person details and associate it with the given user ID.
     *
     * @param person the {@link Person} entity to update
     * @param userId the ID of the user associated with the person
     */
    public void update(Person person, Long userId) {
        this.personService.update(person, userId);
    }

    /**
     * Deletes a {@link Person} entity by its ID.
     * This method removes the person from the system based on the provided ID.
     *
     * @param id the ID of the person to delete
     */
    public void delete(Long id) {
        this.personService.delete(id);
    }

    /**
     * Finds all {@code Person} entities associated with the logged-in user, excluding the logged-in user.
     *
     * @param user the logged-in user whose associated persons are to be retrieved
     * @return a {@code List} of {@code Person} entities excluding the logged-in user
     */
    public List<Person> findAllExcludeLoggedUser(final User user) {
        return this.personService.findAllExcludeLoggedUser(user);
    }

    /**
     * Calculates the net balance for a given {@code Person}.
     *
     * @param person the {@code Person} for whom the net balance is to be calculated
     * @return the net balance as a {@code BigDecimal}
     */
    public BigDecimal calculateNetBalance(final Person person) {
        return this.personService.calculateNetBalance(person);
    }

    /**
     * Marks all expenses as paid for the given {@code Person} where the person is the payer.
     * Updates the user interface to reflect the changes and provides notifications for success or failure.
     *
     * @param person the {@code Person} whose expenses are to be marked as paid
     * @param grid the {@code TreeGrid} component to refresh after updating expenses
     * @param userId the ID of the user performing the update
     */
    public void setDebtPaid(Person person, TreeGrid<Object> grid, Long userId) {
        try {
            List<Expense> expenses = expenseService.findExpensesWherePayer(person).stream().toList();
            for (Expense expense : expenses) {
                expense.setIsPaid(true);
                expenseService.update(expense, userId);
            }
            Notification.show("All expenses marked as paid for " + person.getFirstName() + " " + person.getLastName());
            grid.select(null);
            grid.getDataProvider().refreshAll();
        } catch (Exception e) {
            Notification n = Notification.show("Error marking expenses as paid: " + e.getMessage());
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * Marks all expenses as paid for the given {@code Person} where the person is the beneficiary.
     * Updates the user interface to reflect the changes and provides notifications for success or failure.
     *
     * @param person the {@code Person} whose expenses are to be marked as paid
     * @param grid the {@code TreeGrid} component to refresh after updating expenses
     * @param userId the ID of the user performing the update
     */
    public void setCreditPaid(Person person, TreeGrid<Object> grid, Long userId) {
        try {
            List<Expense> expenses = expenseService.findExpensesWhereBeneficiary(person).stream().toList();
            for (Expense expense : expenses) {
                expense.setIsPaid(true);
                expenseService.update(expense, userId);
            }
            Notification.show("All expenses marked as paid for " + person.getFirstName() + " " + person.getLastName());
            grid.select(null);
            grid.getDataProvider().refreshAll();
        } catch (Exception e) {
            Notification n = Notification.show("Error marking expenses as paid: " + e.getMessage());
            n.setPosition(Notification.Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}