package pt.ul.fc.css.tascaeats.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ul.fc.css.tascaeats.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

  boolean existsByOrderItemsProductId(UUID productId);
}
