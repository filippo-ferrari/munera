package com.application.munera.views.dashboard;

import com.application.munera.data.Expense;
import com.application.munera.data.Person;
import com.application.munera.data.User;
import com.application.munera.facades.PersonFacade;
import com.application.munera.services.ExpenseService;
import com.application.munera.services.UserService;
import com.application.munera.views.MainLayout;
import com.nimbusds.jose.shaded.gson.Gson;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.math.BigDecimal;
import java.time.Month;
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
    private final PersonFacade personFacade;
    private final User loggedUser;
    private final Person loggedPerson;
    private final ComboBox<Integer> yearComboBox;

    public DashboardView(ExpenseService expenseService, UserService userService, PersonFacade personFacade) {
        this.expenseService = expenseService;
        this.personFacade = personFacade;
        loggedUser = userService.getLoggedInUser();
        loggedPerson = personFacade.getLoggedInPerson();
        addClassName("highcharts-view"); // Optional CSS class for styling

        // Fetch available years from the database
        List<Integer> availableYears = this.expenseService.getAvailableExpenseYearsForUser(loggedUser.getId());

        // Initialize the ComboBox for year selection
        yearComboBox = new ComboBox<>("Select Year");
        yearComboBox.setItems(availableYears);
        yearComboBox.setValue(Year.now().getValue()); // Default to current year
        yearComboBox.setWidth("200px");

        // Add listener to update charts when a new year is selected
        yearComboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                updateCharts(Year.of(event.getValue()));
            }
        });

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.add(yearComboBox);
        mainLayout.setSizeFull();
        mainLayout.getStyle().set("padding", "5px"); // Add padding to main layout

        // Create a horizontal layout for the top row
        HorizontalLayout topRowLayout = new HorizontalLayout();
        topRowLayout.setSizeFull();
        topRowLayout.setHeight("50%"); // Make sure the top row occupies half of the page height
        topRowLayout.getStyle().set("padding", "5px"); // Add padding to top row

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
        bottomRowLayout.getStyle().set("padding", "5px"); // Add padding to bottom row

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
        updateCharts(Year.now());
    }

    // Update the charts based on the selected year
    private void updateCharts(Year year) {
        String barChartJs = generateBarChartScript(year);
        String pieChartJs = generatePieChartScript(year);
        String negativeColumnChartJs = generateNegativeColumnChartScript();
        String expensesOverTimeByCategoryChart = generateExpensesOverTimeByCategoryScript(year);

        // Execute the JavaScript to update the charts
        getElement().executeJs(barChartJs);
        getElement().executeJs(pieChartJs);
        getElement().executeJs(negativeColumnChartJs);
        getElement().executeJs(expensesOverTimeByCategoryChart);
    }

    private String generateBarChartScript(Year year) {
        List<Expense> expenses = expenseService.fetchExpensesForDashboard(loggedPerson, year);

        // Create a map to store data by month and category
        Map<String, Map<String, Double>> monthlyCategoryData = new LinkedHashMap<>();

        // Initialize all months (from January to December) for each category
        List<String> monthNames = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String monthName = Month.of(i).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            monthNames.add(monthName);
            monthlyCategoryData.putIfAbsent(monthName, new LinkedHashMap<>());
        }

        // Populate the map with actual expense data
        for (Expense expense : expenses) {
            String monthName = expense.getDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String categoryName = expense.getCategory().getName();
            Double amount = expense.getCost().doubleValue();

            monthlyCategoryData.putIfAbsent(monthName, new LinkedHashMap<>());
            Map<String, Double> categoryData = monthlyCategoryData.get(monthName);
            categoryData.put(categoryName, categoryData.getOrDefault(categoryName, 0.0) + amount);
        }

        // Prepare series data for Highcharts, with each category being a separate series
        Map<String, Map<String, Double>> categoryMonthlyData = new LinkedHashMap<>();
        for (String monthName : monthNames) {
            Map<String, Double> monthData = monthlyCategoryData.getOrDefault(monthName, new LinkedHashMap<>());
            for (Map.Entry<String, Double> entry : monthData.entrySet()) {
                String categoryName = entry.getKey();
                Double amount = entry.getValue();

                categoryMonthlyData.putIfAbsent(categoryName, new LinkedHashMap<>());
                categoryMonthlyData.get(categoryName).put(monthName, amount);
            }
        }

        // Build the series data for each category
        StringBuilder seriesData = new StringBuilder("[");
        for (Map.Entry<String, Map<String, Double>> entry : categoryMonthlyData.entrySet()) {
            String categoryName = entry.getKey();
            Map<String, Double> monthData = entry.getValue();

            seriesData.append("{");
            seriesData.append("name: '").append(categoryName).append("',");
            seriesData.append("data: [");

            for (String monthName : monthNames) {
                seriesData.append(monthData.getOrDefault(monthName, 0.0)).append(",");
            }

            seriesData.setLength(seriesData.length() - 1); // Remove trailing comma
            seriesData.append("], stack: 'expenses'");
            seriesData.append("},");
        }
        seriesData.setLength(seriesData.length() - 1); // Remove trailing comma
        seriesData.append("]");

        // Generate the JavaScript for the stacked column chart
        return "Highcharts.chart('barChart', {" +
                "chart: { type: 'column' }, " +
                "title: { text: 'Monthly Expenses by Category for " + Year.now().getValue() + "' }, " +
                "xAxis: { categories: " + new Gson().toJson(monthNames) + " }, " +
                "yAxis: { " +
                "min: 0, " +
                "title: { text: 'Total Expense' }, " +
                "stackLabels: { " +
                "enabled: true, " +
                "style: { fontWeight: 'bold', fontSize: '14px', color: 'white', textOutline: '1px contrast', " +
                "backgroundColor: 'rgba(0,0,0,0.75)', padding: 4, borderRadius: 3 }, " + // Background and border for better visibility
                "formatter: function() { " +
                "   if (this.total > 0) { return this.total; } else { return ''; }" +  // Only show total if greater than 0
                "}" +
                "} " +
                "}, " +
                "plotOptions: { " +
                "column: { " +
                "stacking: 'normal', " +
                "dataLabels: { " +
                "enabled: true, " +
                "formatter: function() { " +
                "   if (this.y > 0) { return this.y; } else { return ''; }" +  // Only show data label if value > 0
                "}," +
                "color: 'black', " + // Set data label color to black
                "style: { fontSize: '12px', fontWeight: 'bold' }" +  // Customize individual labels' appearance
                "} " +
                "} " +
                "}, " +
                "series: " + seriesData.toString() + " " +
                "});";
    }

    private String generatePieChartScript(Year year) {
        List<Expense> expenses = expenseService.fetchExpensesForDashboard(loggedPerson, year);

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
        final var people = personFacade.findAllExcludeLoggedUser(loggedUser);
        // Create a map to store person names and their balances
        Map<String, Double> personData = people.stream()
                .map(person -> {
                    BigDecimal balance = personFacade.calculateNetBalance(person);
                    // Return an array with the person’s first name and the balance
                    return new Object[]{person.getFirstName(), balance};
                })
                .filter(entry -> ((BigDecimal) entry[1]).compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toMap(
                        entry -> (String) entry[0],
                        entry -> ((BigDecimal) entry[1]).negate().doubleValue(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new // Preserves insertion order
                ));
        if (personData.isEmpty()) return generatePlaceholderChartScript("bottomLeftChart", "All Payments Settled");

        // Prepare series data for Highcharts with conditional coloring
        StringBuilder data = new StringBuilder("[");
        for (Map.Entry<String, Double> entry : personData.entrySet()) {
            double value = entry.getValue();
            String color = value >= 0 ? "#90EE90" : "#FF9999"; // Green for positive, red for negative
            data.append("{ y: ").append(value).append(", color: '").append(color).append("' },");
        }
        data.setCharAt(data.length() - 1, ']'); // Replace the last comma with a closing bracket

        // Generate JavaScript initialization
        return "Highcharts.chart('bottomLeftChart', {" +
                "chart: {" +
                "type: 'column'," +
                "}," +
                "title: {" +
                "text: 'Net Balances by Person'" +
                "}," +
                "xAxis: {" +
                "categories: " + new Gson().toJson(personData.keySet()) +
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
                "plotOptions: {" +
                "column: {" +
                "pointWidth: 50," +
                "threshold: 0" +
                "}" +
                "}," +
                "tooltip: {" + // Combine default point format with a custom label
                "useHTML: true," +
                "formatter: function() {" +
                "var label = this.y >= 0 ? 'Credit: ' : 'Debit: '; " +
                "return '<span style=\"color:' + this.point.color + '\">\u25CF</span> ' + '<b>' + this.x + '</b><br/>' + label + Math.abs(this.y);" +
                "}" +
                "}," +
                "series: [{" +
                "name: 'Balance'," +
                "data: " + data +
                "}]" +
                "});";
    }

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

    private String generateExpensesOverTimeByCategoryScript(Year year) {
        List<Expense> expenses = expenseService.fetchExpensesForDashboard(loggedPerson, year);

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