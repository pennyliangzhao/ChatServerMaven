package org.chat.server.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainUI extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(MainUI.class.getResource("main_ui.fxml"));
        stage.setTitle("ServerApp");
        stage.setScene(new Scene(parent, 600, 500));
        stage.show();
    }
}
