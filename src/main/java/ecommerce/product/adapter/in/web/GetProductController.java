package ecommerce.product.adapter.in.web;

import ecommerce.common.dto.ApiResponse;
import ecommerce.product.application.port.in.GetProductUseCase;
import ecommerce.product.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class GetProductController {

    private static final Logger log = LoggerFactory.getLogger(GetProductController.class);

    private final GetProductUseCase getProductUseCase;

    public GetProductController(GetProductUseCase getProductUseCase) {
        this.getProductUseCase = getProductUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> get(@PathVariable("id") Long id) {
        log.info("GET /products/{} 요청 수신", id);
        try {
            Product product = getProductUseCase.getProduct(id);
            ProductResponseDto response = new ProductResponseDto(
                product.getId(), product.getProductName(), product.getAmount(), product.getQuantity()
            );
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            log.warn("상품 조회 실패 (잘못된 요청) - {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("상품 조회 중 서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("서버 오류 발생"));
        }
    }
}


