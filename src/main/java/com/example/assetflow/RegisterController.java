package com.example.assetflow;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class RegisterController {
    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleRegisterAction() {
        String fullName = fullNameField.getText();
        String user = usernameField.getText();
        String pass = passwordField.getText();
        String confirmPass = confirmPasswordField.getText();

        // 1. Basic Validation
        if (fullName.isEmpty() || user.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
            errorLabel.setText("All fields are required!");
            return;
        }
        if (!pass.equals(confirmPass)) {
            errorLabel.setText("Passwords do not match!");
            return;
        }

        // 2. Check for Duplicate Username (Optional but highly recommended)
        if (userExists(user)) {
            errorLabel.setText("Username already exists!");
            return;
        }

        // 3. Save and Transition
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.csv", true))) {
            bw.write(user + "," + pass + "," + fullName);
            bw.newLine();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            VBox root = loader.load();

            // Pass session to controller
            HelloController controller = loader.getController();
            controller.setSessionUser(user);

            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException e) {
            errorLabel.setText("Error saving user!");
        }
    }

    private boolean userExists(String username) {
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("users.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(username)) return true;
            }
        } catch (IOException ignored) {}
        return false;
    }

    @FXML
    private void backToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Scene scene = new Scene(loader.load(), 900, 700);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
    }
}
