package pt.ul.fc.css.tascaeats.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
// import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// import pt.ul.fc.css.tascaeats.dtos.product.CreateProductDTO;
import pt.ul.fc.css.tascaeats.dtos.product.ProductDTO;
// import pt.ul.fc.css.tascaeats.dtos.product.UpdateProductDTO;
import pt.ul.fc.css.tascaeats.dtos.restaurant.CreateRestaurantDTO;
import pt.ul.fc.css.tascaeats.dtos.restaurant.RestaurantDTO;
import pt.ul.fc.css.tascaeats.entities.Address;
import pt.ul.fc.css.tascaeats.entities.Product;
import pt.ul.fc.css.tascaeats.entities.Restaurant;
import pt.ul.fc.css.tascaeats.services.RestaurantService;

@Validated
@RestController
@RequestMapping("/restaurants")
@Tag(
    name = "Restaurants",
    description = "Operations related to restaurants and their products (use cases D to G)")
public class RestaurantController {

  private final RestaurantService restaurantService;

  public RestaurantController(RestaurantService restaurantService) {
    this.restaurantService = restaurantService;
  }

  // -------------------------------------------------------------------------
  // RESTAURANT
  // -------------------------------------------------------------------------

  @PostMapping
  @Operation(
      summary = "Create a new restaurant",
      description = "Creates a new restaurant and returns the created resource.")
  public ResponseEntity<RestaurantDTO> createRestaurant(
      @Valid @RequestBody CreateRestaurantDTO dto) {
    Restaurant created = restaurantService.createRestaurant(dto);
    RestaurantDTO res = new RestaurantDTO(created);
    return ResponseEntity.created(URI.create("/restaurants/" + res.id())).body(res);
  }

  @GetMapping("/{restaurantId}")
  @Operation(
      summary = "Get restaurant by ID",
      description = "Retrieves a restaurant by its unique identifier.")
  public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable UUID restaurantId) {
    Restaurant found = restaurantService.getById(restaurantId);
    RestaurantDTO res = new RestaurantDTO(found);
    return ResponseEntity.ok(res);
  }

    @GetMapping()
    @Operation(summary = "Get all restaurants")
    public ResponseEntity<List<RestaurantDTO>> getRestaurants() {
        List<Restaurant> results = restaurantService.getAllRestaurants();
        List<RestaurantDTO> dtoList = results.stream().map(RestaurantDTO::new).toList();
        return ResponseEntity.ok(dtoList);
    }

  @PatchMapping("/{restaurantId}/open")
  @Operation(summary = "Open restaurant", description = "Marks the specified restaurant as open.")
  public ResponseEntity<Void> openRestaurant(@PathVariable UUID restaurantId) {
    restaurantService.openRestaurant(restaurantId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{restaurantId}/close")
  @Operation(
      summary = "Close restaurant",
      description = "Marks the specified restaurant as closed.")
  public ResponseEntity<Void> closeRestaurant(@PathVariable UUID restaurantId) {
    restaurantService.closeRestaurant(restaurantId);
    return ResponseEntity.noContent().build();
  }

  // -------------------------------------------------------------------------
  // PRODUCTS
  // -------------------------------------------------------------------------

  // @GetMapping("/{restaurantId}/products/{productId}")
  // @Operation(
  //     summary = "Get product by ID",
  //     description = "Retrieves a product by its unique identifier.")
  // public ResponseEntity<ProductDTO> getProductById(
  //     @PathVariable UUID restaurantId, @PathVariable UUID productId) {
  //   Product product = restaurantService.getProductById(restaurantId, productId);
  //   ProductDTO res = new ProductDTO(product);
  //   return ResponseEntity.ok(res);
  // }

  @GetMapping("/{restaurantId}/products")
  @Operation(
      summary = "Get restaurant products",
      description =
          "Retrieves all products for a restaurant, optionally filtered by name and availability.")
  public ResponseEntity<List<ProductDTO>> getProducts(
      @PathVariable UUID restaurantId,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Boolean available) {
    List<Product> results = restaurantService.getProducts(restaurantId, name, available);
    List<ProductDTO> dtoList = results.stream().map(ProductDTO::new).toList();
    return ResponseEntity.ok(dtoList);
  }

  // @PostMapping("/{restaurantId}/products")
  // @Operation(
  //     summary = "Add new product to restaurant",
  //     description = "Creates a new product for the specified restaurant.")
  // public ResponseEntity<ProductDTO> addProduct(
  //     @PathVariable UUID restaurantId, @Valid @RequestBody CreateProductDTO dto) {
  //   Product created = restaurantService.addProduct(restaurantId, dto);
  //   return ResponseEntity.created(
  //           URI.create("/restaurants/" + restaurantId + "/products/" + created.getId()))
  //       .body(new ProductDTO(created));
  // }

  // @DeleteMapping("/{restaurantId}/products/{productId}")
  // @Operation(
  //     summary = "Remove product from restaurant",
  //     description = "Removes a product from the specified restaurant.")
  // public ResponseEntity<Void> removeProduct(
  //     @PathVariable UUID restaurantId, @PathVariable UUID productId) {
  //   restaurantService.removeProduct(restaurantId, productId);
  //   return ResponseEntity.noContent().build();
  // }

  // @PatchMapping("/{restaurantId}/products/{productId}")
  // @Operation(
  //     summary = "Update product",
  //     description = "Updates the details of a specific product belonging to a restaurant.")
  // public ResponseEntity<ProductDTO> updateProduct(
  //     @PathVariable UUID restaurantId,
  //     @PathVariable UUID productId,
  //     @Valid @RequestBody UpdateProductDTO dto) {
  //   Product updated = restaurantService.updateProduct(restaurantId, productId, dto);
  //   return ResponseEntity.ok(new ProductDTO(updated));
  // }

  // -------------------------------------------------------------------------
  // RATINGS
  // -------------------------------------------------------------------------

  @PostMapping("/{restaurantId}/ratings")
  @Operation(summary = "Rate restaurant")
  public ResponseEntity<RestaurantDTO> rateRestaurant(
      @PathVariable UUID restaurantId,
      @RequestParam UUID customerId,
      @RequestParam double rating) {
    Restaurant rated = restaurantService.rateRestaurant(customerId, restaurantId, rating);
    RestaurantDTO res = new RestaurantDTO(rated);
    return ResponseEntity.ok(res);
  }

  @GetMapping("/filter")
  @Operation(
      summary = "Search restaurants",
      description = "Searches restaurants optionally filtered by name and/or city.")
  public ResponseEntity<List<RestaurantDTO>> searchRestaurants(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer nPedidosRealizados,
      @RequestParam(required = false) Integer nAvaliacoesRecebidas,
      @RequestParam(required = false) Address address,
      @RequestParam(required = false) String kitchenType,
      @RequestParam(required = false) Boolean open,
      @RequestParam(required = false) Double price) {
    List<Restaurant> results = restaurantService.search(name, nPedidosRealizados, nAvaliacoesRecebidas, address, kitchenType, open, price);
    List<RestaurantDTO> dtoList = results.stream().map(RestaurantDTO::new).toList();

    return ResponseEntity.ok(dtoList);
  }
}
