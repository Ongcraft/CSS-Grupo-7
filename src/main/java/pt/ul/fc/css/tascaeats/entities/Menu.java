package pt.ul.fc.css.tascaeats.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Menu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String name;

    @OneToMany 
    @JoinTable(name = "menu_restaurant")
    private List<Restaurant> restaurants = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "menu_product")
    private List<Product> products = new ArrayList<>();

    protected Menu() {}

    public Menu(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Restaurant> getRestaurants() {
        return List.copyOf(restaurants);
    }

    public void addRestaurant(Restaurant restaurant) {
        if (!restaurants.contains(restaurant)) {
            restaurants.add(restaurant);
        }
    }

    public void removeRestaurant(Restaurant restaurant) {
        restaurants.remove(restaurant);
    }

    
    public List<Product> getProducts() {
        return List.copyOf(products);
    }

    public void addProduct(Product product) {
        if (!products.contains(product)) {
            products.add(product);
        }
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }

    //adicionados para o filtro
    public int getNumberOfProducts() {
        return products.size();
    }

    public double getAveragePrice() {
        if (products.isEmpty()) return 0;
        return products.stream().mapToDouble(p -> p.getPrice()).average().orElse(0);
    }
}
