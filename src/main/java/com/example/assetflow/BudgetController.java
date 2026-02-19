package com.example.assetflow;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileNotFoundException;

public class BudgetController {
    @FXML
    private TextField budgetField;
    private HelloController mainController;

    public void setMainController(HelloController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleSave() throws FileNotFoundException {
        double amount = Double.parseDouble(budgetField.getText());
        mainController.setMonthlyBudget(amount);
        ((Stage) budgetField.getScene().getWindow()).close();
    }
}
