package pt.ul.fc.css.tascaeats.entities;

import jakarta.persistence.Entity;
import pt.ul.fc.css.tascaeats.enums.PaymentType;

@Entity
public class CashPayment extends Payment {

  private double change; // informação sobre o troco

  public CashPayment() {
    super(PaymentType.CASH);
  }

  public CashPayment(double change) {
    super(PaymentType.CASH);
    this.change = change;
  }

  public double getChange() {
    return change;
  }

  public void setChange(double change) {
    this.change = change;
  }
}