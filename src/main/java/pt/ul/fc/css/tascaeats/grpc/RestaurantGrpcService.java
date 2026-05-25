package pt.ul.fc.css.tascaeats.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.tascaeats.dtos.common.AddressDTO;
import pt.ul.fc.css.tascaeats.dtos.restaurant.CreateRestaurantDTO;
import pt.ul.fc.css.tascaeats.entities.Restaurant;
import pt.ul.fc.css.tascaeats.enums.KitchenType;
import pt.ul.fc.css.tascaeats.services.RestaurantService;
import pt.ul.fc.css.tascaeats.services.MenuService;

import java.util.List;
import java.util.UUID;

@GrpcService
@Transactional
public class RestaurantGrpcService extends RestaurantServiceGrpc.RestaurantServiceImplBase {

    private final RestaurantService restaurantService;
    private final MenuService menuService;

    public RestaurantGrpcService(RestaurantService restaurantService, MenuService menuService) {
        this.restaurantService = restaurantService;
        this.menuService = menuService;
    }

    @Override
    public void createRestaurant(CreateRestaurantRequest request,
                                 StreamObserver<RestaurantResponse> responseObserver) {
        try {
            AddressDTO addressDTO = new AddressDTO(
                    request.getAddress().getCity(),
                    request.getAddress().getPostalCode(),
                    request.getAddress().getStreet()
            );

            CreateRestaurantDTO dto = new CreateRestaurantDTO(
                    request.getName(),
                    request.getNif(),
                    addressDTO,
                    KitchenType.valueOf(request.getKitchenType())
            );

            Restaurant restaurant = restaurantService.createRestaurant(dto);

            responseObserver.onNext(toGrpcRestaurantResponse(restaurant));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getRestaurantById(RestaurantIdRequest request,
                                  StreamObserver<RestaurantResponse> responseObserver) {
        try {
            Restaurant restaurant =
                    restaurantService.getById(UUID.fromString(request.getRestaurantId()));

            responseObserver.onNext(toGrpcRestaurantResponse(restaurant));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void openRestaurant(RestaurantIdRequest request,
                               StreamObserver<Empty> responseObserver) {
        try {
            restaurantService.openRestaurant(UUID.fromString(request.getRestaurantId()));

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void closeRestaurant(RestaurantIdRequest request,
                                StreamObserver<Empty> responseObserver) {
        try {
            restaurantService.closeRestaurant(UUID.fromString(request.getRestaurantId()));

            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getRestaurantsByFilter(RestaurantFilterRequest request, StreamObserver<RestaurantListResponse> responseObserver) {
        try {
            List<Restaurant> restaurants = restaurantService.getAllRestaurants();

            RestaurantListResponse.Builder response = RestaurantListResponse.newBuilder();

            for (Restaurant restaurant : restaurants) {
                response.addRestaurants(toGrpcRestaurantResponse(restaurant));
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void associateMenuToRestaurant(AssociateMenuRequest request, StreamObserver<RestaurantResponse> responseObserver) {
        try {
            UUID restaurantId = UUID.fromString(request.getRestaurantId());
            UUID menuId = UUID.fromString(request.getMenuId());

            Restaurant current = restaurantService.getById(restaurantId);
            if (current.getMenu() != null) {
                menuService.removeRestaurantFromMenu(current.getMenu().getId(), restaurantId);
            }

            menuService.addRestaurantToMenu(menuId, restaurantId);

            Restaurant updated = restaurantService.getById(restaurantId);
            responseObserver.onNext(toGrpcRestaurantResponse(updated));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    private RestaurantResponse toGrpcRestaurantResponse(Restaurant restaurant) {
        RestaurantResponse.Builder builder = RestaurantResponse.newBuilder()
                .setId(restaurant.getId().toString())
                .setName(restaurant.getName())
                .setNif(restaurant.getNif())
                .setOpen(restaurant.isOpen())
                .setRating(restaurant.getRating());

        if (restaurant.getMenu() != null) {
            builder.setMenuId(restaurant.getMenu().getId().toString());
        }

        if (restaurant.getAddress() != null) {
            builder.setAddress(AddressProto.newBuilder()
                    .setCity(restaurant.getAddress().getCity())
                    .setPostalCode(restaurant.getAddress().getPostalCode())
                    .setStreet(restaurant.getAddress().getStreet())
                    .build());
        }

        return builder.build();
    }
}