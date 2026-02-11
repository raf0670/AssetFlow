package com.example.assetflow;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class HistoryController {
    @FXML private TableView<Expense> historyTable;
    @FXML private TableColumn<Expense, LocalDate> colDate;
    @FXML private TableColumn<Expense, String> colDesc;
    @FXML private TableColumn<Expense, String> colCat;
    @FXML private TableColumn<Expense, Number> colAmt;

    @FXML private DatePicker filterDate;

    private FilteredList<Expense> filteredData;

    @FXML
    public void initialize() {
        colDate.setCellValueFactory(cell -> cell.getValue().dateProperty());
        colDesc.setCellValueFactory(cell -> cell.getValue().descriptionProperty());
        colCat.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        colAmt.setCellValueFactory(cell -> cell.getValue().amountProperty());

        filterDate.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (filteredData == null) return;

            filteredData.setPredicate(expense -> {
                if (newDate == null) {
                    return true;
                }
                LocalDate expenseDate = expense.dateProperty().get();
                return !expenseDate.isBefore(newDate);
            });
        });
    }

    public void setExpenseData(ObservableList<Expense> data) {
        this.filteredData = new FilteredList<>(data, p -> true);
        SortedList<Expense> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(historyTable.comparatorProperty());
        historyTable.setItems(sortedData);

        historyTable.getSortOrder().add(colDate);
        colDate.setSortType(TableColumn.SortType.DESCENDING);
        historyTable.sort();
    }

    @FXML
    private void clearFilter() {
        filterDate.setValue(null);
    }
}
