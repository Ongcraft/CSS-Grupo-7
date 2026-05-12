package pt.ul.fc.css.tascaeats.entities;

import jakarta.persistence.Entity;
import pt.ul.fc.css.tascaeats.enums.Role;

@Entity
public class Admin extends User {

  public Admin() {}

  public Admin(String name, String username, String password) {
    this.name = name;
    this.username = username;
    this.password = password;
    this.role = Role.ADMIN;
  }
}
