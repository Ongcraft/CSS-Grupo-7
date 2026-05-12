package pt.ul.fc.css.tascaeats.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.NotBlank;
import pt.ul.fc.css.tascaeats.entities.Courier;
import pt.ul.fc.css.tascaeats.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  // Obter todos os entregadores disponíveis
  @Query("SELECT d FROM Courier d WHERE d.availability = true")
  List<Courier> findAllAvailableCouriers();

  @Query("SELECT u FROM User u WHERE u.username = :username")
  User findByUsername(@NotBlank String username);
}
