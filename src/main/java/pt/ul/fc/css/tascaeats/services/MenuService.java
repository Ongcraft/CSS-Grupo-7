package pt.ul.fc.css.tascaeats.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.tascaeats.entities.Menu;
import pt.ul.fc.css.tascaeats.entities.Product;
import pt.ul.fc.css.tascaeats.entities.Restaurant;
import pt.ul.fc.css.tascaeats.exception.EntityNotFoundException;
import pt.ul.fc.css.tascaeats.repositories.MenuRepository;
import pt.ul.fc.css.tascaeats.repositories.ProductRepository;
import pt.ul.fc.css.tascaeats.repositories.RestaurantRepository;

@Service
@Transactional
public class MenuService {
    
    private MenuRepository menuRepo;
    private ProductRepository productRepo;
    private RestaurantRepository restaurantRepo;

    public MenuService(MenuRepository menuRepo, ProductRepository productRepo, RestaurantRepository restaurantRepo) {
        this.menuRepo = menuRepo;
        this.productRepo = productRepo;
        this.restaurantRepo = restaurantRepo;
    }

    public Menu createMenu(String name) {
        Menu menu = new Menu(name);
        return menuRepo.save(menu);
    }

    public Menu getMenubyId(UUID id) {
        return menuRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Menu", id));
    }

    public List<Menu> getAllMenus() {
        return menuRepo.findAll();
    }

    public Menu addProductToMenu(UUID id, UUID productId) { 
        Menu menu = getMenubyId(id);
        Product product = productRepo.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product", productId));

        menu.addProduct(product);
        return menuRepo.save(menu);
    }

    public Menu removeProductFromMenu(UUID id, UUID productId) {
        Menu menu = getMenubyId(id);
        Product product = productRepo.findById(productId).orElseThrow(() -> new EntityNotFoundException("Product", productId));

        menu.removeProduct(product);
        return menuRepo.save(menu);
    }

    public Menu addRestaurantToMenu(UUID id, UUID restaurantId) {
        Menu menu = getMenubyId(id);
        Restaurant restaurant = restaurantRepo.findById(restaurantId).orElseThrow(() -> new EntityNotFoundException("Restaurant", restaurantId));

        menu.addRestaurant(restaurant);
        restaurant.setMenu(menu);
        return menuRepo.save(menu);
    }

    public Menu removeRestaurantFromMenu(UUID id, UUID restaurantId) {
        Menu menu = getMenubyId(id);
        Restaurant restaurant = restaurantRepo.findById(restaurantId).orElseThrow(() -> new EntityNotFoundException("Restaurant", restaurantId));

        menu.removeRestaurant(restaurant);
        return menuRepo.save(menu);
    }

    public List<Menu> search(String nome, Integer nProdutos, Double preçoMedio) {
        
        return menuRepo.findAll().stream().

            filter(menu -> nome == null || 
                (nome != null && menu.getName().toLowerCase().contains(nome.toLowerCase()))).

            filter(menu -> nProdutos == null || menu.getNumberOfProducts() == nProdutos).

            filter(menu -> preçoMedio == null || menu.getAveragePrice() == preçoMedio)

            .toList();
    }
}
