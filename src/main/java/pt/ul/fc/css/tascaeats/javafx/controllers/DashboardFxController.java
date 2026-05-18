package pt.ul.fc.css.tascaeats.javafx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import pt.ul.fc.css.tascaeats.javafx.grpc.GrpcClient;
import pt.ul.fc.css.tascaeats.grpc.*;

public class DashboardFxController {

    @FXML
    private Label titleLabel;

    @FXML
    private VBox contentBox;

    private String userId;
    private String role;

    private final GrpcClient grpcClient = new GrpcClient();

    public void initData(String userId, String role) {
        this.userId = userId;
        this.role = role;

        if ("ADMIN".equals(role)) {
            loadAdminView();
        } else if ("COURIER".equals(role)) {
            loadCourierView();
        }
    }

    // ================= ADMIN =================

    private void loadAdminView() {
        titleLabel.setText("Dashboard Admin");
        contentBox.getChildren().clear();

        Button restaurantsBtn = new Button("Gerir Restaurantes");
        Button productsBtn = new Button("Gerir Produtos");
        Button usersBtn = new Button("Gerir Users");

        restaurantsBtn.setOnAction(e -> showAdminRestaurants());
        productsBtn.setOnAction(e -> showAdminProducts());
        usersBtn.setOnAction(e -> showAdminUsers());

        contentBox.getChildren().addAll(restaurantsBtn, productsBtn, usersBtn);
    }

    // ================= USERS =================

    private void showAdminUsers() {
        titleLabel.setText("Gerir Users");
        contentBox.getChildren().clear();

        Button createAdminBtn = new Button("Registar Admin");
        Button createCourierBtn = new Button("Registar Courier");

        createAdminBtn.setOnAction(e -> showCreateAdminForm());
        createCourierBtn.setOnAction(e -> showCreateCourierForm());

        contentBox.getChildren().addAll(createAdminBtn, createCourierBtn);

        try {
            var response = grpcClient.userStub.getUsersByFilter(
                    UserFilterRequest.newBuilder().build()
            );

            for (var u : response.getUsersList()) {
                Label label = new Label(u.getName() + " | " + u.getUsername() + " | " + u.getRole());

                Button removeBtn = new Button("Remover");
                removeBtn.setOnAction(e -> {
                    grpcClient.userStub.removeUser(
                            UserIdRequest.newBuilder().setUserId(u.getId()).build()
                    );
                    showAdminUsers();
                });

                contentBox.getChildren().addAll(label, removeBtn);
            }

        } catch (Exception e) {
            e.printStackTrace();
            contentBox.getChildren().add(new Label("Erro ao carregar users."));
        }

        addBackButtonAtBottom();
    }

    private void showCreateAdminForm() {
        titleLabel.setText("Registar Admin");
        contentBox.getChildren().clear();

        TextField nameField = new TextField();
        nameField.setPromptText("Nome");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button submitBtn = new Button("Criar Admin");

        submitBtn.setOnAction(e -> {
            try {
                grpcClient.userStub.createAdmin(
                        CreateAdminRequest.newBuilder()
                                .setName(nameField.getText())
                                .setUsername(usernameField.getText())
                                .setPassword(passwordField.getText())
                                .build()
                );
                showAdminUsers();
            } catch (Exception ex) {
                ex.printStackTrace();
                contentBox.getChildren().add(new Label("Erro ao criar admin."));
            }
        });

        contentBox.getChildren().addAll(nameField, usernameField, passwordField, submitBtn);
        addBackButtonAtBottom();
    }

    private void showCreateCourierForm() {
        titleLabel.setText("Registar Courier");
        contentBox.getChildren().clear();

        TextField nameField = new TextField();
        nameField.setPromptText("Nome");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button submitBtn = new Button("Criar Courier");

        submitBtn.setOnAction(e -> {
            try {
                grpcClient.userStub.createCourier(
                        CreateCourierRequest.newBuilder()
                                .setName(nameField.getText())
                                .setUsername(usernameField.getText())
                                .setPassword(passwordField.getText())
                                .build()
                );
                showAdminUsers();
            } catch (Exception ex) {
                ex.printStackTrace();
                contentBox.getChildren().add(new Label("Erro ao criar courier."));
            }
        });

        contentBox.getChildren().addAll(nameField, usernameField, passwordField, submitBtn);
        addBackButtonAtBottom();
    }

    // ================= RESTAURANTS =================

