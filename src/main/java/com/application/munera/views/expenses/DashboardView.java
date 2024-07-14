package com.application.munera.views.expenses;

import com.application.munera.data.Expense;
import com.application.munera.services.ExpenseService;
import com.application.munera.views.MainLayout;
import com.nimbusds.jose.shaded.gson.Gson;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

//@HtmlImport("frontend://styles/shared-styles.html") // If you have custom styles
@PageTitle("Dashboard")
@Route(value = "highcharts-view", layout = MainLayout.class)
public class DashboardView extends Div {

    private final ExpenseService expenseService;

    public DashboardView(final ExpenseService expenseService) {
        this.expenseService = expenseService;
        addClassName("highcharts-view"); // Optional CSS class for styling

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.getStyle().set("padding", "10px"); // Add padding to main layout

        // Create a horizontal layout for the top row
        HorizontalLayout topRowLayout = new HorizontalLayout();
        topRowLayout.setSizeFull();
        topRowLayout.setHeight("50%"); // Make sure the top row occupies half of the page height
        topRowLayout.getStyle().set("padding", "10px"); // Add padding to top row

        // Create and add the existing bar chart to the top left
        Div barChartDiv = new Div();
        barChartDiv.setId("barChart");
        barChartDiv.getStyle().set("min-height", "100%"); // Ensure it occupies the full height of the container
        barChartDiv.getStyle().set("width", "50%"); // Occupy half of the width
        barChartDiv.getStyle().set("border", "1px solid #ccc"); // Add border
        barChartDiv.getStyle().set("padding", "10px"); // Add padding inside the border
        topRowLayout.add(barChartDiv);

        // Create and add the new pie chart to the top right
        Div pieChartDiv = new Div();
        pieChartDiv.setId("pieChart");
        pieChartDiv.getStyle().set("min-height", "100%"); // Ensure it occupies the full height of the container
        pieChartDiv.getStyle().set("width", "50%"); // Occupy half of the width
        pieChartDiv.getStyle().set("border", "1px solid #ccc"); // Add border
        pieChartDiv.getStyle().set("padding", "10px"); // Add padding inside the border
        topRowLayout.add(pieChartDiv);

        mainLayout.add(topRowLayout);

        // Create a horizontal layout for the bottom row
        HorizontalLayout bottomRowLayout = new HorizontalLayout();
        bottomRowLayout.setSizeFull();
        bottomRowLayout.setHeight("50%"); // Make sure the bottom row occupies the other half of the page height
        bottomRowLayout.getStyle().set("padding", "10px"); // Add padding to bottom row

        // Create placeholder divs for the bottom charts
        Div bottomLeftChartDiv = new Div();
        bottomLeftChartDiv.setId("bottomLeftChart");
        bottomLeftChartDiv.getStyle().set("min-height", "100%"); // Ensure it occupies the full height of the container
        bottomLeftChartDiv.getStyle().set("width", "50%"); // Occupy half of the width
        bottomLeftChartDiv.getStyle().set("border", "1px solid #ccc"); // Add border
        bottomLeftChartDiv.getStyle().set("padding", "10px"); // Add padding inside the border
        bottomRowLayout.add(bottomLeftChartDiv);

        Div bottomRightChartDiv = new Div();
        bottomRightChartDiv.setId("bottomRightChart");
        bottomRightChartDiv.getStyle().set("min-height", "100%"); // Ensure it occupies the full height of the container
        bottomRightChartDiv.getStyle().set("width", "50%"); // Occupy half of the width
        bottomRightChartDiv.getStyle().set("border", "1px solid #ccc"); // Add border
        bottomRightChartDiv.getStyle().set("padding", "10px"); // Add padding inside the border
        bottomRowLayout.add(bottomRightChartDiv);

        mainLayout.add(bottomRowLayout);

        add(mainLayout);

        String barChartJs = generateBarChartScript();
        String pieChartJs = generatePieChartScript();
        String bottomLeftChartJs = generatePlaceholderChartScript("bottomLeftChart", "Bottom Left Chart");
        String bottomRightChartJs = generatePlaceholderChartScript("bottomRightChart", "Bottom Right Chart");

        // Execute the JavaScript to initialize the charts
        getElement().executeJs(barChartJs);
        getElement().executeJs(pieChartJs);
        getElement().executeJs(bottomLeftChartJs);
        getElement().executeJs(bottomRightChartJs);
    }

    private String generateBarChartScript() {
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
        return "Highcharts.chart('barChart', {" +
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

    private String generatePieChartScript() {
        List<Expense> expenses = expenseService.findAllByYear(Year.now().getValue());

        // Group expenses by category name and sum their costs
        Map<String, Double> categoryData = expenses.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.getCategory().getName(),
                        LinkedHashMap::new,
                        Collectors.summingDouble(expense -> expense.getCost().doubleValue())
                ));

        // Prepare series data for Highcharts
        StringBuilder data = new StringBuilder("[");
        for (Map.Entry<String, Double> entry : categoryData.entrySet()) {
            data.append("{ name: '").append(entry.getKey()).append("', y: ").append(entry.getValue()).append(" },");
        }
        data.setCharAt(data.length() - 1, ']'); // Replace last comma with closing bracket

        // Generate JavaScript initialization
        return "Highcharts.chart('pieChart', {" +
                "chart: {" +
                "type: 'pie'" +
                "}," +
                "title: {" +
                "text: 'Expenses by Category for " + Year.now().getValue() + "'" +
                "}," +
                "plotOptions: {" +
                "pie: {" +
                "size: '80%'" + // Adjust size to make the pie chart larger
                "}" +
                "}," +
                "series: [{" +
                "name: 'Expenses'," +
                "colorByPoint: true," +
                "data: " + data + // Use the data fetched from DB
                "}]" +
                "});";
    }

    private String generatePlaceholderChartScript(String chartId, String title) {
        return "Highcharts.chart('" + chartId + "', {" +
                "chart: {" +
                "type: 'line'" + // Placeholder type
                "}," +
                "title: {" +
                "text: '" + title + "'" +
                "}," +
                "xAxis: {" +
                "categories: []" + // Placeholder empty categories
                "}," +
                "series: [{" +
                "name: 'Placeholder'," +
                "data: []" + // Placeholder empty data
                "}]" +
                "});";
    }
}