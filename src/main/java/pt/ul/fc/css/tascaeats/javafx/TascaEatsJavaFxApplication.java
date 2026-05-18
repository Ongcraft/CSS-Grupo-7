package pt.ul.fc.css.tascaeats.javafx;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TascaEatsJavaFxApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        URL loginFxml = TascaEatsJavaFxApplication.class.getResource("/javafx/login.fxml");

        if (loginFxml == null) {
            throw new IllegalStateException("Ficheiro não encontrado: src/main/resources/javafx/login.fxml");
        }

        FXMLLoader loader = new FXMLLoader(loginFxml);
        Scene scene = new Scene(loader.load(), 400, 300);

        stage.setTitle("TascaEats");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}