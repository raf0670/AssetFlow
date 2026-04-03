package com.example.assetflow.network;

import javafx.application.Platform;
import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatService {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Consumer<String> onMessageReceived;

    public void connect(String host, int port, Consumer<String> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;

        // Use a Thread to connect and listen so the UI doesn't hang
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null) {
                    final String msg = message;
                    // Platform.runLater is CRITICAL to update JavaFX from a background thread
                    Platform.runLater(() -> onMessageReceived.accept(msg));
                }
            } catch (IOException e) {
                Platform.runLater(() -> onMessageReceived.accept("System: Could not connect to server."));
            }
        }).start();
    }

    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
}