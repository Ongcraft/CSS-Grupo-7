package pt.ul.fc.css.tascaeats.entities;

import jakarta.persistence.Entity;
import pt.ul.fc.css.tascaeats.enums.CardFlag;
import pt.ul.fc.css.tascaeats.enums.PaymentType;

@Entity
public class MultibancoPayment extends Payment {

  private CardFlag cardFlag; // bandeira do cartão

  public MultibancoPayment() {
    super(PaymentType.MULTIBANCO);
  }

  public MultibancoPayment(CardFlag cardFlag) {
    super(PaymentType.MULTIBANCO);
    this.cardFlag = cardFlag;
  }

  public CardFlag getCardFlag() {
    return cardFlag;
  }

  public void setCardFlag(CardFlag cardFlag) {
    this.cardFlag = cardFlag;
  }
}