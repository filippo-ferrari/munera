package com.application.munera.views.expenses;

import com.application.munera.data.Category;
import com.application.munera.data.Expense;
import com.application.munera.data.Person;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class Filters extends Div implements Specification<Expense> {

    private final TextField name = new TextField("Expense's name");
    private final ComboBox<Category> category = new ComboBox<>("Category");
    private final DatePicker startDate = new DatePicker("Date of Birth");
    private final DatePicker endDate = new DatePicker();
    private final MultiSelectComboBox<Person> creditors = new MultiSelectComboBox<>("Creditors");
    private final CheckboxGroup<String> isResolved = new CheckboxGroup<>("Role");

    public Filters(Runnable onSearch, List<Category> categoriesNames, List<Person> creditorsNames) {
        setWidthFull();
        addClassName("filter-layout");
        addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                LumoUtility.BoxSizing.BORDER);
        name.setPlaceholder("Expense's name");
        final var names = creditorsNames.stream().map(Person::getFirstName).toArray();
        creditors.setItems((creditorsNames));

        isResolved.setItems("Worker", "Supervisor", "Manager", "External");
        isResolved.addClassName("double-width");

        // Action buttons
        Button resetBtn = new Button("Reset");
        resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        resetBtn.addClickListener(e -> {
            name.clear();
            category.clear();
            startDate.clear();
            endDate.clear();
            creditors.clear();
            isResolved.clear();
            onSearch.run();
        });
        Button searchBtn = new Button("Search");
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchBtn.addClickListener(e -> onSearch.run());

        Div actions = new Div(resetBtn, searchBtn);
        actions.addClassName(LumoUtility.Gap.SMALL);
        actions.addClassName("actions");

        add(name, category, creditors, isResolved, actions);
    }

    private Component createDateRangeFilter() {
        startDate.setPlaceholder("From");

        endDate.setPlaceholder("To");

        // For screen readers
        startDate.setAriaLabel("From date");
        endDate.setAriaLabel("To date");

        FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
        dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);

        return dateRangeComponent;
    }

    @Override
    public Predicate toPredicate(Root<Expense> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (!name.isEmpty()) {
            String lowerCaseFilter = name.getValue().toLowerCase();
            Predicate firstNameMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    lowerCaseFilter + "%");
            predicates.add(firstNameMatch);
        }

//        if (!category.isEmpty()) {
//            String databaseColumn = "phone";
//            String ignore = "- ()";
//
//            String lowerCaseFilter = ignoreCharacters(ignore, category.getValue().toLowerCase());
//            Predicate phoneMatch = criteriaBuilder.like(
//                    ignoreCharacters(ignore, criteriaBuilder, criteriaBuilder.lower(root.get(databaseColumn))),
//                    "%" + lowerCaseFilter + "%");
//            predicates.add(phoneMatch);
//
//        }
        if (startDate.getValue() != null) {
            String databaseColumn = "dateOfBirth";
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(databaseColumn),
                    criteriaBuilder.literal(startDate.getValue())));
        }
        if (endDate.getValue() != null) {
            String databaseColumn = "dateOfBirth";
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.literal(endDate.getValue()),
                    root.get(databaseColumn)));
        }
//        if (!creditors.isEmpty()) {
//            String databaseColumn = "occupation";
//            List<Predicate> occupationPredicates = new ArrayList<>();
//            for (String occupation : creditors.getValue()) {
//                occupationPredicates
//                        .add(criteriaBuilder.equal(criteriaBuilder.literal(occupation), root.get(databaseColumn)));
//            }
//            predicates.add(criteriaBuilder.or(occupationPredicates.toArray(Predicate[]::new)));
//        }
        if (!isResolved.isEmpty()) {
            String databaseColumn = "role";
            List<Predicate> rolePredicates = new ArrayList<>();
            for (String role : isResolved.getValue()) {
                rolePredicates.add(criteriaBuilder.equal(criteriaBuilder.literal(role), root.get(databaseColumn)));
            }
            predicates.add(criteriaBuilder.or(rolePredicates.toArray(Predicate[]::new)));
        }
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    private String ignoreCharacters(String characters, String in) {
        String result = in;
        for (int i = 0; i < characters.length(); i++) {
            result = result.replace("" + characters.charAt(i), "");
        }
        return result;
    }

    private Expression<String> ignoreCharacters(String characters, CriteriaBuilder criteriaBuilder,
                                                Expression<String> inExpression) {
        Expression<String> expression = inExpression;
        for (int i = 0; i < characters.length(); i++) {
            expression = criteriaBuilder.function("replace", String.class, expression,
                    criteriaBuilder.literal(characters.charAt(i)), criteriaBuilder.literal(""));
        }
        return expression;
    }
}
