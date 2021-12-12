package org.chat.server.ui;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.chat.server.ui.server.ThreadedServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class Controller {

    @FXML
    public TextArea textArea;
    @FXML
    public TextField portNumber;
    @FXML
    public Label statusLabel;
    @FXML
    public Label ip;

    private ServerSocket listener;

    Service service = new Service() {
        @Override
        protected Task<Void> createTask() {
            System.out.println(portNumber.getText());
            return new Task<>() {
                @Override
                protected Void call() throws Exception {
                    int portNo = Integer.parseInt(portNumber.getText());
                    if (portNo <= 65535 && portNo >= 0) {
                        listener = new ServerSocket(Integer.parseInt(portNumber.getText()));
                        ThreadedServer threadedServer = new ThreadedServer(textArea,statusLabel, Controller.this.listener);
                        threadedServer.listen();

                    } else {
                        Platform.runLater(() -> statusLabel.setText("invalid number"));
                    }
                    return null;
                }
            };

        }
    };

    @FXML
    public void stopServer(MouseEvent mouseEvent) {
        try {
            service.cancel();
            listener.close();
            System.out.println("Server stopped");
            Platform.runLater(() -> statusLabel.setText("Server stopped"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void startServer(MouseEvent mouseEvent) throws UnknownHostException {
        if (service.isRunning() || service.getState() == Worker.State.FAILED) {
            service.cancel();
            service.reset();
            ip = getIpAddressView();

        }
        ip = getIpAddressView();
        service.start();


    }
    public Label getIpAddressView()throws UnknownHostException {
        ip.setText(InetAddress.getLocalHost().getHostAddress());
        return ip;

    }
}
