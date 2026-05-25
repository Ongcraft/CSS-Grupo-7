package pt.ul.fc.css.tascaeats.controllers;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import pt.ul.fc.css.tascaeats.dtos.menu.CreateMenuDTO;
import pt.ul.fc.css.tascaeats.dtos.menu.MenuDTO;
import pt.ul.fc.css.tascaeats.entities.Menu;
import pt.ul.fc.css.tascaeats.services.MenuService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;




@Validated
@RestController
@RequestMapping("/menus")
@Tag(name = "Menus", description = "Operations related to menus lifecycle")
public class MenuController {
    
    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // -------------------- CREATE --------------------

    @PostMapping()
    @Operation(summary = "Create a new menu")
    public ResponseEntity<MenuDTO> createMenu(@Valid @RequestBody CreateMenuDTO dto) {
        Menu created = menuService.createMenu(dto.name());
        MenuDTO res = new MenuDTO(created);
        return ResponseEntity.created(URI.create("/menus/" + created.getId())).body(res);
    }

    // -------------------- READ --------------------

    @GetMapping("/{menuId}")
    @Operation(summary = "Get a menu by ID")
    public ResponseEntity<MenuDTO> getMenu(@PathVariable UUID menuId) {
        Menu found = menuService.getMenubyId(menuId);
        MenuDTO res = new MenuDTO(found);
        return ResponseEntity.ok(res);
    }

    @GetMapping()
    @Operation(summary = "Get all menus")
    public ResponseEntity<List<MenuDTO>> getAllMenus() {
        List<Menu> results = menuService.getAllMenus();
        List<MenuDTO> dtoList = results.stream().map(m -> new MenuDTO(m)).toList();
        return ResponseEntity.ok(dtoList);
    }

    // -------------------- MENU MODIFICATION --------------------
        // Product
    @PostMapping("/{menuId}/products/{productId}")
    @Operation(summary = "Add product to menu")
    public ResponseEntity<MenuDTO> addProductToMenu(
            @PathVariable UUID menuId, @PathVariable UUID productId) {
        Menu menu = menuService.addProductToMenu(menuId, productId);
        MenuDTO res = new MenuDTO(menu);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{menuId}/products/{productId}")
    @Operation(summary = "Remove product from menu")
    public ResponseEntity<MenuDTO> removeProductFromMenu(
            @PathVariable UUID menuId, @PathVariable UUID productId) {
        Menu menu = menuService.removeProductFromMenu(menuId, productId);
        MenuDTO res = new MenuDTO(menu);
        return ResponseEntity.ok(res);
    }

        // Restaurant
    @PostMapping("/{menuId}/restaurants/{restaurantId}")
    @Operation(summary = "Assign restaurant to menu")
    public ResponseEntity<MenuDTO> addRestaurantToMenu(
            @PathVariable UUID menuId, @PathVariable UUID restaurantId) {
        Menu menu = menuService.addRestaurantToMenu(menuId, restaurantId);
        MenuDTO res = new MenuDTO(menu);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{menuId}/restaurants/{restaurantId}")
    @Operation(summary = "Remove restaurant assigned to menu")
    public ResponseEntity<MenuDTO> removeRestaurantFromMenu(
            @PathVariable UUID menuId, @PathVariable UUID restaurantId) {
        Menu menu = menuService.removeRestaurantFromMenu(menuId, restaurantId);
        MenuDTO res = new MenuDTO(menu);
        return ResponseEntity.ok(res);
    }

    // -------------------- MENU FILTERS --------------------

  @GetMapping("/filter")
  @Operation(summary = "Search menus")
  public ResponseEntity<List<MenuDTO>> searchMenus(
      @RequestParam(required = false) String nome,
      @RequestParam(required = false) Integer nProdutos, 
      @RequestParam(required = false) Double preçoMedio) {

      List<Menu> found = menuService.search(nome, nProdutos, preçoMedio);
      List<MenuDTO> res = found.stream().map(MenuDTO::from).toList();
      return ResponseEntity.ok(res);
  }
    
}