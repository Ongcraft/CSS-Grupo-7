package pt.ul.fc.css.tascaeats.exception;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {

  public EntityNotFoundException(String type, UUID id) {
    super(String.format("%s not found (id=%s)", type, id));
  }
}
