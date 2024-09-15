package com.application.munera.views.categories;

import com.application.munera.data.Category;
import com.application.munera.services.CategoryService;
import com.application.munera.services.UserService;
import com.application.munera.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

@PageTitle("Categories")
@PermitAll
@Route(value = "categories/:categoryID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
public class CategoriesView extends Div implements BeforeEnterObserver {

    private static final String CATEGORY_ID = "categoryID";
    private static final String CATEGORY_EDIT_ROUTE_TEMPLATE = "categories/%s/edit";

    private final Grid<Category> grid = new Grid<>(Category.class, false);

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private final BeanValidationBinder<Category> binder;

    private Category category;
    private final CategoryService categoryService;
    private final UserService userService;
    private TextField name;
    private TextArea description;

    public CategoriesView(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
        final var userId = this.userService.getLoggedInUser().getId();
        addClassNames("expenses-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);
        add(splitLayout);

        // Configure Grid
        grid.addColumn(Category::getName).setHeader("Name").setSortable(true);
        grid.addColumn(Category::getDescription).setHeader("Description").setSortable(true);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.setItems(this.categoryService.findAllByUserId(userId));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) UI.getCurrent().navigate(String.format(CATEGORY_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            else {
                clearForm();
                UI.getCurrent().navigate(CategoriesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Category.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.bindInstanceFields(this);
        binder.forField(name)
                .asRequired("Name is required")
                .bind(Category::getName, Category::setName);


        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.category == null) {
                    this.category = new Category();
                }
                binder.writeBean(this.category);
                categoryService.update(this.category, userId);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(CategoriesView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the category. Check again that all values are valid");
            }
        });

        delete.addClickListener(e -> {
            try {
                if (this.category == null) throw new RuntimeException("Category is null!"); //TODO: create proper exception
                categoryService.delete(this.category);
                clearForm();
                refreshGrid();
                Notification.show("Data deleted");
                UI.getCurrent().navigate(CategoriesView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (DataIntegrityViolationException ex) {
                Notification n = Notification.show(
                        "Cannot delete this category as it is associated with existing expenses.");
                n.setPosition(Notification.Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> categoryId = event.getRouteParameters().get(CATEGORY_ID).map(Long::parseLong);
        if (categoryId.isPresent()) {
            Optional<Category> categoryFromBackend = categoryService.findById(categoryId.get());
            if (categoryFromBackend.isPresent()) {
                populateForm(categoryFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested category was not found, ID = %s", categoryId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(CategoriesView.class);
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
        name = new TextField("Name");
        description = new TextArea("Description");
        formLayout.add(name, description);
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

    private void populateForm(Category value) {
        this.category = value;
        binder.readBean(this.category);
    }
}
