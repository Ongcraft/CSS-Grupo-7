package pt.ul.fc.css.tascaeats.dtos.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import pt.ul.fc.css.tascaeats.entities.Product;
import pt.ul.fc.css.tascaeats.enums.FoodCategory;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductDTO(
    String id, String name, String description, Double price, Boolean available, FoodCategory category, Integer popularity) {

  public ProductDTO(Product product) {
    this(
        product.getId().toString(),
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        product.isAvailable(),
        product.getCategory(),
        null);
  }

  public ProductDTO(Product product, int popularity) {
    this(
        product.getId().toString(),
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        product.isAvailable(),
        product.getCategory(),
        popularity);
  }

  public static ProductDTO from(Product product) {
    return new ProductDTO(product);
  }

  public static ProductDTO from(Product product, int popularity) {
    return new ProductDTO(product, popularity);
  }
}
