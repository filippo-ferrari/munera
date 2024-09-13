package com.application.munera.services;

import com.application.munera.data.Expense;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class CSVService {

    public StreamResource createCSVResource(List<Expense> expenses) {
        return new StreamResource("expenses.csv", () -> {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try (OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Name", "Cost", "Category", "Date", "Payment date"))) {

                for (Expense expense : expenses) {
                    csvPrinter.printRecord(
                            expense.getName(),
                            expense.getCost(),
                            expense.getCategory() != null ? expense.getCategory().getName() : "",
                            expense.getDate(),
                            expense.getPaymentDate() != null ? expense.getPaymentDate() : "Unpaid"
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new ByteArrayInputStream(stream.toByteArray());
        });
    }
}
