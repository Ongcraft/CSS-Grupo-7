package pt.ul.fc.css.tascaeats.controllers;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import pt.ul.fc.css.tascaeats.dtos.product.CreateProductDTO;
import pt.ul.fc.css.tascaeats.dtos.product.ProductDTO;
import pt.ul.fc.css.tascaeats.dtos.product.UpdateProductDTO;
import pt.ul.fc.css.tascaeats.entities.Product;
import pt.ul.fc.css.tascaeats.enums.FoodCategory;
import pt.ul.fc.css.tascaeats.services.ProductService;

@Validated
@RestController
@RequestMapping("/products")
@Tag(name = "Product", description = "Operations related to product lifecycle")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // -------------------- CREATE --------------------

    @PostMapping()
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody CreateProductDTO dto) {
        Product created = productService.createProduct(dto);
        ProductDTO res = new ProductDTO(created);
        return ResponseEntity.created(URI.create("/products/" + created.getId())).body(res);
    }

    // -------------------- READ --------------------

    @GetMapping("/{productId}")
    @Operation(summary = "Get a product by ID")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable UUID productId) {
        Product found = productService.getProductById(productId);
        ProductDTO res = new ProductDTO(found);
        return ResponseEntity.ok(res);
    }

    @GetMapping()
    @Operation(summary = "Get all products")
    public ResponseEntity<List<ProductDTO>> getProduct() {
        List<Product> results = productService.getAllProducts();
        List<ProductDTO> dtoList = results.stream().map(m -> new ProductDTO(m)).toList();
        return ResponseEntity.ok(dtoList);
    }

    // -------------------- PRODUCT MODIFICATION --------------------

    @PostMapping("/{productId}")
    @Operation(summary = "Update product")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable UUID productId, @RequestBody UpdateProductDTO dto) {
        Product updated = productService.updateProduct(productId, dto);
        ProductDTO res = new ProductDTO(updated);
        return ResponseEntity.ok(res);
    }

      // -------------------- PRODUCT FILTERS --------------------

    @GetMapping("/filter")
    @Operation(summary = "Search poducts")
    public ResponseEntity<List<ProductDTO>> searchProducts(
        @RequestParam(required = true) UUID userId,
        @RequestParam(required = false) String nome, 
        @RequestParam(required = false) Double preco, 
        @RequestParam(required = false) FoodCategory categoria, 
        @RequestParam(required = false) Boolean disponibilidade,
        @RequestParam(required = false) Integer popularidade,
        @RequestParam(required = false) Integer timeFrameSeconds) {

        List<Product> found = productService.search(userId, nome, preco, categoria, disponibilidade, popularidade, timeFrameSeconds);
        List<ProductDTO> res = found.stream().map(ProductDTO::from).toList();
        return ResponseEntity.ok(res);
    }
}
