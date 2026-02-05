package com.example.assetflow;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Expense {
    private final StringProperty description = new SimpleStringProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();
    private final StringProperty category = new SimpleStringProperty();

    public Expense(String description, double amount, String category) {
        this.description.set(description);
        this.amount.set(amount);
        this.category.set(category);
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
}
