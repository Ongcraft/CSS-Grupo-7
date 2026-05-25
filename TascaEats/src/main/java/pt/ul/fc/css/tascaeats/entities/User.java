package pt.ul.fc.css.tascaeats.entities;

import jakarta.persistence.*;
import java.util.UUID;
import pt.ul.fc.css.tascaeats.enums.Role;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  protected UUID id;

  @Column(nullable = false)
  protected String name;

  @Column(unique = true, nullable = false)
  protected String username;

  @Column(nullable = false)
  protected String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  protected Role role;

  /** Default constructor required by JPA. */
  protected User() {}

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getUsername() {
    return username;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Role getRole() {
    return role;
  }
}
