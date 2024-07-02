package com.application.munera.views.expenses;

import com.application.munera.data.Expense;
import com.application.munera.services.ExpenseService;
import com.application.munera.views.MainLayout;
import com.nimbusds.jose.shaded.gson.Gson;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//@HtmlImport("frontend://styles/shared-styles.html") // If you have custom styles
@Route(value = "highcharts-view", layout = MainLayout.class)
public class DashboardView extends Div {

    private final ExpenseService expenseService;

    public DashboardView(final ExpenseService expenseService) {
        this.expenseService = expenseService;
        addClassName("highcharts-view"); // Optional CSS class for styling

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        // Create a div to host the chart
        Div chartDiv = new Div();
        chartDiv.setId("chart"); // Assign an ID to this div for later reference
        chartDiv.getStyle().set("min-height", "400px"); // Set minimum height for the chart
        layout.add(chartDiv);
        add(layout);

        String jsInit = generateChartInitializationScript();

        // Execute the JavaScript to initialize the chart
        getElement().executeJs(jsInit);
    }

    private String generateChartInitializationScript() {
        List<Expense> expenses = expenseService.findAllByYear(Year.now().getValue());

        // Prepare data for Highcharts
        Map<String, Double> monthlyData = new LinkedHashMap<>();
        YearMonth currentYearMonth = YearMonth.now().withMonth(1); // Start from January

        for (int i = 1; i <= 12; i++) {
            String monthName = currentYearMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            monthlyData.put(monthName, 0.0);
            currentYearMonth = currentYearMonth.plusMonths(1); // Move to the next month
        }

        // Populate map with actual data
        for (Expense expense : expenses) {
            String monthName = expense.getDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            // Convert BigDecimal to Double
            Double amount = expense.getCost().doubleValue();
            monthlyData.put(monthName, monthlyData.get(monthName) + amount);
        }

        // Prepare series data for Highcharts
        StringBuilder data = new StringBuilder("[");
        for (Map.Entry<String, Double> entry : monthlyData.entrySet()) {
            data.append(entry.getValue()).append(",");
        }
        data.setCharAt(data.length() - 1, ']'); // Replace last comma with closing bracket

        // Generate JavaScript initialization
        return "Highcharts.chart('chart', {" +
                "chart: {" +
                "type: 'column'" +
                "}," +
                "title: {" +
                "text: 'Monthly Expenses for " + Year.now().getValue() + "'" +
                "}," +
                "xAxis: {" +
                "categories: " + new Gson().toJson(monthlyData.keySet()) + // Categories are the month names
                "}," +
                "series: [{" +
                "name: 'Expenses'," +
                "data: " + data + // Use the data fetched from DB
                "}]" +
                "});";
    }
}