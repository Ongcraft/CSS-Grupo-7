package pt.ul.fc.css.tascaeats.dtos.menu;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import pt.ul.fc.css.tascaeats.entities.Menu;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MenuDTO(
    UUID id,
    String name,
    List<UUID> productIds,
    List<UUID> restaurantIds
) {
    public MenuDTO(Menu menu) {
        this(
            menu.getId(),
            menu.getName(),
            menu.getProducts().stream().map(p -> p.getId()).toList(),
            menu.getRestaurants().stream().map(r -> r.getId()).toList()
        );
    }

    public static MenuDTO from(Menu menu) {
        return new MenuDTO(menu);
    }
}