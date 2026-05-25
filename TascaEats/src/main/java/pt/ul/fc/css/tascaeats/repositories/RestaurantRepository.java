package pt.ul.fc.css.tascaeats.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ul.fc.css.tascaeats.entities.Restaurant;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

  List<Restaurant> findByNameContainingIgnoreCase(String name);

  List<Restaurant> findByAddressCityIgnoreCase(String city);

  List<Restaurant> findByNameContainingIgnoreCaseAndAddressCityIgnoreCase(String name, String city);
}