    private void showAdminRestaurants() {
        titleLabel.setText("Gerir Restaurantes");
        contentBox.getChildren().clear();

        try {
            var response = grpcClient.restaurantStub.getRestaurantsByFilter(
                    RestaurantFilterRequest.newBuilder().build()
            );

            for (var r : response.getRestaurantsList()) {
                Label label = new Label(
                        r.getName() + " | " + (r.getOpen() ? "Aberto" : "Fechado")
                );

                Button openBtn = new Button("Abrir");
                Button closeBtn = new Button("Fechar");
                Button menuBtn = new Button("Ver Menu");

                openBtn.setOnAction(e -> {
                    grpcClient.restaurantStub.openRestaurant(
                            RestaurantIdRequest.newBuilder()
                                    .setRestaurantId(r.getId())
                                    .build()
                    );
                    showAdminRestaurants();
                });

                closeBtn.setOnAction(e -> {
                    grpcClient.restaurantStub.closeRestaurant(
                            RestaurantIdRequest.newBuilder()
                                    .setRestaurantId(r.getId())
                                    .build()
                    );
                    showAdminRestaurants();
                });

                menuBtn.setOnAction(e -> {
                    if (r.getMenuId() == null || r.getMenuId().isBlank()) {
                        contentBox.getChildren().add(new Label("Este restaurante ainda não tem menu."));
                    } else {
                        showRestaurantMenu(r.getMenuId(), r.getName());
                    }
                });

                contentBox.getChildren().addAll(label, openBtn, closeBtn, menuBtn);
            }

        } catch (Exception e) {
            e.printStackTrace();
            contentBox.getChildren().add(new Label("Erro ao carregar restaurantes."));
        }

        addBackButtonAtBottom();
    }

    private void showRestaurantMenu(String menuId, String restaurantName) {
        titleLabel.setText("Menu de " + restaurantName);
        contentBox.getChildren().clear();

        try {
            var menu = grpcClient.menuStub.getMenuById(
                    MenuIdRequest.newBuilder().setMenuId(menuId).build()
            );

            contentBox.getChildren().add(new Label("Menu: " + menu.getName()));

            for (var p : menu.getProductsList()) {
                contentBox.getChildren().add(
                        new Label(p.getName() + " | " + p.getPrice() + "€ | " + p.getCategory())
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            contentBox.getChildren().add(new Label("Erro ao carregar menu."));
        }

        addBackButtonAtBottom();
    }

    // ================= PRODUCTS =================

    private void showAdminProducts() {
        titleLabel.setText("Gerir Produtos");
        contentBox.getChildren().clear();

        Button createProductBtn = new Button("Criar Produto");
        createProductBtn.setOnAction(e -> showCreateProductForm());

        contentBox.getChildren().add(createProductBtn);

        try {
            var response = grpcClient.productStub.getProductsByFilter(
                    ProductFilterRequest.newBuilder().build()
            );

            for (var p : response.getProductsList()) {
                Label label = new Label(
                        p.getName() + " | " + p.getPrice() + "€ | " + p.getCategory()
                );

                contentBox.getChildren().add(label);
            }

        } catch (Exception e) {
            e.printStackTrace();
            contentBox.getChildren().add(new Label("Erro ao carregar produtos."));
        }

        addBackButtonAtBottom();
    }

    private void showCreateProductForm() {
        titleLabel.setText("Criar Produto");
        contentBox.getChildren().clear();

        TextField nameField = new TextField();
        nameField.setPromptText("Nome");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Descrição");

        TextField priceField = new TextField();
        priceField.setPromptText("Preço");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Categoria ex: MAIN_COURSE");

        CheckBox availableBox = new CheckBox("Disponível");
        availableBox.setSelected(true);

        Button submitBtn = new Button("Criar Produto");

        submitBtn.setOnAction(e -> {
            try {
                grpcClient.productStub.createProduct(
                        CreateProductRequest.newBuilder()
                                .setName(nameField.getText())
                                .setDescription(descriptionField.getText())
                                .setPrice(Double.parseDouble(priceField.getText()))
                                .setCategory(categoryField.getText())
                                .setAvailable(availableBox.isSelected())
                                .build()
                );

                showAdminProducts();

            } catch (Exception ex) {
                ex.printStackTrace();
                contentBox.getChildren().add(new Label("Erro ao criar produto."));
            }
        });

        contentBox.getChildren().addAll(
                nameField,
                descriptionField,
                priceField,
                categoryField,
                availableBox,
                submitBtn
        );

        addBackButtonAtBottom();
    }

    // ================= COURIER =================

    private void loadCourierView() {
        titleLabel.setText("Dashboard Courier");
        contentBox.getChildren().clear();

        Button ordersBtn = new Button("Ver Pedidos");

        ordersBtn.setOnAction(e -> showCourierOrders());

        contentBox.getChildren().add(ordersBtn);
    }

    private void showCourierOrders() {
        titleLabel.setText("Pedidos Courier");
        contentBox.getChildren().clear();

        contentBox.getChildren().add(
                new Label("Aqui ficam os pedidos atribuídos/entregas do courier.")
        );

        addBackButtonAtBottom();
    }

    // ================= SHARED =================

    private Button backButton() {
        Button backBtn = new Button("Voltar");

        backBtn.setOnAction(e -> {
            if ("ADMIN".equals(role)) {
                loadAdminView();
            } else if ("COURIER".equals(role)) {
                loadCourierView();
            }
        });

        return backBtn;
    }

    private void addBackButtonAtBottom() {
        contentBox.getChildren().add(backButton());
    }
}