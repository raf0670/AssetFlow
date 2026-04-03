package com.example.assetflow;

import javafx.beans.property.StringProperty;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;
import javafx.scene.chart.PieChart;

import javax.swing.*;
import java.util.Map;
import java.util.stream.Collectors;

public class HelloController {
    private final Map<String, String> categoryColors = Map.of(
            "Food", "#26de81",
            "Transport", "#45aaf2",
            "Shopping", "#eb3b5b",
            "Bills", "#f7b731",
            "Entertainment", "#a55eea"
    );

    private String currentUser;

    private String getColorFor(String category) {
        return categoryColors.getOrDefault(category, "#778ca3");
    }

    @FXML
    private Button btnBack;
    @FXML
    private Label lblViewTitle;
    private boolean isDashboardMode = true;

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
    @FXML
    private PieChart categoryChart;

    private final ObservableList<Expense> expenseData = FXCollections.observableArrayList();

    @FXML
    private ProgressBar budgetProgress;
    @FXML
    private Label lblBudgetStatus;
    private double monthlyBudget = 0;

    public void setSessionUser(String username) {
        this.currentUser = username;
        this.lblViewTitle.setText("Dashboard for " + username);
        // You can now change your save/load methods to use "expenses_" + username + ".csv"
        loadData();
        showDashboard();
    }

    public void setMonthlyBudget(double monthlyBudget) throws FileNotFoundException {
        this.monthlyBudget = monthlyBudget;
        updateTotal();
        saveBudget();
    }

    @FXML
    private void onSetBudgetClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("budget-view.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        BudgetController controller = loader.getController();
        controller.setMainController(this);

        Stage stage = new Stage();
        stage.setTitle("Set Budget");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() {
        loadData();
        loadBudget();

        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        colCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        colAmount.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        expenseTable.setRowFactory(tv -> {
            TableRow<Expense> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) && isDashboardMode) {
                    Expense clickedRow = row.getItem();
                    showCategoryDetails(clickedRow.categoryProperty().get());
                }
            });
            return row;
        });

        categoryChart.setVisible(true);
        categoryChart.setManaged(true);

        showDashboard();
        updateTotal();

        colCategory.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    javafx.scene.shape.Circle icon = new javafx.scene.shape.Circle(6);
                    icon.setFill(javafx.scene.paint.Color.web(getColorFor(item)));

                    setGraphic(icon);
                    setGraphicTextGap(10);
                    setText(item);
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });
    }

    @FXML
    private void showDashboard() {
        isDashboardMode = true;
        lblViewTitle.setText("Financial Dashboard");
        btnBack.setVisible(false);
        categoryChart.setVisible(true);
        categoryChart.setManaged(true);

        Map<String, Double> summaryMap = expenseData.stream()
                .collect(Collectors.groupingBy(
                        e -> e.categoryProperty().get(),
                        Collectors.summingDouble(e -> e.amountProperty().get())
                ));

        ObservableList<Expense> summaryList = FXCollections.observableArrayList();
        summaryMap.forEach((category, total) -> {
            summaryList.add(new Expense("Double-click to view details", total, category, LocalDate.now()));
        });

        expenseTable.setItems(summaryList);
        colDescription.setText("Action");
        updateChart();
    }

    private void showCategoryDetails(String categoryName) {
        isDashboardMode = false;
        lblViewTitle.setText(categoryName + " Details");
        btnBack.setVisible(true);

        categoryChart.setVisible(false);
        categoryChart.setManaged(false);

        ObservableList<Expense> filtered = expenseData.filtered(e ->
                e.categoryProperty().get().equalsIgnoreCase(categoryName));

        expenseTable.setItems(filtered);
        colDescription.setText("Description");
    }

