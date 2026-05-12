package pt.ul.fc.css.tascaeats.entities;

import java.util.UUID;

import jakarta.persistence.*;
import pt.ul.fc.css.tascaeats.enums.PaymentType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  private PaymentType paymentType;

  public Payment() {} // obrigatório para JPA

  public Payment(PaymentType paymentType) {
    this.paymentType = paymentType;
  }

  public PaymentType getPaymentType() {
    return paymentType;
  }
}
