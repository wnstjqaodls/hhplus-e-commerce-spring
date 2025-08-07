package ecommerce.product.adapter.in.web;

import ecommerce.product.application.port.in.GetProductListUseCase;
import ecommerce.product.domain.Product;
import ecommerce.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class GetProductListController {

    private static final Logger log = LoggerFactory.getLogger(GetProductListController.class);

    private final GetProductListUseCase getProductListUseCase;

    public GetProductListController(GetProductListUseCase getProductListUseCase) {
        this.getProductListUseCase = getProductListUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getProductList() {
        log.info("GET /products 요청 수신");

        try {
            List<Product> products = getProductListUseCase.getProductList();

            List<ProductResponseDto> responseDtos = products.stream()
                .map(product -> new ProductResponseDto(
                    product.getId(),
                    product.getProductName(),
                    product.getAmount(),
                    product.getQuantity()
                ))
                .collect(Collectors.toList());

            log.info("상품 목록 조회 성공: 상품 수={}", responseDtos.size());
            return ResponseEntity.ok(ApiResponse.success(responseDtos));

        } catch (Exception e) {
            log.error("상품 목록 조회 중 서버 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("서버 오류 발생"));
        }
    }
}