//    void updateTotal() {
//        double sum = 0;
//        for (Expense e : expenseData) {
//            sum += e.amountProperty().get();
//        }
//        lblTotal.setText(String.format("%.2f", sum));
//    }

    private void saveBudget() throws FileNotFoundException {
        try (java.io.PrintWriter out = new java.io.PrintWriter("budget.txt")) {
            out.println(monthlyBudget);
        } catch (Exception ignored) {

        }
    }

    private void loadBudget() {
        java.io.File file = new java.io.File("budget.txt");
        if (file.exists()) {
            try (java.util.Scanner scanner = new java.util.Scanner(file)) {
                if (scanner.hasNextDouble()) {
                    this.monthlyBudget = scanner.nextDouble();
                }
            } catch (Exception ignored) {

            }
        }
    }

    void updateTotal() {
        double sum = expenseData.stream().mapToDouble(e -> e.amountProperty().get()).sum();
        lblTotal.setText(String.format("BDT %.2f", sum));

        if (monthlyBudget >= 0) {
            double percentage = sum / monthlyBudget;
            budgetProgress.setProgress(percentage);
            lblBudgetStatus.setText(String.format("%.1f%% of BDT %s", percentage * 100, monthlyBudget));

            if (percentage < 0.5) {
                budgetProgress.setStyle("-fx-accent: #20bf6b;");
            } else if (percentage < 0.8) {
                budgetProgress.setStyle("-fx-accent: #f7b731;");
            } else {
                budgetProgress.setStyle("-fx-accent: #eb3b5a;");
            }
        }
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
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.setEditable(true);
        categoryBox.setPromptText("Select or type category");
        categoryBox.getItems().addAll("Food", "Transport", "Rent", "Salary", "Entertainment", "Shopping", "Others");
        TextField amountField = new TextField();
        amountField.setPromptText("Set your amount");

        grid.add(new Label("Description"), 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(new Label("Category"), 0, 1);
        grid.add(categoryBox, 1, 1);
        grid.add(new Label("Amount"), 0, 2);
        grid.add(amountField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String desc = descriptionField.getText();
                String cat = categoryBox.getEditor().getText();
                double amt = Double.parseDouble(amountField.getText());
                return new Expense(desc, amt, cat, LocalDate.now());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(expense -> {
            double currentSum = expenseData.stream().mapToDouble(e -> e.amountProperty().get()).sum();
            double projectedTotal = currentSum + expense.amountProperty().get();
            if (monthlyBudget > 0 && projectedTotal > monthlyBudget){
                Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                a.setTitle("Budget Warning");
                a.setHeaderText("Budget Limit Exceeded!");
                a.setContentText("Adding this expense makes your total BDT " + String.format("%.2f", projectedTotal) +
                        ", which is over your BDT " + monthlyBudget + " budget.\n\nDo you still want to proceed?");
                a.getDialogPane().setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
                a.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                java.util.Optional<ButtonType> result = a.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES){
                    expenseData.add(expense);
                    updateTotal();
                    if (categoryChart.isVisible()) {
                        updateChart();
                    }
                    saveData();
                }
            }
            else{
            expenseData.add(expense);
            updateTotal();
            if (categoryChart.isVisible()) {
                updateChart();
            }
            saveData();
            }
        });

        saveData();    // Save to the user-specific CSV
        updateTotal(); // Update the bottom total label

        // ADD THIS LINE:
        // This forces the app to re-group everything and refresh the pie chart
        showDashboard();

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
        String fileName = (currentUser != null) ? "expenses_" + currentUser + ".csv" : "expenses.csv";

        try (java.io.BufferedWriter w = new java.io.BufferedWriter(new java.io.FileWriter(fileName))) {
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
        String fileName = (currentUser != null) ? "expenses_" + currentUser + ".csv" : "expenses.csv";
        java.io.File f = new java.io.File(fileName);

        if (!f.exists()) return;
        expenseData.clear();

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

    @FXML
    protected void onToggleChart() {
        boolean isShowing = !categoryChart.isVisible();
        categoryChart.setVisible(isShowing);
        categoryChart.setManaged(isShowing);
        if (isShowing) {
            updateChart();
        }
    }

    private void updateChart() {
        categoryChart.getData().clear();

        Map<String, Double> categoryTotals = expenseData.stream()
                .collect(Collectors.groupingBy(
                        expense -> expense.categoryProperty().get(),
                        Collectors.summingDouble(e -> e.amountProperty().get())
                ));

        categoryTotals.forEach((category, total) -> {
            PieChart.Data data = new PieChart.Data(category, total);
            categoryChart.getData().add(data);

            data.getNode().setStyle("-fx-pie-color: " + getColorFor(category) + ";");
        });
    }
}
