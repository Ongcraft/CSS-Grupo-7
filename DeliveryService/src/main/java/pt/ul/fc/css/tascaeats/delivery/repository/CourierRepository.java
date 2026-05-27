package pt.ul.fc.css.tascaeats.delivery.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.ul.fc.css.tascaeats.delivery.model.Courier;

public interface CourierRepository extends JpaRepository<Courier, UUID> {

  Optional<Courier> findFirstByAvailableTrue();
}
