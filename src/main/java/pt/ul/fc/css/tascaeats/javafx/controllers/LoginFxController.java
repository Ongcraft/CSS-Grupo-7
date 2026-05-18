package pt.ul.fc.css.tascaeats.javafx.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import pt.ul.fc.css.tascaeats.grpc.LoginRequest;
import pt.ul.fc.css.tascaeats.grpc.LoginResponse;
import pt.ul.fc.css.tascaeats.javafx.grpc.GrpcClient;

public class LoginFxController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final GrpcClient grpcClient = new GrpcClient();

    @FXML
    private void handleLogin() {
        try {
            LoginRequest request = LoginRequest.newBuilder()
                .setUsername(usernameField.getText())
                .setPassword(passwordField.getText())
                .build();

            LoginResponse response = grpcClient.userStub.login(request);

            String role = response.getRole();

            if (!role.equals("ADMIN") && !role.equals("COURIER")) {
                messageLabel.setText("Acesso negado: JavaFX é só para admins e entregadores.");
                return;
            }

            messageLabel.setText("Bem-vindo " + response.getName() + " (" + role + ")");
            openDashboard(response.getUserId(), response.getRole());


        } catch (Exception e) {
            messageLabel.setText("Erro no login: backend indisponível ou utilizador inválido.");
            e.printStackTrace();
        }
    }

    private void openDashboard(String userId, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafx/views/dashboard.fxml"));

            Parent root = loader.load();

            DashboardFxController controller = loader.getController();
            controller.initData(userId, role);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("TascaEats - Dashboard");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}