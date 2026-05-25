package pt.ul.fc.css.tascaeats.services;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

import org.springframework.boot.autoconfigure.integration.IntegrationProperties.RSocket.Client;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.tascaeats.dtos.product.CreateProductDTO;
import pt.ul.fc.css.tascaeats.dtos.product.UpdateProductDTO;
import pt.ul.fc.css.tascaeats.entities.Product;
import pt.ul.fc.css.tascaeats.enums.FoodCategory;
import pt.ul.fc.css.tascaeats.exception.BusinessRuleException;
import pt.ul.fc.css.tascaeats.exception.EntityNotFoundException;
import pt.ul.fc.css.tascaeats.repositories.OrderItemRepository;
import pt.ul.fc.css.tascaeats.repositories.ProductRepository;
import pt.ul.fc.css.tascaeats.repositories.UserRepository;
import pt.ul.fc.css.tascaeats.entities.User;
import pt.ul.fc.css.tascaeats.entities.Customer;

@Service
@Transactional
public class ProductService {
    private ProductRepository productRepo;
    private OrderItemRepository orderItemRepo;
    private UserRepository userRepo;

    public ProductService(ProductRepository productRepo, OrderItemRepository orderItemRepo, UserRepository userRepo) {
        this.productRepo = productRepo;
        this.orderItemRepo = orderItemRepo;
        this.userRepo = userRepo;
    }

    public Product getProductById(UUID id) {
        return productRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Product", id));
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product createProduct(CreateProductDTO dto) {        
        Product product = new Product(dto.name(), dto.description(), dto.price(), dto.category());
        if (dto.available() != null && !dto.available()) product.deactivate();       

        return productRepo.save(product);
    }
 
    public Product updateProduct(UUID productId, UpdateProductDTO dto) {
        Product product = getProductById(productId);
        product.update(dto.name(), dto.description(), dto.price(), dto.available(), dto.category());
        return productRepo.save(product);
    }
    
    public int calculatePopularity(UUID productId, Integer secondsBack) {
        if (secondsBack == null) {
            secondsBack = 60 * 5; // Default: last 5 minutes
        }
        LocalDateTime startDate = LocalDateTime.now().minusSeconds(secondsBack);
        LocalDateTime endDate = LocalDateTime.now();
        return (int) orderItemRepo.popularityByProductIdAndDateRange(productId, startDate, endDate);
    }

    public List<Product> search(UUID userId, String nome, Double preco, FoodCategory categoria, Boolean disponibilidade, Integer popularidade, Integer timeFrameSeconds) {

    User user = userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
    if (user instanceof Customer && disponibilidade != null) {
      throw new BusinessRuleException("Customer cannot filter product by availability");
    }

    return productRepo.findAll().stream()
      
      .filter(u -> nome == null || 
        (u.getName() != null && u.getName().toLowerCase().contains(nome.toLowerCase())))

      .filter(u -> preco == null || 
        u.getPrice() >= preco)

      .filter(u -> categoria == null ||
        u.getCategory() == categoria)

      .filter(u -> disponibilidade == null ||
        u.isAvailable() == disponibilidade)

      .filter(u -> popularidade == null ||
        calculatePopularity(u.getId(), timeFrameSeconds) >= popularidade)
      
      .toList();
  }
    
}
