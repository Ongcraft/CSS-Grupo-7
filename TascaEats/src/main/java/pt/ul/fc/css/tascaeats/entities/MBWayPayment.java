package pt.ul.fc.css.tascaeats.entities;

import jakarta.persistence.Entity;
import pt.ul.fc.css.tascaeats.enums.PaymentType;

@Entity
public class MBWayPayment extends Payment {

  private String phoneNumber; // número de telemóvel

  public MBWayPayment() {
    super(PaymentType.MBWAY);
  }

  public MBWayPayment(String phoneNumber) {
    super(PaymentType.MBWAY);
    this.phoneNumber = phoneNumber;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
}