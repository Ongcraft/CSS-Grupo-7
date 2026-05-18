package pt.ul.fc.css.tascaeats.grpc;

import java.util.List;
import java.util.UUID;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import pt.ul.fc.css.tascaeats.entities.Menu;
import pt.ul.fc.css.tascaeats.entities.Product;
import pt.ul.fc.css.tascaeats.services.MenuService;

@GrpcService
public class MenuGrpcService extends MenuServiceGrpc.MenuServiceImplBase {

    private final MenuService menuService;

    public MenuGrpcService(MenuService menuService) {
        this.menuService = menuService;
    }

    @Override
    public void createMenu(CreateMenuRequest request, StreamObserver<MenuResponse> responseObserver) {
        try {
            Menu menu = menuService.createMenu(request.getName());

            responseObserver.onNext(toGrpcMenuResponse(menu));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getMenuById(MenuIdRequest request, StreamObserver<MenuResponse> responseObserver) {
        try {
            Menu menu = menuService.getMenubyId(UUID.fromString(request.getMenuId()));

            responseObserver.onNext(toGrpcMenuResponse(menu));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getMenusByFilter(MenuFilterRequest request, StreamObserver<MenuListResponse> responseObserver) {
        try {
            String name = request.getName().isBlank() ? null : request.getName();

            Integer minProducts = request.getMinProducts() == 0 ? null : request.getMinProducts();
            Double maxAveragePrice = request.getMaxAveragePrice() == 0.0 ? null : request.getMaxAveragePrice();

            List<Menu> menus = menuService.search(name, minProducts, maxAveragePrice);

            MenuListResponse.Builder response = MenuListResponse.newBuilder();

            for (Menu menu : menus) {
                response.addMenus(toGrpcMenuResponse(menu));
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void addProductToMenu(AddProductToMenuRequest request, StreamObserver<MenuResponse> responseObserver) {
        try {
            Menu menu = menuService.addProductToMenu(
                    UUID.fromString(request.getMenuId()),
                    UUID.fromString(request.getProductId())
            );

            responseObserver.onNext(toGrpcMenuResponse(menu));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void removeProductFromMenu(RemoveProductFromMenuRequest request, StreamObserver<MenuResponse> responseObserver) {
        try {
            Menu menu = menuService.removeProductFromMenu(
                    UUID.fromString(request.getMenuId()),
                    UUID.fromString(request.getProductId())
            );

            responseObserver.onNext(toGrpcMenuResponse(menu));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    private MenuResponse toGrpcMenuResponse(Menu menu) {
        MenuResponse.Builder builder = MenuResponse.newBuilder()
                .setId(menu.getId().toString())
                .setName(menu.getName())
                .setNumberOfProducts(menu.getNumberOfProducts())
                .setAveragePrice(menu.getAveragePrice());

        for (Product product : menu.getProducts()) {
            builder.addProducts(toGrpcProductResponse(product));
        }

        return builder.build();
    }

    private ProductResponse toGrpcProductResponse(Product product) {
        return ProductResponse.newBuilder()
                .setId(product.getId().toString())
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setPrice(product.getPrice())
                .setCategory(product.getCategory().toString())
                .setAvailable(product.isAvailable())
                .build();
    }
}