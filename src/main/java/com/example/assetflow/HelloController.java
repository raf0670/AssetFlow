package com.example.assetflow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private TableView<Expense> expenseTable;
    @FXML
    private TableColumn<Expense, String> colDescription;
    @FXML
    private TableColumn<Expense, String> colCategory;
    @FXML
    private TableColumn<Expense, Double> colAmount;
    @FXML
    private Label lblTotal;

    private final ObservableList<Expense> expenseData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        colCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        colAmount.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        expenseTable.setItems(expenseData);

        expenseData.add(new Expense("Starbucks Coffee", 5.50, "Food"));
        expenseData.add(new Expense("Groceries", 42.10, "Food"));
        expenseData.add(new Expense("Netflix Subscription", 15.99, "Entertainment"));

        updateTotal();
    }

    void updateTotal() {
        double sum = 0;
        for (Expense e : expenseData) {
            sum += e.amountProperty().get();
        }
        lblTotal.setText(String.format("%.2f", sum));
    }

    @FXML
    protected void onAddButtonClick() {
        expenseData.add(new Expense("New Item", 10.00, "Mic"));
        updateTotal();
    }
}
