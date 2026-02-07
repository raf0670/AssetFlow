package com.example.assetflow;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
                return expense.dateProperty().get().equals(newDate);
            });
        });
    }

    public void setExpenseData(ObservableList<Expense> data) {
        this.filteredData = new FilteredList<>(data, p -> true);
        historyTable.setItems(filteredData);
    }

    @FXML
    private void clearFilter() {
        filterDate.setValue(null);
    }
}
