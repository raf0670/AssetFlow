package com.example.assetflow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

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
        loadData();
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        colCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        colAmount.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        expenseTable.setItems(expenseData);

//        expenseData.add(new Expense("Starbucks Coffee", 5.50, "Food", LocalDate.of(2026, 2, 5)));
//        expenseData.add(new Expense("Groceries", 42.10, "Food", LocalDate.of(2026, 1, 29)));
//        expenseData.add(new Expense("Netflix Subscription", 15.99, "Entertainment", LocalDate.of(2025, 12, 28)));

        updateTotal();

        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete Expense");
        deleteItem.setOnAction(event -> {
            Expense selected = expenseTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                expenseData.remove(selected);
                updateTotal();
                saveData();
            }
        });
        contextMenu.getItems().add(deleteItem);
        expenseTable.setContextMenu(contextMenu);
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
        Dialog<Expense> dialog = new Dialog<>();
        dialog.setTitle("Add Expense");
        dialog.setHeaderText("Add expense details");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        dialogPane.getStyleClass().add("my-custom-dialog");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Write your item name");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Input your category");
        TextField amountField = new TextField();
        amountField.setPromptText("Set your amount");

        grid.add(new Label("Description"), 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(new Label("Category"), 0, 1);
        grid.add(categoryField, 1, 1);
        grid.add(new Label("amount"), 0, 2);
        grid.add(amountField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String desc = descriptionField.getText();
                String cat = categoryField.getText();
                double amt = Double.parseDouble(amountField.getText());
                return new Expense(desc, amt, cat, LocalDate.now());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(expense -> {
            expenseData.add(expense);
            updateTotal();
        });

        saveData();
    }

    @FXML
    protected void onHistoryButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("history-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 500);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
            HistoryController controller = fxmlLoader.getController();
            controller.setExpenseData(expenseData);

            Stage stage = new Stage();
            stage.setTitle("Expense History");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try (java.io.BufferedWriter w = new java.io.BufferedWriter(new java.io.FileWriter("expenses.csv"))) {
            for (Expense expense : expenseData) {
                String line = String.format("%s,%s,%s,%.2f",
                        expense.dateProperty().get(),
                        expense.descriptionProperty().get(),
                        expense.categoryProperty().get(),
                        expense.amountProperty().get());
                w.write(line);
                w.newLine();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        java.io.File f = new java.io.File("expenses.csv");
        if (!f.exists()) return;

        try (java.io.BufferedReader r = new java.io.BufferedReader(new java.io.FileReader(f))) {
            String l;
            while ((l = r.readLine()) != null) {
                String[] parts = l.split(",");
                if (parts.length == 4) {
                    LocalDate date = LocalDate.parse(parts[0]);
                    String desc = parts[1];
                    String cat = parts[2];
                    double amt = Double.parseDouble(parts[3]);

                    expenseData.add(new Expense(desc, amt, cat, date));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
