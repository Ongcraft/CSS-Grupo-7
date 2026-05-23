package pt.ul.fc.css.tascaeats.grpc;

import java.util.UUID;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;
import pt.ul.fc.css.tascaeats.dtos.order.PaymentDTO;
import pt.ul.fc.css.tascaeats.entities.CashPayment;
import pt.ul.fc.css.tascaeats.entities.MBWayPayment;
import pt.ul.fc.css.tascaeats.entities.MultibancoPayment;
import pt.ul.fc.css.tascaeats.entities.Order;
import pt.ul.fc.css.tascaeats.entities.Payment;
import pt.ul.fc.css.tascaeats.enums.CardFlag;
import pt.ul.fc.css.tascaeats.enums.PaymentType;
import pt.ul.fc.css.tascaeats.services.OrderService;

@GrpcService
@Transactional
public class PaymentGrpcService extends PaymentServiceGrpc.PaymentServiceImplBase {

    private final OrderService orderService;

    public PaymentGrpcService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void registerPayment(CreatePaymentRequest request,
                            StreamObserver<PaymentResponse> responseObserver) {
        try {
            PaymentType paymentType = PaymentType.valueOf(request.getPaymentType());

            PaymentDTO dto = new PaymentDTO(
                    paymentType,
                    request.getCashReceived(),
                    request.getCardBrand().isBlank() ? null : CardFlag.valueOf(request.getCardBrand()),
                    request.getPhoneNumber().isBlank() ? null : request.getPhoneNumber()
            );

            Order order = orderService.payOrder(UUID.fromString(request.getOrderId()), dto);

            responseObserver.onNext(toGrpcPaymentResponse(order));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getPaymentByOrder(OrderIdRequest request,
                                  StreamObserver<PaymentResponse> responseObserver) {
        try {
            Order order = orderService.getOrderById(
                    UUID.fromString(request.getOrderId())
            );

            responseObserver.onNext(toGrpcPaymentResponse(order));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    private PaymentResponse toGrpcPaymentResponse(Order order) {
        Payment payment = order.getPayment();

        PaymentResponse.Builder builder = PaymentResponse.newBuilder()
                .setOrderId(order.getId().toString())
                .setPaid(order.getAmountPaid() > 0);

        if (payment != null) {
            builder.setPaymentType(payment.getPaymentType().toString());

            if (payment instanceof MBWayPayment mbway) {
                builder.setPhoneNumber(mbway.getPhoneNumber());
            }

            if (payment instanceof MultibancoPayment multibanco) {
                builder.setCardBrand(multibanco.getCardFlag().toString());
            }

            if (payment instanceof CashPayment cash) {
                builder.setCashReceived(order.getAmountPaid());
                builder.setChangeAmount(cash.getChange());
            }
        }

        return builder.build();
    }
}