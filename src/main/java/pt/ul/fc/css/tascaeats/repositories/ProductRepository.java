package pt.ul.fc.css.tascaeats.repositories;

// import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ul.fc.css.tascaeats.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

  // List<Product> findByRestaurantId(UUID restaurantId);

  // List<Product> findByRestaurantIdAndNameContainingIgnoreCase(UUID restaurantId, String name);

  // List<Product> findByRestaurantIdAndAvailable(UUID restaurantId, boolean available);

  // List<Product> findByRestaurantIdAndNameContainingIgnoreCaseAndAvailable(
  //     UUID restaurantId, String name, boolean available);

  // boolean existsByRestaurantIdAndNameIgnoreCase(UUID restaurantId, String name);
}
