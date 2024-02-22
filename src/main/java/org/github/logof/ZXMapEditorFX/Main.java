package org.github.logof.ZXMapEditorFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.awt.*;
import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MainLayout.fxml")));
            Scene scene = new Scene(root,
                    Toolkit.getDefaultToolkit().getScreenSize().width,
                    Toolkit.getDefaultToolkit().getScreenSize().height);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("application.css"))
                                              .toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("WiTKMapEditor V0.7.8.2018.4.26");
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.png"))));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        MainLayoutController.isRunning = false;
        super.stop();
    }
}
