package pt.ul.fc.css.tascaeats.grpc;

import java.util.List;
import java.util.UUID;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import pt.ul.fc.css.tascaeats.dtos.product.CreateProductDTO;
import pt.ul.fc.css.tascaeats.dtos.product.UpdateProductDTO;
import pt.ul.fc.css.tascaeats.entities.Product;
import pt.ul.fc.css.tascaeats.enums.FoodCategory;
import pt.ul.fc.css.tascaeats.services.ProductService;

@GrpcService
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;

    public ProductGrpcService(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void createProduct(CreateProductRequest request,
                              StreamObserver<ProductResponse> responseObserver) {
        try {
            CreateProductDTO dto = new CreateProductDTO(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getAvailable(),
                request.getCategory().isBlank() ? null : FoodCategory.valueOf(request.getCategory()));

            Product product = productService.createProduct(dto);

            responseObserver.onNext(toGrpcProductResponse(product));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void updateProduct(UpdateProductRequest request,
                              StreamObserver<ProductResponse> responseObserver) {
        try {
            UpdateProductDTO dto = new UpdateProductDTO(
                    request.getName(),
                    request.getDescription(),
                    request.getPrice(),
                    request.getAvailable(),
                    request.getCategory().isBlank() ? null : FoodCategory.valueOf(request.getCategory())
                );

            Product product = productService.updateProduct(
                    UUID.fromString(request.getProductId()),
                    dto
            );

            responseObserver.onNext(toGrpcProductResponse(product));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getProductById(ProductIdRequest request,
                               StreamObserver<ProductResponse> responseObserver) {
        try {
            Product product = productService.getProductById(
                    UUID.fromString(request.getProductId())
            );

            responseObserver.onNext(toGrpcProductResponse(product));
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getProductsByFilter(ProductFilterRequest request,
                                    StreamObserver<ProductListResponse> responseObserver) {
        try {
            List<Product> products = productService.getAllProducts();

            ProductListResponse.Builder response = ProductListResponse.newBuilder();

            for (Product product : products) {
                response.addProducts(toGrpcProductResponse(product));
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    private ProductResponse toGrpcProductResponse(Product product) {
        return ProductResponse.newBuilder()
                .setId(product.getId().toString())
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setPrice(product.getPrice())
                .setCategory(product.getCategory().toString())
                .setAvailable(product.isAvailable())
                .build();
    }
}