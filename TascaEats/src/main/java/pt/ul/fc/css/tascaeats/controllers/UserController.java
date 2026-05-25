package pt.ul.fc.css.tascaeats.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.ul.fc.css.tascaeats.dtos.restaurant.RestaurantDTO;
import pt.ul.fc.css.tascaeats.dtos.user.CreateAdminDTO;
import pt.ul.fc.css.tascaeats.dtos.user.CreateCourierDTO;
import pt.ul.fc.css.tascaeats.dtos.user.CreateCustomerDTO;
import pt.ul.fc.css.tascaeats.dtos.user.LoginUserDTO;
import pt.ul.fc.css.tascaeats.dtos.user.UpdateAdminDTO;
import pt.ul.fc.css.tascaeats.dtos.user.UpdateCourierDTO;
import pt.ul.fc.css.tascaeats.dtos.user.UpdateCustomerDTO;
import pt.ul.fc.css.tascaeats.dtos.user.UserDTO;
import pt.ul.fc.css.tascaeats.entities.Admin;
import pt.ul.fc.css.tascaeats.entities.Courier;
import pt.ul.fc.css.tascaeats.entities.Customer;
import pt.ul.fc.css.tascaeats.entities.Restaurant;
import pt.ul.fc.css.tascaeats.entities.User;
import pt.ul.fc.css.tascaeats.services.UserService;
import pt.ul.fc.css.tascaeats.enums.Role;
import org.springframework.web.bind.annotation.RequestParam;


@Validated
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Operations related to users management")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  // -------------------- AUTH --------------------

  // use case A
  @PostMapping("/login")
  @Operation(
      summary = "User login",
      description = "Authenticates a user and returns whether login was successful")
  public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginUserDTO dto) {
    UserDTO res = userService.login(dto);    
    return ResponseEntity.ok().body(res);
  }

  // -------------------- CREATE --------------------

  // use case B

  @PostMapping("/admins")
  @Operation(summary = "Create admin")
  public ResponseEntity<UserDTO> createAdmin(@Valid @RequestBody CreateAdminDTO dto) {
    Admin admin = userService.registerAdmin(dto);
    UserDTO res = new UserDTO(admin);
    return ResponseEntity.created(URI.create("/users/" + res.id())).body(res);
  }

  @PostMapping("/couriers")
  @Operation(summary = "Create courier")
  public ResponseEntity<UserDTO> createCourier(@Valid @RequestBody CreateCourierDTO dto) {
    Courier courier = userService.registerCourier(dto);
    UserDTO res = new UserDTO(courier);
    return ResponseEntity.created(URI.create("/users/" + res.id())).body(res);
  }

  @PostMapping("/customers")
  @Operation(summary = "Create customer")
  public ResponseEntity<UserDTO> createCustomer(@Valid @RequestBody CreateCustomerDTO dto) {
    Customer customer = userService.registerCustomer(dto);
    UserDTO res = new UserDTO(customer);
    return ResponseEntity.created(URI.create("/users/" + res.id())).body(res);
  }

  // -------------------- READ --------------------  // falta um get all location or slt? porque o user pode ter varias addreses

  @GetMapping("/{userId}")
  @Operation(summary = "Get user by ID")
  public ResponseEntity<UserDTO> getUserById(@PathVariable UUID userId) {
    User user = userService.getUserById(userId);
    UserDTO res = UserDTO.from(user);
    return ResponseEntity.ok(res);
  }

  @GetMapping
  @Operation(summary = "Get all users")
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    List<User> found = userService.getAllUsers();
    List<UserDTO> res = found.stream().map(UserDTO::from).toList();
    return ResponseEntity.ok(res);
  }

  @GetMapping("/couriers/available")
  @Operation(summary = "Get available couriers")
  public ResponseEntity<List<UserDTO>> getAvailableCouriers() {
    List<Courier> found = userService.getAvailableCouriers();
    List<UserDTO> res = found.stream().map(UserDTO::new).toList();
    return ResponseEntity.ok(res);
  }

  @GetMapping("/{customerId}/restaurants-to-rate")
  @Operation(summary = "Get restaurants to rate for a customer")
  public ResponseEntity<List<RestaurantDTO>> getRestaurantsToRate(@PathVariable UUID customerId) {
    List<Restaurant> found = userService.getRestaurantsToRate(customerId);
    List<RestaurantDTO> res = found.stream().map(RestaurantDTO::new).toList();
    return ResponseEntity.ok(res);
  }

  // -------------------- UPDATE --------------------

  @PutMapping("/admins/{id}")
  @Operation(summary = "Update admin")
  public ResponseEntity<UserDTO> updateAdmin(
      @PathVariable UUID id, @Valid @RequestBody UpdateAdminDTO dto) {
    Admin admin = userService.updateAdmin(id, dto);
    UserDTO res = new UserDTO(admin);
    return ResponseEntity.ok(res);
  }

  @PutMapping("/couriers/{id}")
  @Operation(summary = "Update courier")
  public ResponseEntity<UserDTO> updateCourier(
      @PathVariable UUID id, @Valid @RequestBody UpdateCourierDTO dto) {
    Courier courier = userService.updateCourier(id, dto);
    UserDTO res = new UserDTO(courier);
    return ResponseEntity.ok(res);
  }

  @PutMapping("/customers/{id}")
  @Operation(summary = "Update client")
  public ResponseEntity<UserDTO> updateClient(
      @PathVariable UUID id, @Valid @RequestBody UpdateCustomerDTO dto) {
    Customer customer = userService.updateCustomer(id, dto);
    UserDTO res = new UserDTO(customer);
    return ResponseEntity.ok(res);
  }

  // -------------------- DELETE --------------------

  @DeleteMapping("/{userId}")
  @Operation(summary = "Delete user by ID")
  public ResponseEntity<Void> removeUser(@PathVariable UUID userId) {
    userService.removeUser(userId);
    return ResponseEntity.noContent().build();
  }

  // -------------------- VERIFY --------------------

  // use case C

  @GetMapping("/{userId}/exists")
  @Operation(summary = "Check if user exists")
  public ResponseEntity<Boolean> userExists(@PathVariable UUID userId) {
    return ResponseEntity.ok(userService.verifyUser(userId));
  }

  // -------------------- USER FILTERS --------------------

  @GetMapping("/filter")
  @Operation(summary = "Search users")
  public ResponseEntity<List<UserDTO>> searchUsers(
      @RequestParam(required = false) String nome, 
      @RequestParam(required = false) Role role, 
      @RequestParam(required = false) Integer nPedidos, 
      @RequestParam(required = false) Integer nEntregas) {

      List<User> found = userService.search(nome, role, nPedidos, nEntregas);
      List<UserDTO> res = found.stream().map(UserDTO::from).toList();
      return ResponseEntity.ok(res);
  }
}
