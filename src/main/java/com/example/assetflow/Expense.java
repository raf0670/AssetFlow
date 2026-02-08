package com.example.assetflow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDate;

public class Expense {
    private final StringProperty description = new SimpleStringProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

    public Expense(String description, double amount, String category, LocalDate date) {
        this.description.set(description);
        this.amount.set(amount);
        this.category.set(category);
        this.date.set(date);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public DoubleProperty amountProperty() {
        return amount;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public ObjectProperty<LocalDate> dateProperty() { return date; }
}
