package pt.ul.fc.css.tascaeats.services;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ul.fc.css.tascaeats.dtos.order.AddProductDTO;
import pt.ul.fc.css.tascaeats.dtos.order.CreateOrderDTO;
import pt.ul.fc.css.tascaeats.dtos.order.OrderDTO;
import pt.ul.fc.css.tascaeats.common.dto.OrderDto;
import pt.ul.fc.css.tascaeats.dtos.order.PaymentDTO;
import pt.ul.fc.css.tascaeats.dtos.order.PaymentInfoDTO;
import pt.ul.fc.css.tascaeats.dtos.order.RemoveProductDTO;
import pt.ul.fc.css.tascaeats.entities.*;
import pt.ul.fc.css.tascaeats.exception.BusinessRuleException;
import pt.ul.fc.css.tascaeats.exception.EntityNotFoundException;
import pt.ul.fc.css.tascaeats.kafka.KafkaSender;
import pt.ul.fc.css.tascaeats.repositories.OrderRepository;
import pt.ul.fc.css.tascaeats.repositories.ProductRepository;
import pt.ul.fc.css.tascaeats.repositories.RestaurantRepository;
import pt.ul.fc.css.tascaeats.repositories.UserRepository;

@Service
@Transactional
public class OrderService {

  private RestaurantRepository restaurantRepo;
  private ProductRepository productRepo;
  private OrderRepository orderRepo;
  private UserRepository userRepo;
  private final KafkaSender kafkaSender;

  public OrderService(
      OrderRepository orderRepo,
      UserRepository userRepo,
      RestaurantRepository restaurantRepo,
      ProductRepository productRepo,
      KafkaSender kafkaSender) {
    this.restaurantRepo = restaurantRepo;
    this.productRepo = productRepo;
    this.orderRepo = orderRepo;
    this.userRepo = userRepo;
    this.kafkaSender = kafkaSender;
  }

  public Order getOrderById(UUID id) {
    return orderRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Order", id));
  }

  public Product getProductById(Order order, UUID productId) {
    Product product =
        productRepo
            .findById(productId)
            .orElseThrow(() -> new EntityNotFoundException("Product", productId));

    // if (!product.getRestaurant().equals(order.getRestaurant()))
    //   throw new BusinessRuleException("Product does not belong to this restaurant");

    return product;
  }

  public List<Order> getAllOrders() {
    return orderRepo.findAll();
  }

  // caso de uso H. Criação de pedido: Efetuar um pedido no sistema.
  public Order createOrder(CreateOrderDTO dto) {

    Customer customer =
        (Customer)
            userRepo
                .findById(dto.customerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer", dto.customerId()));

    Payment payment = createPayment(dto.payment());
    
    Order order = new Order(payment, customer, dto.address().toAddress());
    return orderRepo.save(order);
  }

  private Payment createPayment(PaymentInfoDTO dto) {
    return switch (dto.paymentType()) {
      case MULTIBANCO -> new MultibancoPayment(dto.cardFlag());
      case MBWAY -> new MBWayPayment(dto.phoneNumber());
      case CASH -> new CashPayment();
    };
  }

  public Order addProduct(UUID orderId, UUID restaurantId, AddProductDTO dto) {
    Order order = getOrderById(orderId);
    Product product = getProductById(order, dto.productId);
    Restaurant restaurant = restaurantRepo.findById(restaurantId)
              .orElseThrow(() -> new EntityNotFoundException("Restaurant", restaurantId));

    Menu menu = restaurant.getMenu();
    if (menu == null) throw new BusinessRuleException("Restaurant does not have a menu");

    boolean belongs = menu.getProducts().stream()
      .anyMatch(p -> p.getId().equals(product.getId()));
    
    if (!belongs) {
      throw new BusinessRuleException("Product does not belong to the specified restaurant");
    }

    order.addProduct(product, restaurant, dto.quantity);
    
    // Atualizar popularidade do produto
    product.setPopularity(product.getPopularity() + dto.quantity);
    productRepo.save(product);
    
    return orderRepo.save(order);
  }

  public Order removeProduct(UUID orderId, RemoveProductDTO dto) {
    Order order = getOrderById(orderId);
    Product product = getProductById(order, dto.productId);

    order.removeProduct(product, dto.quantity);
    return orderRepo.save(order);
  }

  // caso de uso I. Processar pagamento: Registo e processamento do pagamento
  // associado a um pedido.
  public Order payOrder(UUID orderId, PaymentDTO dto) {
    Order order = getOrderById(orderId);
    Payment payment = createPayment(dto);
    order.payOrder(payment, dto.amount());
    return orderRepo.save(order);
  }

  private Payment createPayment(PaymentDTO dto) {
    return switch (dto.paymentType()) {
      case MULTIBANCO -> new MultibancoPayment(dto.cardFlag());
      case MBWAY -> new MBWayPayment(dto.phoneNumber());
      case CASH -> new CashPayment(); // change will be calculated and set in payOrder
    };
  }

  // caso de uso J. Atualizar pedido: Atualização do estado do pedido (ex: Criado,
  // Pago, Em preparo, etc.).
  public Order prepareOrder(UUID orderId) {
    Order order = getOrderById(orderId);
    order.startPreparation();
    return orderRepo.save(order);
  }

  public Order markOrderReady(UUID orderId) {
    Order order = getOrderById(orderId);
    order.markAsReady();

    Order saved = orderRepo.save(order);

    kafkaSender.sendOrderReady(
        new OrderDto(
            saved.getId(),
            saved.getDeliveryAddress().getStreet(),
            saved.getDeliveryAddress().getCity(),
            saved.getDeliveryAddress().getPostalCode()
        )
    );

    return saved;
  }

  public Order assignCourierFromKafka(UUID orderId, UUID courierId) {
    Order order = getOrderById(orderId);

    order.assignCourierId(courierId);
    order.startDelivery();
    
    return orderRepo.save(order);
  }

  public Order startDelivery(UUID orderId) {
    Order order = getOrderById(orderId);
    order.startDelivery();
    return orderRepo.save(order);
  }

  public Order completeDelivery(UUID orderId) {
    Order order = getOrderById(orderId);

    order.completeDelivery();
    order.getCustomer().incrementOrderCount();

    return orderRepo.save(order);
  }

  public Order cancelOrder(UUID orderId) {
    Order order = getOrderById(orderId);
    order.cancel();
    return orderRepo.save(order);
  }

  public List<OrderDTO> getOrdersByCustomerId(UUID customerId) {
    return orderRepo.findAll().stream()
        .filter(o -> o.getCustomer().getId().equals(customerId))
        .map(OrderDTO::new)
        .toList();
  }

  public List<Order> getOrdersByCourierId(UUID courierId) {
    return orderRepo.findAll().stream()
        .filter(o -> courierId.equals(o.getCourierId()))
        .toList();
  }
}
