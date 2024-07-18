package com.application.munera.views.people;

import com.application.munera.data.Expense;
import com.application.munera.data.Person;
import com.application.munera.services.ExpenseService;
import com.application.munera.services.PersonService;
import com.application.munera.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@PageTitle("People")
@PermitAll
@Route(value = "people/:personID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
public class PeopleView extends Div implements BeforeEnterObserver {

    private static final String PERSON_ID = "personID";
    private static final String PERSON_EDIT_ROUTE_TEMPLATE = "people/%s/edit";

    private final TreeGrid<Object> grid = new TreeGrid<>();

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private final BeanValidationBinder<Person> binder;

    private Person person;
    private final PersonService personService;
    private final ExpenseService expenseService;
    private TextField firstName;
    private TextField lastName;
    private EmailField email;

    public PeopleView(PersonService personService, ExpenseService expenseService) {
        this.personService = personService;
        this.expenseService = expenseService;
        addClassNames("expenses-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addHierarchyColumn(this::getNodeName).setHeader("Name");
        grid.addColumn(this::getNodeCost).setHeader("Total Expenses Value").setSortable(true);
        grid.addColumn(new ComponentRenderer<>(persona -> {
            if (persona instanceof Person) return createPersonBadge(personService.calculateNetBalance((Person) persona));
            else return createExpenseBadge(((Expense) persona).getIsResolved());
        })).setHeader("Balance Status");

        List<Person> people = (List<Person>) personService.findAll();

        this.setGridData(people);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            Object selectedItem = event.getValue();
            if (selectedItem instanceof Person selectedPerson) UI.getCurrent().navigate(String.format(PERSON_EDIT_ROUTE_TEMPLATE, selectedPerson.getId()));
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Person.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.person == null) {
                    this.person = new Person();
                }
                binder.writeBean(this.person);
                personService.update(this.person);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(PeopleView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });

        delete.addClickListener(e -> {
            try {
                if (this.person == null) throw new RuntimeException("The person is null!"); //TODO: create proper exception
                personService.delete(this.person.getId());
                clearForm();
                refreshGrid();
                Notification.show("Data delete");
                UI.getCurrent().navigate(PeopleView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }

    private String getNodeName(Object node) {
        if (node instanceof Person) return ((Person) node).getFirstName() + " " + ((Person) node).getLastName();
        else if (node instanceof Expense) return ((Expense) node).getName();
        return "";
    }

    private String getNodeCost(Object node) {
        if (node instanceof Person) return this.personService.calculateNetBalance((Person) node).toString() + " €";
        else if (node instanceof Expense) return ((Expense) node).getCost().toString() + " €";
        return "";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> personId = event.getRouteParameters().get(PERSON_ID).map(Long::parseLong);
        if (personId.isPresent()) {
            Optional<Person> personFromBackend = personService.get(personId.get());
            if (personFromBackend.isPresent()) populateForm(personFromBackend.get());
            else {
                Notification.show(
                        String.format("The requested person was not found, ID = %s", personId.get()), 3000,
                        Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(PeopleView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        email = new EmailField("Email");

        // We set the maximum parallel columns to 1
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        formLayout.add(firstName, lastName, email);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, delete, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Person value) {
        this.person = value;
        binder.readBean(this.person);

    }

    private Span createPersonBadge(BigDecimal netBalance) {
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

    private Span createExpenseBadge(Boolean isExpenseResolved) {
        Span badge = new Span();
        if (Boolean.TRUE.equals(isExpenseResolved)) {
            badge.setText("Resolved");
            badge.getElement().getThemeList().add("badge success");
        } else  {
            badge.setText("To be Resolved");
            badge.getElement().getThemeList().add("badge error");
        }
        return badge;
    }

    public void setGridData(List<Person> people) {
        for (Person person : people) {
            // Add the person as a root item
            grid.getTreeData().addItem(null, person);

            // Fetch expenses for the current person
            List<Expense> expenses =  expenseService.findExpenseByUser(person);

            // Add each expense as a child item under the person
            for (Expense expense : expenses) grid.getTreeData().addItem(person, expense);
        }
    }
}