package ecommerce.product.adapter.in.web;

import ecommerce.common.dto.ApiResponse;
import ecommerce.product.application.port.in.CreateProductUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class CreateProductController {

    private static final Logger log = LoggerFactory.getLogger(CreateProductController.class);

    private final CreateProductUseCase createProductUseCase;

    public CreateProductController(CreateProductUseCase createProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        log.info("CreateProductController 생성자 실행 - CreateProductUseCase 주입 완료");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> create(@RequestBody CreateProductRequestDto request) {
        log.info("POST /products 요청 수신 - name: {}, amount: {}, quantity: {}", request.getProductName(), request.getAmount(), request.getQuantity());
        try {
            Long id = createProductUseCase.createProduct(request.getProductName(), request.getAmount(), request.getQuantity());
            ProductResponseDto response = new ProductResponseDto(id, request.getProductName(), request.getAmount(), request.getQuantity());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.warn("상품 생성 실패 (잘못된 요청) - {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("상품 생성 중 서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("서버 오류 발생"));
        }
    }
}


