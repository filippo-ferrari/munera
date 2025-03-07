package com.application.munera.views.expenses;

import com.application.munera.data.Category;
import com.application.munera.data.Expense;
import com.application.munera.data.enums.PeriodUnit;
import com.application.munera.data.Person;
import com.application.munera.facades.PersonFacade;
import com.application.munera.services.CategoryService;
import com.application.munera.services.ExpenseService;
import com.application.munera.services.UserService;
import com.application.munera.services.ViewsService;
import com.application.munera.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.klaudeta.PaginatedGrid;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@PermitAll
@PageTitle("Expenses")
@Route(value = "/:expenseID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class ExpensesView extends Div implements BeforeEnterObserver {

    private static final String EXPENSE_ID = "expenseID";
    private static final String EXPENSE_EDIT_ROUTE_TEMPLATE = "/%s/edit";

    private final PaginatedGrid<Expense, Objects> grid = new PaginatedGrid<>();
    private final TextField nameFilter = new TextField();
    private final MultiSelectComboBox<Category> categoryFilter = new MultiSelectComboBox<>();
    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final BeanValidationBinder<Expense> binder;

    private Expense expense;
    private final Long userId;

    private final ExpenseService expenseService;
    private final PersonFacade personFacade;
    private final CategoryService categoryService;
    private final ViewsService viewsService;
    private final UserService userService;
    private TextField name;
    private TextField cost;
    private ComboBox<Category> category;
    private TextArea description;
    private Checkbox isPeriodic;
    private Checkbox isPaid;
    private ComboBox<PeriodUnit> periodUnit;
    private TextField periodInterval;
    private DatePicker date;
    private ComboBox<Person> payer;
    private ComboBox<Person> beneficiary;

    @Autowired
    public ExpensesView(ExpenseService expenseService, CategoryService categoryService, ViewsService viewsService, UserService userService, PersonFacade personFacade) {
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.viewsService = viewsService;
        this.userService = userService;
        this.personFacade = personFacade;
        this.userId = this.userService.getLoggedInUser().getId();
        addClassNames("expenses-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn(Expense::getName).setHeader("Name").setSortable(true).setSortProperty("name");
        grid.addColumn(Expense::getCost).setHeader("Amount").setSortable(true).setSortProperty("cost");
        grid.addColumn(expenseCategory -> expenseCategory.getCategory().getName()).setHeader("Category").setSortable(true).setSortProperty("category");
        grid.addColumn(Expense::getDate).setHeader("Date").setSortable(true).setSortProperty("date");
        grid.addColumn(new ComponentRenderer<>(this.viewsService::createExpenseBadge)).setHeader("Status").setSortable(true);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.setItems(this.expenseService.findAllOrderByDateDescending(userId));
        grid.setPaginatorSize(5);
        grid.setPageSize(22); // setting page size
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // Filtering setup - Name
        nameFilter.setPlaceholder("Filter by Name...");
        nameFilter.setClearButtonVisible(true);
        nameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        nameFilter.addValueChangeListener(e -> this.viewsService.applyNameFilter(nameFilter, userId, grid));

        // Filtering setup - Category
        categoryFilter.setPlaceholder("Filter by Category...");
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.setItems(categoryService.findAllByUserId(userId));
        categoryFilter.setItemLabelGenerator(Category::getName);
        categoryFilter.addValueChangeListener(e -> this.viewsService.applyCategoryFilter(categoryFilter, userId, grid));

        // Add filter fields to layout (above the grid)
        VerticalLayout layout = new VerticalLayout();
        HorizontalLayout filterLayout = new HorizontalLayout(nameFilter, categoryFilter);
        filterLayout.setSpacing(true);
        filterLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        layout.add(filterLayout, grid);
        splitLayout.addToPrimary(layout);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) UI.getCurrent().navigate(String.format(EXPENSE_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
             else {
                clearForm();
                UI.getCurrent().navigate(ExpensesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Expense.class);
        binder.bindInstanceFields(this);
        binder.forField(name)
                .asRequired("Name is required")
                .bind(Expense::getName, Expense::setName);
        binder.forField(cost)
                .asRequired("Cost is required")
                .withConverter(new StringToBigDecimalConverter("Invalid cost"))
                .withValidator(costValue -> costValue.compareTo(BigDecimal.ONE) > 0, "Cost must be greater than 1")
                .bind(Expense::getCost, Expense::setCost);
        binder.forField(category)
                .asRequired("Category is required")
                .bind(Expense::getCategory, Expense::setCategory);
        binder.forField(date)
                .asRequired("Date is required")
                .bind(Expense::getDate, Expense::setDate);

        // We set initial value of isPeriodic to true and show period fields
        isPeriodic.setValue(false);
        isPaid.setValue(false);
        periodUnit.setVisible(false);
        periodInterval.setVisible(false);

        // We show the periodic fields only when the isPeriodic boolean is true
        isPeriodic.addValueChangeListener(event -> {
            boolean isPeriodicChecked = event.getValue();
            periodUnit.setVisible(isPeriodicChecked);
            periodInterval.setVisible(isPeriodicChecked);

            // Clear periodUnit and periodInterval if isPeriodic is unchecked
            if (!isPeriodicChecked) {
                periodUnit.clear();
                periodInterval.clear();
            }
        });

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.expense == null) this.expense = new Expense();
                binder.writeBean(this.expense);
                expenseService.update(this.expense, userId);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(ExpensesView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the expense. Check again that all values are valid");
            }
        });

        delete.addClickListener(e -> {
            try {
                if (Objects.isNull(this.expense)) throw new IllegalStateException("Expense is null!");
                expenseService.delete(this.expense.getId());
                clearForm();
                refreshGrid();
                Notification.show("Data deleted");
                UI.getCurrent().navigate(ExpensesView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error deleting the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        initializeComboBoxes();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        initializeComboBoxes();
        Optional<Long> expenseId = event.getRouteParameters().get(EXPENSE_ID).map(Long::parseLong);
        if (expenseId.isPresent()) {
            Optional<Expense> expenseFromBackend = expenseService.get(expenseId.get());
            if (expenseFromBackend.isPresent()) populateForm(expenseFromBackend.get());
            else {
                Notification.show(
                        String.format("The requested expense was not found, ID = %s", expenseId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(ExpensesView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");
        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);
        final var people = this.personFacade.findAllByUserId(userId);

        FormLayout formLayout = new FormLayout();
        name = new TextField("Name");
        cost = new TextField("Cost");
        category = new ComboBox<>("Category");
        category.setItems(categoryService.findAllByUserId(userId));
        category.setItemLabelGenerator(Category::getName);
        description = new TextArea("Description");
        periodUnit = new ComboBox<>("Period Unit");
        periodUnit.setItems(PeriodUnit.values());
        periodInterval = new TextField("Period Interval");
        payer = new ComboBox<>("Payer");
        payer.setItems(people);
        payer.setItemLabelGenerator(person -> person.getFirstName() + " " + person.getLastName());
        beneficiary = new ComboBox<>("Beneficiary");
        beneficiary.setItems(people);
        beneficiary.setItemLabelGenerator(person -> person.getFirstName() + " " + person.getLastName());
        date = new DatePicker("Date");

        // Horizontal layout for checkboxes
        HorizontalLayout checkboxLayout = new HorizontalLayout();
        isPeriodic = new Checkbox("Is Periodic");
        isPaid = new Checkbox("Paid");
        checkboxLayout.add(isPeriodic, isPaid);

        formLayout.add(name, cost, category, description, checkboxLayout, periodUnit, periodInterval, date, payer, beneficiary);
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
        grid.setItems(this.expenseService.findAllOrderByDateDescending(userId));
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Expense value) {
        this.expense = value;
        binder.readBean(this.expense);
        boolean isPeriodicChecked = (value != null) && value.getIsPeriodic();
        periodUnit.setVisible(isPeriodicChecked);
        periodInterval.setVisible(isPeriodicChecked);
    }

    private void initializeComboBoxes() {
        final var loggedInPerson = this.personFacade.getLoggedInPerson();
        payer.setValue(loggedInPerson);
        beneficiary.setValue(loggedInPerson);
    }
}