package pt.ul.fc.css.tascaeats.services;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ul.fc.css.tascaeats.dtos.restaurant.CreateRestaurantDTO;
import pt.ul.fc.css.tascaeats.entities.Address;
import pt.ul.fc.css.tascaeats.entities.Customer;
import pt.ul.fc.css.tascaeats.entities.Menu;
import pt.ul.fc.css.tascaeats.entities.Product;
import pt.ul.fc.css.tascaeats.entities.Restaurant;
import pt.ul.fc.css.tascaeats.entities.User;
import pt.ul.fc.css.tascaeats.exception.EntityNotFoundException;
// import pt.ul.fc.css.tascaeats.repositories.OrderRepository;
// import pt.ul.fc.css.tascaeats.repositories.ProductRepository;
import pt.ul.fc.css.tascaeats.repositories.RestaurantRepository;
import pt.ul.fc.css.tascaeats.repositories.UserRepository;

@Service
@Transactional
public class RestaurantService {

  private final RestaurantRepository restaurantRepo;
  private final UserRepository userRepo;
  // private final ProductRepository productRepo;
  // private final OrderRepository orderRepo;

  public RestaurantService(
      RestaurantRepository restaurantRepository,
      UserRepository userRepository/*,
      ProductRepository productRepository,
      OrderRepository orderRepository*/) {
    this.restaurantRepo = restaurantRepository;
    this.userRepo = userRepository;
    // this.productRepo = productRepository;
    // this.orderRepo = orderRepository;
  }

  // -------------------------------------------------------------------------
  // RESTAURANTS
  // -------------------------------------------------------------------------

  public Restaurant createRestaurant(CreateRestaurantDTO dto) {
    Restaurant restaurant = new Restaurant(dto.nif(), dto.name(), dto.address().toAddress(), dto.kitchenType(), false);
    return restaurantRepo.save(restaurant);
  }

  public Restaurant getById(UUID id) {
    return restaurantRepo
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Restaurant", id));
  }

  public List<Restaurant> getAllRestaurants() {
    return restaurantRepo.findAll();
  }

  public List<Restaurant> search(String name, Integer nPedidosRealizados, Integer nAvaliacoesRecebidas, Address address, String kitchenType, Boolean open, Double price) {
    return restaurantRepo.findAll().stream()

        .filter(r -> name == null || r.getName().toLowerCase().contains(name.toLowerCase()))

        .filter(r -> open == null || r.isOpen() == open)

        .filter(r -> nAvaliacoesRecebidas == null || r.getNRatings() >= nAvaliacoesRecebidas)

        .filter(r -> address == null || (
            address.getCity() == null || r.getAddress().getCity().equalsIgnoreCase(address.getCity())) &&
            (address.getStreet() == null || r.getAddress().getStreet().equalsIgnoreCase(address.getStreet())) &&
            (address.getPostalCode() == null || r.getAddress().getPostalCode().equalsIgnoreCase(address.getPostalCode()))
        )

        .filter(r -> kitchenType == null || r.getKitchenType().equalsIgnoreCase(kitchenType))

        .filter(r -> price == null || r.getAveragePrice() <= price)

        .toList();
  }

  public void openRestaurant(UUID id) {
    Restaurant restaurant = getById(id);
    restaurant.open();
  }

  public void closeRestaurant(UUID id) {
    Restaurant restaurant = getById(id);
    restaurant.close();
  }

  // -------------------------------------------------------------------------
  // PRODUCTS
  // -------------------------------------------------------------------------

  // public Product getProductById(UUID restaurantId, UUID productId) {
  //   Product product =
  //       productRepo
  //           .findById(productId)
  //           .orElseThrow(() -> new EntityNotFoundException("Product", productId));


  //   if (!product.getRestaurant().getId().equals(restaurantId))
  //     throw new BusinessRuleException("Product does not belong to this restaurant");

  //   return product;
  // }



  // public List<Product> getProducts(UUID restaurantId, String name, Boolean available) {
  //   if (name != null && available != null)
  //     return productRepo.findByRestaurantIdAndNameContainingIgnoreCaseAndAvailable(
  //         restaurantId, name, available);

  //   if (name != null)
  //     return productRepo.findByRestaurantIdAndNameContainingIgnoreCase(restaurantId, name);

  //   if (available != null)
  //     return productRepo.findByRestaurantIdAndAvailable(restaurantId, available);

  //   return productRepo.findByRestaurantId(restaurantId);
  // }
  public List<Product> getProducts(UUID restaurantId, String name, Boolean available) {
    if (restaurantId == null) throw new IllegalArgumentException("Restaurant ID cannot be null");

    Restaurant restaurant = getById(restaurantId);    
    Menu menu = restaurant.getMenu();

    if (menu == null) throw new IllegalArgumentException("Menu cannot be null on restaurant with ID " + restaurantId);

    if (name != null && available != null) {      
      return menu.getProducts().stream()
        .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()) && p.isAvailable() == available)
        .toList();
    }

    if (name != null)
      return menu.getProducts().stream()
        .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
        .toList();

    if (available != null)
      return menu.getProducts().stream()
        .filter(p -> p.isAvailable() == available)
        .toList();

    return menu.getProducts();
  }

  public Restaurant rateRestaurant(UUID customerId, UUID restaurantId, double rating) {
    User customer = userRepo.findById(customerId)
        .orElseThrow(() -> new EntityNotFoundException("Customer", customerId));
    Restaurant restaurant = getById(restaurantId);

    if (!(customer instanceof Customer))
      throw new IllegalArgumentException("User with ID " + customerId + " is not a customer");

    if (!((Customer) customer).canRateRestaurant(restaurant))
      throw new IllegalArgumentException("Customer with ID " + customerId + " is not allowed to rate restaurant with ID " + restaurantId);

    restaurant.rateRestaurant(rating);
    return restaurant;
  }



  // public Product addProduct(UUID restaurantId, CreateProductDTO dto) {
  //   Restaurant restaurant = getById(restaurantId);

  //   boolean exists = productRepo.existsByRestaurantIdAndNameIgnoreCase(restaurantId, dto.name());
  //   if (exists)
  //     throw new BusinessRuleException(
  //         "Product with the same name already exists in the restaurant");

  //   Product product = new Product(dto.name(), dto.description(), dto.price());

  //   if (dto.available() != null && !dto.available()) product.deactivate();

  //   restaurant.addProduct(product);

  //   return productRepo.save(product);
  // }

  // public Product updateProduct(UUID restaurantId, UUID productId, UpdateProductDTO dto) {
  //   Product product = getProductById(restaurantId, productId);

  //   product.update(dto.name(), dto.description(), dto.price(), dto.available());

  //   return product;
  // }

  // public void removeProduct(UUID restaurantId, UUID productId) {
  //   Restaurant restaurant = getById(restaurantId);
  //   Product product = getProductById(restaurantId, productId);

  //   // soft delete if product is part of an order
  //   boolean isUsedInOrders = orderRepo.existsByOrderItemsProductId(productId);
  //   if (isUsedInOrders) {
  //     product.deactivate();
  //   } else {
  //     restaurant.removeProduct(product);
  //     productRepo.delete(product);
  //   }
  // }
}
