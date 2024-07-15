package com.application.munera.views.dashboard;

import com.application.munera.data.Expense;
import com.application.munera.data.Person;
import com.application.munera.services.ExpenseService;
import com.application.munera.services.PersonService;
import com.application.munera.views.MainLayout;
import com.nimbusds.jose.shaded.gson.Gson;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.math.BigDecimal;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

//@HtmlImport("frontend://styles/shared-styles.html") // If you have custom styles
@PermitAll
@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
public class DashboardView extends Div {

    private final ExpenseService expenseService;
    private final PersonService personService;

    public DashboardView(final ExpenseService expenseService, final PersonService personService) {
        this.expenseService = expenseService;
        this.personService = personService;
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

        // Create the bottom left chart
        Div bottomLeftChartDiv = new Div();
        bottomLeftChartDiv.setId("bottomLeftChart");
        bottomLeftChartDiv.getStyle().set("min-height", "100%"); // Ensure it occupies the full height of the container
        bottomLeftChartDiv.getStyle().set("width", "50%"); // Occupy half of the width
        bottomLeftChartDiv.getStyle().set("border", "1px solid #ccc"); // Add border
        bottomLeftChartDiv.getStyle().set("padding", "10px"); // Add padding inside the border
        bottomRowLayout.add(bottomLeftChartDiv);

        // Placeholder for the bottom right chart
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
        String bottomLeftChartJs = generateNegativeColumnChartScript();
        String bottomRightChartJs = generateExpensesOverTimeByCategoryScript();

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

    private String generateNegativeColumnChartScript() {
        final var people = personService.findAll().stream()
                .filter(person -> personService.calculateNetBalance(person).compareTo(BigDecimal.ZERO) != 0)
                .toList();
        if (people.isEmpty()) return generatePlaceholderChartScript("bottomLeftChart", "No Data Available");

        Map<String, Double> personData = new LinkedHashMap<>();

        for (Person person : people) {
            BigDecimal balance = personService.calculateNetBalance(person);
            personData.put(person.getFirstName(), balance.doubleValue());
        }

        // Prepare series data for Highcharts with conditional coloring
        StringBuilder data = new StringBuilder("[");
        for (Map.Entry<String, Double> entry : personData.entrySet()) {
            double value = entry.getValue();
            String color = value >= 0 ? "#90EE90" : "#FF9999"; // Green for positive, red for negative
            data.append("{ y: ").append(value).append(", color: '").append(color).append("' },");
        }
        data.setCharAt(data.length() - 1, ']'); // Replace last comma with closing bracket

        // Generate JavaScript initialization
        return "Highcharts.chart('bottomLeftChart', {" +
                "chart: {" +
                "type: 'column'," + // Specify the chart type as column
                "}," +
                "title: {" +
                "text: 'Net Balances by Person'" +
                "}," +
                "xAxis: {" +
                "categories: " + new Gson().toJson(personData.keySet()) + // Categories are the person names
                "}," +
                "yAxis: {" +
                "title: {" +
                "text: 'Balance'" +
                "}," +
                "plotLines: [{" +
                "value: 0," +
                "width: 1," +
                "color: '#808080'" +
                "}]" +
                "}," +
                "plotOptions: {" + // Add plotOptions to configure the column width
                "column: {" +
                "pointWidth: 50" + // Adjust the width of the columns (in pixels)
                "}" +
                "}," +
                "series: [{" +
                "name: 'Balance'," +
                "data: " + data + // Use the data fetched from DB
                "}]" +
                "});";    }

    private String generatePlaceholderChartScript(String divId, String title) {
        return "Highcharts.chart('" + divId + "', {" +
                "chart: {" +
                "type: 'column'" +
                "}," +
                "title: {" +
                "text: '" + title + "'" +
                "}," +
                "series: [{" +
                "name: 'Data'," +
                "data: [0]" + // Placeholder data
                "}]" +
                "});";
    }

    private String generateExpensesOverTimeByCategoryScript() {
        List<Expense> expenses = expenseService.findAllByYear(Year.now().getValue());

        // Group expenses by category and by month
        Map<String, Map<String, Double>> categoryMonthlyData = new LinkedHashMap<>();

        YearMonth currentYearMonth = YearMonth.now().withMonth(1); // Start from January
        List<String> monthNames = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            String monthName = currentYearMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            monthNames.add(monthName);
            currentYearMonth = currentYearMonth.plusMonths(1); // Move to the next month
        }

        for (Expense expense : expenses) {
            String categoryName = expense.getCategory().getName();
            String monthName = expense.getDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            Double amount = expense.getCost().doubleValue();

            categoryMonthlyData.putIfAbsent(categoryName, new LinkedHashMap<>());
            Map<String, Double> monthlyData = categoryMonthlyData.get(categoryName);

            // Initialize all months to 0 for each category
            for (String month : monthNames) {
                monthlyData.putIfAbsent(month, 0.0);
            }

            monthlyData.put(monthName, monthlyData.get(monthName) + amount);
        }

        // Prepare series data for Highcharts
        StringBuilder seriesData = new StringBuilder("[");
        for (Map.Entry<String, Map<String, Double>> entry : categoryMonthlyData.entrySet()) {
            String categoryName = entry.getKey();
            Map<String, Double> monthlyData = entry.getValue();

            seriesData.append("{");
            seriesData.append("name: '").append(categoryName).append("',");
            seriesData.append("data: ").append(monthlyData.values());
            seriesData.append("},");
        }
        seriesData.setCharAt(seriesData.length() - 1, ']'); // Replace last comma with closing bracket

        // Generate JavaScript initialization
        return "Highcharts.chart('bottomRightChart', {" +
                "chart: {" +
                "type: 'line'" +
                "}," +
                "title: {" +
                "text: 'Expenses Over Time by Category for " + Year.now().getValue() + "'" +
                "}," +
                "xAxis: {" +
                "categories: " + new Gson().toJson(monthNames) +
                "}," +
                "yAxis: {" +
                "title: {" +
                "text: 'Amount'" +
                "}" +
                "}," +
                "series: " + seriesData +
                "});";
    }
}