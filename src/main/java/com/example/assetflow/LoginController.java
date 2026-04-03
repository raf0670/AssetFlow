package com.example.assetflow;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.Objects;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private static final String USER_FILE = "users.csv";

    @FXML
    private void handleLogin() throws IOException {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (validateLogin(user, pass)) {
            // Success! Load the main dashboard
            switchToMain(user);
        } else {
            errorLabel.setText("Invalid username or password!");
        }
    }

    @FXML
    private void handleRegister() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Fields cannot be empty!");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            bw.write(user + "," + pass);
            bw.newLine();
            errorLabel.setText("Registration successful! Please login.");
        } catch (IOException e) {
            errorLabel.setText("Error saving user.");
        }
    }

    private boolean validateLogin(String username, String password) {
        File file = new File(USER_FILE);
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return false;
    }

    private void switchToMain(String username) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Scene scene = new Scene(loader.load(), 400, 500);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

        // Pass the username to the main controller so we can load THEIR specific data
        HelloController controller = loader.getController();
        controller.setSessionUser(username);

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
    }
}