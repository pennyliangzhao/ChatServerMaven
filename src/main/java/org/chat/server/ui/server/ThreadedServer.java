package org.chat.server.ui.server;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class ThreadedServer {
    private Label label;
    private ServerSocket listener;
    private TextArea textArea;
    private Socket socket;
    private static HashMap<String, Socket> clientsMap = new HashMap<>();


    public ThreadedServer(TextArea textArea, Label label, ServerSocket listener) {
        this.label = label;
        this.listener = listener;
        this.textArea = textArea;
    }

    public void listen() throws IOException {
        updateLabel();

        while (true) {
            socket = listener.accept();
            textArea.appendText("Client: "+socket+"\n");
            new ServerThread(socket, textArea).start();
        }
    }


    public void updateLabel() {
        Platform.runLater(() -> label.setText("Server started on: " + listener.getLocalPort()));
    }

    public static void addClient(String username, Socket clientSocket) {
        clientsMap.put(username, clientSocket);
    }

    public static Socket getClient(String username) {
        return clientsMap.get(username);
    }

    public static void removeClient(String username) {
        clientsMap.remove(username);
    }


}
