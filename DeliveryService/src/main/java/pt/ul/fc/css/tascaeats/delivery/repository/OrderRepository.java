package pt.ul.fc.css.tascaeats.delivery.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.ul.fc.css.tascaeats.delivery.enums.DeliveryStatus;
import pt.ul.fc.css.tascaeats.delivery.model.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {

  List<Order> findByCourierId(UUID courierId);

  List<Order> findByStatus(DeliveryStatus status);
}
