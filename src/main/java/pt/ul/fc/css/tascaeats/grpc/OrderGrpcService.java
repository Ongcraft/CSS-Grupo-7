package pt.ul.fc.css.tascaeats.grpc;

import java.util.UUID;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import pt.ul.fc.css.tascaeats.dtos.order.OrderDTO;
import pt.ul.fc.css.tascaeats.entities.Order;
import pt.ul.fc.css.tascaeats.entities.OrderItem;
import pt.ul.fc.css.tascaeats.services.OrderService;

@GrpcService
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderService orderService;

    public OrderGrpcService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void getOrderById(OrderIdRequest request,
                             StreamObserver<OrderResponse> responseObserver) {
        try {
            Order order = orderService.getOrderById(
                    UUID.fromString(request.getOrderId())
            );

            responseObserver.onNext(toGrpcOrderResponse(order));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getOrdersByCustomer(UserIdRequest request,
                                    StreamObserver<OrderListResponse> responseObserver) {
        try {
            var orders = orderService.getOrdersByCustomerId(
                    UUID.fromString(request.getUserId())
            );

            OrderListResponse.Builder response = OrderListResponse.newBuilder();

            for (OrderDTO order : orders) {
                response.addOrders(toGrpcOrderDTOResponse(order));
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void cancelOrder(OrderIdRequest request,
                            StreamObserver<BooleanResponse> responseObserver) {
        try {
            orderService.cancelOrder(UUID.fromString(request.getOrderId()));

            responseObserver.onNext(
                    BooleanResponse.newBuilder()
                            .setValue(true)
                            .build()
            );
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void assignCourierToOrder(OrderIdRequest request,
                                     StreamObserver<OrderResponse> responseObserver) {
        try {
            Order order = orderService.assignCourier(
                    UUID.fromString(request.getOrderId())
            );

            responseObserver.onNext(toGrpcOrderResponse(order));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    private OrderResponse toGrpcOrderDTOResponse(OrderDTO order) {
        OrderResponse.Builder builder = OrderResponse.newBuilder()
                .setId(order.id().toString())
                .setCustomerId(order.customerId().toString())
                .setTotalPrice(order.total())
                .setStatus(order.orderStatus().toString());

        if (order.courierId() != null) {
            builder.setCourierId(order.courierId().toString());
        }

        if (order.address() != null) {
            builder.setDeliveryAddress(
                    AddressProto.newBuilder()
                            .setCity(order.address().city())
                            .setPostalCode(order.address().postalCode())
                            .setStreet(order.address().street())
                            .build()
            );
        }

        if (order.items() != null) {
            for (var item : order.items()) {
                builder.addItems(
                        OrderItemResponse.newBuilder()
                                .setProductId(item.productId().toString())
                                .setProductName(item.productName())
                                .setQuantity(item.quantity())
                                .setUnitPrice(item.quantity() == 0 ? 0 : item.totalPrice() / item.quantity())
                                .setSubtotal(item.totalPrice())
                                .build()
                );
            }
        }

        return builder.build();
    }

    private OrderResponse toGrpcOrderResponse(Order order) {
        OrderResponse.Builder builder = OrderResponse.newBuilder()
                .setId(order.getId().toString())
                .setTotalPrice(order.getTotal())
                .setStatus(order.getStatus().toString());

        if (order.getCustomer() != null) {
            builder.setCustomerId(order.getCustomer().getId().toString());
        }

        if (order.getCourier() != null) {
            builder.setCourierId(order.getCourier().getId().toString());
        }

        if (order.getDeliveryAddress() != null) {
            builder.setDeliveryAddress(
                    AddressProto.newBuilder()
                            .setCity(order.getDeliveryAddress().getCity())
                            .setPostalCode(order.getDeliveryAddress().getPostalCode())
                            .setStreet(order.getDeliveryAddress().getStreet())
                            .build()
            );
        }

        for (OrderItem item : order.getOrderItems()) {
            builder.addItems(
                    OrderItemResponse.newBuilder()
                            .setProductId(item.getProduct().getId().toString())
                            .setProductName(item.getProduct().getName())
                            .setQuantity(item.getQuantity())
                            .setUnitPrice(item.getProduct().getPrice())
                            .setSubtotal(item.getTotalPrice())
                            .build()
            );
        }

        return builder.build();
    }
}