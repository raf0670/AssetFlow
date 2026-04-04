package com.example.assetflow;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class RegisterController {
    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleRegisterAction(){
        String fullName = fullNameField.getText();
        String user = usernameField.getText();
        String pass = passwordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (fullName.isEmpty() || user.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            errorLabel.setText("All fields are required!");
            return;
        }
        if (!pass.equals(confirmPass)) {
            //errorLabel.setStyle("-fx-text-fill: red;");
            errorLabel.setText("Passwords do not match!");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.csv", true))){
            bw.write(user + "," + pass + "," + fullName);
            bw.newLine();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Scene scene = new Scene(loader.load(), 900, 700);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();

            fullNameField.clear();
            usernameField.clear();
            passwordField.clear();
            confirmPasswordField.clear();

        } catch (IOException e) {
            errorLabel.setText("Error saving user!");
        }
    }

    @FXML
    private void backToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Scene scene = new Scene(loader.load(), 900, 700);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
    }
}
