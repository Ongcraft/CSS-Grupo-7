package pt.ul.fc.css.tascaeats.dtos.restaurant;

import com.fasterxml.jackson.annotation.JsonInclude;
import pt.ul.fc.css.tascaeats.dtos.common.AddressDTO;
import pt.ul.fc.css.tascaeats.entities.Restaurant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestaurantDTO(String id, double rating, String name, String nif, AddressDTO address, String menuId, Boolean open, String kitchenType) {

  public RestaurantDTO(Restaurant restaurant) {
    this(
        restaurant.getId().toString(),
        restaurant.getRating(),
        restaurant.getName(),
        restaurant.getNif(),
        new AddressDTO(restaurant.getAddress()),
        restaurant.getMenu() != null ? restaurant.getMenu().getId().toString() : null,
        restaurant.isOpen(),
        restaurant.getKitchenType());
  }
}
