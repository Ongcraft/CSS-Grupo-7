package pt.ul.fc.css.tascaeats.services;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ul.fc.css.tascaeats.common.dto.CourierDto;
import pt.ul.fc.css.tascaeats.dtos.common.AddressDTO;
import pt.ul.fc.css.tascaeats.dtos.user.CreateAdminDTO;
import pt.ul.fc.css.tascaeats.dtos.user.CreateCourierDTO;
import pt.ul.fc.css.tascaeats.dtos.user.CreateCustomerDTO;
import pt.ul.fc.css.tascaeats.dtos.user.LoginUserDTO;
import pt.ul.fc.css.tascaeats.dtos.user.UpdateAdminDTO;
import pt.ul.fc.css.tascaeats.dtos.user.UpdateCourierDTO;
import pt.ul.fc.css.tascaeats.dtos.user.UpdateCustomerDTO;
import pt.ul.fc.css.tascaeats.dtos.user.UserDTO;
import pt.ul.fc.css.tascaeats.entities.Address;
import pt.ul.fc.css.tascaeats.entities.Admin;
import pt.ul.fc.css.tascaeats.entities.Courier;
import pt.ul.fc.css.tascaeats.entities.Customer;
import pt.ul.fc.css.tascaeats.entities.Restaurant;
import pt.ul.fc.css.tascaeats.entities.User;
import pt.ul.fc.css.tascaeats.enums.Role;
import pt.ul.fc.css.tascaeats.exception.BusinessRuleException;
import pt.ul.fc.css.tascaeats.exception.EntityNotFoundException;
import pt.ul.fc.css.tascaeats.kafka.KafkaSender;
import pt.ul.fc.css.tascaeats.repositories.UserRepository;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepo;

  private final KafkaSender kafkaSender;

  public UserService(UserRepository userRepo, KafkaSender kafkaSender) {
    this.userRepo = userRepo;
    this.kafkaSender = kafkaSender;
  }

  // A. Login com autenticação: Vamos fazer *mock*. Qualquer palavra-passe será
  // aceite contanto que o utilizador seja válido.
  public UserDTO login(LoginUserDTO dto) {
    User user = userRepo.findByUsername(dto.username());
    if (user == null) throw new BusinessRuleException("User with username:" + dto.username() + " not found");
    if (user.getRole() == Role.ADMIN) return new UserDTO((Admin) user);
    if (user.getRole() == Role.COURIER) return new UserDTO((Courier) user);
    if (user.getRole() == Role.CUSTOMER) return new UserDTO((Customer) user);
    throw new BusinessRuleException("User with username:" + dto.username() + " has an invalid role");
  }

  // B. Registo de utilizadores: Criação de perfis de Clientes, Administradores e
  // Entregadores.
  public Customer registerCustomer(CreateCustomerDTO dto) {
    if (userRepo.findByUsername(dto.username()) != null) {
      throw new BusinessRuleException("Username " + dto.username() + " is already in use");
    }
    Customer customer = new Customer(dto.name(), dto.username(), dto.password(), dto.address().toAddress());
    return userRepo.save(customer);
  }

  public Admin registerAdmin(CreateAdminDTO dto) {
    if (userRepo.findByUsername(dto.username()) != null) {
      throw new BusinessRuleException("Username " + dto.username() + " is already in use");
    }
    Admin admin = new Admin(dto.name(), dto.username(), dto.password());
    return userRepo.save(admin);
  }

  public Courier registerCourier(CreateCourierDTO dto) {

    if (userRepo.findByUsername(dto.username()) != null) {
        throw new BusinessRuleException(
            "Username " + dto.username() + " is already in use");
    }

    Courier courier = new Courier(
        dto.name(),
        dto.username(),
        dto.password());

    Courier saved = userRepo.save(courier);

    kafkaSender.sendCourierRegistered(
        new CourierDto(
            saved.getId(),
            saved.getName(),
            saved.getUsername()
        )
    );

    return saved;
  }

  // C. Gerir utilizadores: Verificar, remover e atualizar utilizadores.
  public boolean verifyUser(UUID id) {
    return userRepo.findById(id).isPresent();
  }

  public boolean removeUser(UUID id) {

    User user = userRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));

    if (user instanceof Courier courier) {
        kafkaSender.sendCourierRemoved(
            new CourierDto(
                courier.getId(),
                courier.getName(),
                courier.getUsername()
            )
        );
    }

    userRepo.delete(user);

    return !userRepo.findById(id).isPresent();
  }

  public User getUserById(UUID id) {
    return userRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("User", id));
  }

  public List<User> getAllUsers() {    
    return userRepo.findAll();
  }

  public List<Courier> getAvailableCouriers() {
    return userRepo.findAllAvailableCouriers();
  }

  public Admin updateAdmin(UUID id, UpdateAdminDTO dto) {
    User user = getUserById(id);

    if (user.getRole() != Role.ADMIN)
      throw new BusinessRuleException("User with id=" + id + " is not an Admin");

    Admin admin = (Admin) user;
    if (dto.name() != null) admin.setName(dto.name());
    if (dto.password() != null) admin.setPassword(dto.password());

    return userRepo.save(admin);
  }

  public Courier updateCourier(UUID id, UpdateCourierDTO dto) {
    User user = getUserById(id);

    if (user.getRole() != Role.COURIER)
      throw new BusinessRuleException("User with id=" + id + " is not a Courier");

    Courier courier = (Courier) user;
    if (dto.name() != null) courier.setName(dto.name());
    if (dto.password() != null) courier.setPassword(dto.password());

    Courier saved = userRepo.save(courier);

    kafkaSender.sendCourierUpdated(
      new CourierDto(
          saved.getId(),
          saved.getName(),
          saved.getUsername()
      )
    );

    return saved;
  }

  public Customer updateCustomer(UUID id, UpdateCustomerDTO dto) {
    User user = getUserById(id);

    if (user.getRole() != Role.CUSTOMER)
      throw new BusinessRuleException("User with id=" + id + " is not a Customer");

    Customer customer = (Customer) user;

    if (dto.name() != null) customer.setName(dto.name());
    if (dto.username() != null) {
      User existing = userRepo.findByUsername(dto.username());
      if (existing != null && !existing.getId().equals(id))
        throw new BusinessRuleException("Username " + dto.username() + " is already in use");
      customer.setUsername(dto.username());
    }
    if (dto.password() != null) customer.setPassword(dto.password());
    
    return userRepo.save(customer);
  }

  public Customer addAddress(UUID id, Address address) {
    User user = getUserById(id);

    if (user.getRole() != Role.CUSTOMER)
      throw new BusinessRuleException("User with id=" + id + " is not a Customer");

    Customer customer = (Customer) user;
    if (address != null) customer.addAddress(address);

    return userRepo.save(customer);
  }

  public Customer removeAddress(UUID customerId, int addressIndex) {
    User user = getUserById(customerId);

    if (user.getRole() != Role.CUSTOMER)
      throw new BusinessRuleException("User with id=" + customerId + " is not a Customer");

    Customer customer = (Customer) user;
    var addresses = new ArrayList<>(customer.getAddresses());
    
    if (addressIndex < 0 || addressIndex >= addresses.size())
      throw new BusinessRuleException("Address index out of bounds");
    
    customer.removeAddress(addresses.get(addressIndex));
    return userRepo.save(customer);
  }

  public List<Restaurant> getRestaurantsToRate(UUID customerId) {
    User user = getUserById(customerId);

    if (user.getRole() != Role.CUSTOMER)
      throw new BusinessRuleException("User with id=" + customerId + " is not a Customer");
    
    return ((Customer) user).getRestaurantsToRate();
  }

  
  public List<User> search(String nome, Role role, Integer nPedidos, Integer nEntregas) {

    if (nPedidos != null && nEntregas != null) {
      throw new BusinessRuleException("Cannot filter by both nPedidos and nEntregas");
    }
    if (nPedidos != null && role != null && role != Role.CUSTOMER) {
      throw new BusinessRuleException("nPedidos only applicable to Customers");
    }
    if (nEntregas != null && role != Role.COURIER && role != null) {
      throw new BusinessRuleException("nEntregas only applicable to Couriers");
    }

    return userRepo.findAll().stream()
      
      .filter(u -> nome == null || 
        (u.getName() != null && u.getName().toLowerCase().contains(nome.toLowerCase())))

      .filter(u -> role == null || 
        u.getRole() == role)

      .filter(u -> {
        if (nPedidos == null) return true;
        if (u instanceof Customer c) {
          return c.getOrderCount() >= nPedidos;
        }
        return false;
      })

      .filter(u -> {
        if (nEntregas == null) return true;
        if (u instanceof Courier c) {
          return c.getDeliveryCount() >= nEntregas;
        }
        return false;
      })
      
      .toList();
  }
}
