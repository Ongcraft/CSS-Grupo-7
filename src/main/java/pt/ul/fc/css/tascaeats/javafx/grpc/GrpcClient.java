package pt.ul.fc.css.tascaeats.javafx.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import pt.ul.fc.css.tascaeats.grpc.UserServiceGrpc;
import pt.ul.fc.css.tascaeats.grpc.RestaurantServiceGrpc;
import pt.ul.fc.css.tascaeats.grpc.MenuServiceGrpc;
import pt.ul.fc.css.tascaeats.grpc.ProductServiceGrpc;
import pt.ul.fc.css.tascaeats.grpc.OrderServiceGrpc;

public class GrpcClient {

    private final ManagedChannel channel;

    public final UserServiceGrpc.UserServiceBlockingStub userStub;
    public final RestaurantServiceGrpc.RestaurantServiceBlockingStub restaurantStub;
    public final MenuServiceGrpc.MenuServiceBlockingStub menuStub;
    public final ProductServiceGrpc.ProductServiceBlockingStub productStub;
    public final OrderServiceGrpc.OrderServiceBlockingStub orderStub;

    public GrpcClient() {
        channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();

        userStub = UserServiceGrpc.newBlockingStub(channel);
        restaurantStub = RestaurantServiceGrpc.newBlockingStub(channel);
        menuStub = MenuServiceGrpc.newBlockingStub(channel);
        productStub = ProductServiceGrpc.newBlockingStub(channel);
        orderStub = OrderServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() {
        channel.shutdown();
    }
}