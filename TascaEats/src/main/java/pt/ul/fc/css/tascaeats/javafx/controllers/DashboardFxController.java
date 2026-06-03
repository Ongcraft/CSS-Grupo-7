package pt.ul.fc.css.tascaeats.javafx.controllers;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.ul.fc.css.tascaeats.enums.KitchenType;
import pt.ul.fc.css.tascaeats.enums.FoodCategory;
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
        Button menusBtn = new Button("Gerir Menus");
        Button productsBtn = new Button("Gerir Produtos");
        Button usersBtn = new Button("Gerir Users");
        Button ordersBtn = new Button("Gerir Pedidos");
        Button logoutBtn = new Button("Logout");

        restaurantsBtn.setOnAction(e -> showAdminRestaurants());
        menusBtn.setOnAction(e -> showAdminMenus());
        productsBtn.setOnAction(e -> showAdminProducts());
        usersBtn.setOnAction(e -> showAdminUsers());
        ordersBtn.setOnAction(e -> showAdminOrders());
        logoutBtn.setOnAction(e -> handleLogout());

        contentBox.getChildren().addAll(restaurantsBtn, menusBtn, productsBtn, usersBtn, ordersBtn, logoutBtn);
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

        contentBox.getChildren().addAll(
            nameField, 
            usernameField, 
            passwordField, 
            submitBtn
        );
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

                contentBox.getChildren().addAll(
                    label, 
                    openBtn, 
                    closeBtn, 
                    menuBtn
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            contentBox.getChildren().add(new Label("Erro ao carregar restaurantes."));
        }

        Button criarBtn = new Button("Criar Restaurante");
        criarBtn.setOnAction(e -> showCreateRestaurantForm());
        contentBox.getChildren().add(criarBtn);

        addBackButtonAtBottom();
    }

    private void showCreateRestaurantForm() {
        titleLabel.setText("Criar Restaurante");
        contentBox.getChildren().clear();

        TextField restNif = new TextField();
        restNif.setPromptText("Nif");

        TextField restName = new TextField();
        restName.setPromptText("Nome");
    
        TextField restCity = new TextField();
        restCity.setPromptText("Cidade");
        
        TextField restStreet = new TextField();
        restStreet.setPromptText("Rua");

        TextField restPostalCode = new TextField();
        restPostalCode.setPromptText("Codigo Postal");
        
        ComboBox<KitchenType> kitchenTypeBox = new ComboBox<>();
        kitchenTypeBox.getItems().addAll(KitchenType.values());
        kitchenTypeBox.setPromptText("Tipo de cozinha");

        CheckBox restOpen = new CheckBox("Aberto");
        restOpen.setSelected(true);
        
        Button submitBtn = new Button("Criar Restaurante");
        submitBtn.setOnAction(e -> {
            try {
                RestaurantResponse created = grpcClient.restaurantStub.createRestaurant(
                    CreateRestaurantRequest.newBuilder()
                        .setName(restName.getText())
                        .setNif(restNif.getText())
                        .setAddress(
                            AddressProto.newBuilder()
                                .setCity(restCity.getText())
                                .setStreet(restStreet.getText())
                                .setPostalCode(restPostalCode.getText())
                                .build()
                        )
                        .setKitchenType(kitchenTypeBox.getValue().name())
                        .build()
                );

                if (restOpen.isSelected()) {
                    grpcClient.restaurantStub.openRestaurant(
                        RestaurantIdRequest.newBuilder()
                            .setRestaurantId(created.getId())
                            .build()
                    );
                }

                showAdminRestaurants();
            } catch (Exception ex) {
                ex.printStackTrace();
                contentBox.getChildren().add(new Label("Erro ao criar o restaurante"));
            }
        });

        contentBox.getChildren().addAll(
            restNif,
            restName,
            restCity,
            restStreet,
            restPostalCode,
            kitchenTypeBox,
            restOpen,
            submitBtn
        );

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

    
    // ================= MENUS =================

    private void showAdminMenus() {
        titleLabel.setText("Gerir Menus");
        contentBox.getChildren().clear();


        try {
            var response = grpcClient.menuStub.getMenusByFilter(
                    MenuFilterRequest.newBuilder().build()
            );

            for (var m : response.getMenusList()) {
                Label label = new Label(
                        m.getName() + " | " + m.getNumberOfProducts() + " produtos | preço médio: " + String.format("%.2f", m.getAveragePrice()) + "€"
                );

                Button verProdutosBtn = new Button("Ver Produtos");
                Button linkRestBtn = new Button("Associar Restaurante");
                Button removerBtn = new Button("Remover");

                verProdutosBtn.setOnAction(e -> showMenuProducts(m.getId(), m.getName()));
                
                linkRestBtn.setOnAction(e -> linkRestToMenu(m.getId()));

                removerBtn.setOnAction(e -> {
                    try {
                        grpcClient.menuStub.removeMenu(
                                MenuIdRequest.newBuilder()
                                        .setMenuId(m.getId())
                                        .build()
                        );
                        showAdminMenus();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        contentBox.getChildren().add(new Label("Erro ao remover menu."));
                    }
                });

                contentBox.getChildren().addAll(label, verProdutosBtn, linkRestBtn, removerBtn);
            }

        } catch (Exception e) {
            e.printStackTrace();
            contentBox.getChildren().add(new Label("Erro ao carregar menus."));
        }
        
        Button criarBtn = new Button("Criar Menu");
        criarBtn.setOnAction(e -> showCreateMenuForm());
        contentBox.getChildren().add(criarBtn);

        addBackButtonAtBottom();
    }

    private void linkRestToMenu(String menuId) {
        titleLabel.setText("Associar Restaurante ao Menu");
        contentBox.getChildren().clear();

        ComboBox<String> restaurantBox = new ComboBox<>();
        restaurantBox.setPromptText("Selecionar restaurante");

        java.util.Map<String, String> nameToId = new java.util.HashMap<>();

        try {
            var response = grpcClient.restaurantStub.getRestaurantsByFilter(
                    RestaurantFilterRequest.newBuilder().build()
            );
            for (var r : response.getRestaurantsList()) {
                restaurantBox.getItems().add(r.getName());
                nameToId.put(r.getName(), r.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            contentBox.getChildren().add(new Label("Erro ao carregar restaurantes."));
            addBackButtonAtBottom();
            return;
        }

        Button submitBtn = new Button("Associar");
        submitBtn.setOnAction(e -> {
            try {
                String selected = restaurantBox.getValue();
                if (selected == null) {
                    contentBox.getChildren().add(new Label("Seleciona um restaurante."));
                    return;
                }

                grpcClient.restaurantStub.associateMenuToRestaurant(
                        AssociateMenuRequest.newBuilder()
                                .setRestaurantId(nameToId.get(selected))
                                .setMenuId(menuId)
                                .build()
                );

                showAdminMenus();
            } catch (Exception ex) {
                ex.printStackTrace();
                contentBox.getChildren().add(new Label("Erro ao associar restaurante."));
            }
        });

        contentBox.getChildren().addAll(restaurantBox, submitBtn);
        addBackButtonAtBottom();
    }

    private void showMenuProducts(String menuId, String menuName) {
        titleLabel.setText("Produtos - " + menuName);
        contentBox.getChildren().clear();

        try {
            var menu = grpcClient.menuStub.getMenuById(
                    MenuIdRequest.newBuilder().setMenuId(menuId).build()
            );

            if (menu.getProductsList().isEmpty()) {
                contentBox.getChildren().add(new Label("Este menu não tem produtos."));
            }

            for (var p : menu.getProductsList()) {
                Label label = new Label(
                        p.getName() + " | " + p.getPrice() + "€ | " + p.getCategory()
                );

                Button removeFromMenuBtn = new Button("Remover do Menu");
                removeFromMenuBtn.setOnAction(e -> {
                    try {
                        grpcClient.menuStub.removeProductFromMenu(
                                RemoveProductFromMenuRequest.newBuilder()
                                        .setMenuId(menuId)
                                        .setProductId(p.getId())
                                        .build()
                        );
                        showMenuProducts(menuId, menuName);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        contentBox.getChildren().add(new Label("Erro ao remover produto do menu."));
                    }
                });

                contentBox.getChildren().addAll(label, removeFromMenuBtn);
            }

        } catch (Exception e) {
            e.printStackTrace();
            contentBox.getChildren().add(new Label("Erro ao carregar produtos."));
        }

        addBackButtonAtBottom();
    }
    
    private void showCreateMenuForm() {
        titleLabel.setText("Criar Menu");
        contentBox.getChildren().clear();

        TextField restName = new TextField();
        restName.setPromptText("Nome");

        Button submitBtn = new Button("Criar Menu");
        submitBtn.setOnAction(e -> {
            try {
                grpcClient.menuStub.createMenu(
                    CreateMenuRequest.newBuilder()
                        .setName(restName.getText())
                        .build()  
                );

                showAdminMenus();
            } catch (Exception ex) {
                ex.printStackTrace();
                contentBox.getChildren().add(new Label("Erro ao criar o menu"));
            }
        });

        contentBox.getChildren().addAll(
            restName,
            submitBtn  
        );

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

                Button updateProductBtn = new Button("Atualizar Produto");
                updateProductBtn.setOnAction(e -> showUpdateProductForm(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getCategory(), p.getAvailable()
                ));

                contentBox.getChildren().addAll(label, updateProductBtn);
            }

        } catch (Exception e) {
            e.printStackTrace();
            contentBox.getChildren().add(new Label("Erro ao carregar produtos."));
        }

        addBackButtonAtBottom();
    }

    private void showUpdateProductForm(String productId, String currentName, String currentDescription, double currentPrice, String currentCategory, boolean currentAvailable) {

        titleLabel.setText("Atualizar Produto");
        contentBox.getChildren().clear();

        TextField nameField = new TextField(currentName);
        nameField.setPromptText("Nome");

        TextField descriptionField = new TextField(currentDescription);
        descriptionField.setPromptText("Descrição");

        TextField priceField = new TextField(String.valueOf(currentPrice));
        priceField.setPromptText("Preço");

        ComboBox<FoodCategory> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(FoodCategory.values());
        categoryBox.setPromptText("Categoria");
        try {
            categoryBox.setValue(FoodCategory.valueOf(currentCategory));
        } catch (IllegalArgumentException ignored) {}

        CheckBox availableBox = new CheckBox("Disponível");
        availableBox.setSelected(currentAvailable);

        Button submitBtn = new Button("Guardar Alterações");
        submitBtn.setOnAction(e -> {
            try {
                grpcClient.productStub.updateProduct(
                        UpdateProductRequest.newBuilder()
                                .setProductId(productId)
                                .setName(nameField.getText())
                                .setDescription(descriptionField.getText())
                                .setPrice(Double.parseDouble(priceField.getText()))
                                .setCategory(categoryBox.getValue().name())
                                .setAvailable(availableBox.isSelected())
                                .build()
                );
                showAdminProducts();
            } catch (Exception ex) {
                ex.printStackTrace();
                contentBox.getChildren().add(new Label("Erro ao atualizar produto."));
            }
        });

        contentBox.getChildren().addAll(
                nameField,
                descriptionField,
                priceField,
                categoryBox,
                availableBox,
                submitBtn
        );

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

        ComboBox<FoodCategory> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(FoodCategory.values());
        categoryBox.setPromptText("Categoria");

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
                                .setCategory(categoryBox.getValue().name())
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
                categoryBox,
                availableBox,
                submitBtn
        );

        addBackButtonAtBottom();
    }

    // ================= ORDERS =================

    private void showAdminOrders() {
        titleLabel.setText("Gerir Pedidos");
        contentBox.getChildren().clear();

        try {
            var response = grpcClient.orderStub.getAllOrders(Empty.newBuilder().build());

            if (response.getOrdersList().isEmpty()) {
                contentBox.getChildren().add(new Label("Não existem pedidos."));
            }

            for (var order : response.getOrdersList()) {
                contentBox.getChildren().add(
                    new Label(
                        "Pedido " + order.getId()
                        + " | Estado: " + order.getStatus()
                        + " | Total: " + String.format("%.2f", order.getTotalPrice()) + "€"
                    )
                );

                switch (order.getStatus()) {
                    case "CREATED" -> {
                        Button cancelBtn = new Button("Cancelar");
                        cancelBtn.setOnAction(e -> {
                            try {
                                grpcClient.orderStub.cancelOrder(
                                    OrderIdRequest.newBuilder()
                                        .setOrderId(order.getId())
                                        .build()
                                );
                                showAdminOrders();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                contentBox.getChildren().add(new Label("Erro ao cancelar pedido."));
                            }
                        });
                        contentBox.getChildren().add(cancelBtn);
                    }

                    case "PAID" -> {
                        Button prepareBtn = new Button("Preparar");
                        Button cancelBtn = new Button("Cancelar");

                        prepareBtn.setOnAction(e -> {
                            try {
                                grpcClient.orderStub.updateOrderStatus(
                                    UpdateOrderStatusRequest.newBuilder()
                                        .setOrderId(order.getId())
                                        .setStatus("PREPARING")
                                        .build()
                                );
                                showAdminOrders();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                contentBox.getChildren().add(new Label("Erro ao preparar pedido."));
                            }
                        });

                        cancelBtn.setOnAction(e -> {
                            try {
                                grpcClient.orderStub.cancelOrder(
                                    OrderIdRequest.newBuilder()
                                        .setOrderId(order.getId())
                                        .build()
                                );
                                showAdminOrders();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                contentBox.getChildren().add(new Label("Erro ao cancelar pedido."));
                            }
                        });

                        contentBox.getChildren().addAll(prepareBtn, cancelBtn);
                    }

                    case "PREPARING" -> {
                        Button readyBtn = new Button("Marcar como Pronto");
                        readyBtn.setOnAction(e -> {
                            try {
                                grpcClient.orderStub.updateOrderStatus(
                                    UpdateOrderStatusRequest.newBuilder()
                                        .setOrderId(order.getId())
                                        .setStatus("READY")
                                        .build()
                                );
                                showAdminOrders();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                contentBox.getChildren().add(new Label("Erro ao marcar pedido como pronto."));
                            }
                        });
                        contentBox.getChildren().add(readyBtn);
                    }

                    case "READY" -> {
                        if (order.getCourierId() == null || order.getCourierId().isBlank()) {
                            contentBox.getChildren().add(
                                new Label("A aguardar atribuição automática de courier via Kafka...")
                            );
                        } else {
                            contentBox.getChildren().add(
                                new Label("Courier atribuído: " + order.getCourierId())
                            );
                        }
                    }

                    case "DELIVERING" -> {
                        Button deliveredBtn = new Button("Marcar como Entregue");
                        deliveredBtn.setOnAction(e -> {
                            try {
                                grpcClient.orderStub.updateOrderStatus(
                                    UpdateOrderStatusRequest.newBuilder()
                                        .setOrderId(order.getId())
                                        .setStatus("DELIVERED")
                                        .build()
                                );
                                showAdminOrders();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                contentBox.getChildren().add(new Label("Erro ao marcar pedido como entregue."));
                            }
                        });
                        contentBox.getChildren().add(deliveredBtn);
                    }

                    default -> {}
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            contentBox.getChildren().add(new Label("Erro ao carregar pedidos."));
        }

        addBackButtonAtBottom();
    }

    // ================= COURIER =================

    private void loadCourierView() {
        titleLabel.setText("Dashboard Courier");
        contentBox.getChildren().clear();

        Button ordersBtn = new Button("Ver Pedidos");
        Button logoutBtn = new Button("Logout");

        ordersBtn.setOnAction(e -> showCourierOrders());
        logoutBtn.setOnAction(e -> handleLogout());

        contentBox.getChildren().addAll(ordersBtn, logoutBtn);
    }

    private void showCourierOrders() {
        titleLabel.setText("Os Meus Pedidos");
        contentBox.getChildren().clear();

        try {
            var response = grpcClient.orderStub.getOrdersByCourier(
                    UserIdRequest.newBuilder().setUserId(userId).build()
            );

            if (response.getOrdersList().isEmpty()) {
                contentBox.getChildren().add(new Label("Não tens pedidos atribuídos."));
            }

            for (var order : response.getOrdersList()) {
                contentBox.getChildren().add(
                    new Label(
                        "Pedido " + order.getId() + " | Estado: " + order.getStatus() + " | Total: " + String.format("%.2f", order.getTotalPrice()) + "€"
                    )
                );

                switch (order.getStatus()) {
                    case "READY" -> {
                        Button startBtn = new Button("Iniciar Entrega");
                        startBtn.setOnAction(e -> {
                            try {
                                grpcClient.orderStub.updateOrderStatus(
                                        UpdateOrderStatusRequest.newBuilder()
                                                .setOrderId(order.getId())
                                                .setStatus("DELIVERING")
                                                .build()
                                );
                                showCourierOrders();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                contentBox.getChildren().add(new Label("Erro ao iniciar entrega."));
                            }
                        });
                        contentBox.getChildren().add(startBtn);
                    }
                    case "DELIVERING" -> {
                        Button doneBtn = new Button("Marcar como Entregue");
                        doneBtn.setOnAction(e -> {
                            try {
                                grpcClient.orderStub.updateOrderStatus(
                                        UpdateOrderStatusRequest.newBuilder()
                                                .setOrderId(order.getId())
                                                .setStatus("DELIVERED")
                                                .build()
                                );
                                showCourierOrders();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                contentBox.getChildren().add(new Label("Erro ao marcar como entregue."));
                            }
                        });
                        contentBox.getChildren().add(doneBtn);
                    }
                    default -> {}
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            contentBox.getChildren().add(new Label("Erro ao carregar pedidos."));
        }

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

    @FXML
    private void handleLogout() {
        try {
            this.userId = null;
            this.role = null;

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/javafx/login.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) contentBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("TascaEats - Login");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}