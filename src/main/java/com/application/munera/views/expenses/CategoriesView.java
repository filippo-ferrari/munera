package com.application.munera.views.expenses;

import com.application.munera.data.Category;
import com.application.munera.services.CategoryService;
import com.application.munera.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@PageTitle("Categories")
@Route(value = "categories/:categoryID?/:action?(edit)", layout = MainLayout.class)
public class CategoriesView extends VerticalLayout implements BeforeEnterObserver {

    private final String CATEGORY_ID = "categoryID";
    private final TextField categoryNameField = new TextField("Category Name");
    private final Button addButton = new Button("Add Category");
    private final Grid<Category> categoryGrid = new Grid<>(Category.class);
    private final Button removeButton = new Button("Remove Category");

    @Autowired
    private final CategoryService categoryService;

    private final BeanValidationBinder<Category> binder;
    private Category category;

    public CategoriesView(CategoryService categoryService) {
        this.categoryService = categoryService;

        FormLayout formLayout = new FormLayout();
        formLayout.add(categoryNameField, addButton);

        categoryGrid.setColumns("id", "name");
        categoryGrid.setItems(categoryService.findAll());
        categoryGrid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format("/manage-categories/%d/edit", event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(CategoriesView.class);
            }
        });

        removeButton.setEnabled(false);
        removeButton.addClickListener(event -> removeCategory());

        addButton.addClickListener(event -> addCategory());

        binder = new BeanValidationBinder<>(Category.class);
        binder.bindInstanceFields(this);

        add(formLayout, categoryGrid, removeButton);
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
                event.forwardTo(ExpensesView.class);
            }
        }
    }

    private void addCategory() {
        Category newCategory = new Category();
        binder.writeBeanIfValid(newCategory);
        categoryService.update(newCategory);
        refreshGrid();
        Notification.show("Category added successfully");
    }

    private void removeCategory() {
        if (category != null) {
            categoryService.delete(category);
            clearForm();
            refreshGrid();
            Notification.show("Category removed successfully");
            UI.getCurrent().navigate(CategoriesView.class);
        }
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Category value) {
        this.category = value;
        binder.readBean(this.category);
    }

    private void refreshGrid() {
        categoryGrid.setItems(categoryService.findAll());
    }
}
