package ecommerce.product.adapter.out.persistence;

import ecommerce.product.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductPersistenceAdapter 테스트")
class ProductPersistenceAdapterTest {

    @InjectMocks
    ProductPersistenceAdapter productPersistenceAdapter;

    @Mock
    ProductRepository productRepository;

    @Test
    @DisplayName("상품 목록 조회에 성공한다")
    void loadProductListSuccess() {
        // given
        ProductJpaEntity entity1 = createProductJpaEntity(1L, "상품1", 10000L, 100);
        ProductJpaEntity entity2 = createProductJpaEntity(2L, "상품2", 20000L, 50);
        List<ProductJpaEntity> entities = Arrays.asList(entity1, entity2);

        when(productRepository.findAll()).thenReturn(entities);

        // when
        List<Product> result = productPersistenceAdapter.loadProductList();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getProductName()).isEqualTo("상품1");
        assertThat(result.get(0).getAmount()).isEqualTo(10000L);
        assertThat(result.get(0).getQuantity()).isEqualTo(100);
        
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getProductName()).isEqualTo("상품2");
        assertThat(result.get(1).getAmount()).isEqualTo(20000L);
        assertThat(result.get(1).getQuantity()).isEqualTo(50);
    }

    @Test
    @DisplayName("상품 목록이 비어있을 때 빈 리스트를 반환한다")
    void loadProductListEmpty() {
        // given
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<Product> result = productPersistenceAdapter.loadProductList();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("상품 조회에 성공한다")
    void loadProductSuccess() {
        // given
        Long productId = 1L;
        ProductJpaEntity entity = createProductJpaEntity(productId, "테스트상품", 15000L, 200);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(entity));

        // when
        Product result = productPersistenceAdapter.loadProduct(productId);

        // then
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getProductName()).isEqualTo("테스트상품");
        assertThat(result.getAmount()).isEqualTo(15000L);
        assertThat(result.getQuantity()).isEqualTo(200);
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 예외가 발생한다")
    void loadProductNotFound() {
        // given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productPersistenceAdapter.loadProduct(productId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품을 찾을 수 없습니다. id=" + productId);
    }

    @Test
    @DisplayName("새로운 상품 저장에 성공한다")
    void saveNewProductSuccess() {
        // given
        Product product = new Product(null, "새상품", 25000L, 150);
        ProductJpaEntity savedEntity = createProductJpaEntity(1L, "새상품", 25000L, 150);
        
        when(productRepository.save(any(ProductJpaEntity.class))).thenReturn(savedEntity);

        // when
        Product result = productPersistenceAdapter.saveProduct(product);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getProductName()).isEqualTo("새상품");
        assertThat(result.getAmount()).isEqualTo(25000L);
        assertThat(result.getQuantity()).isEqualTo(150);
    }

    @Test
    @DisplayName("기존 상품 업데이트에 성공한다")
    void updateExistingProductSuccess() {
        // given
        Product product = new Product(1L, "수정된상품", 30000L, 80);
        ProductJpaEntity savedEntity = createProductJpaEntity(1L, "수정된상품", 30000L, 80);
        
        when(productRepository.save(any(ProductJpaEntity.class))).thenReturn(savedEntity);

        // when
        Product result = productPersistenceAdapter.saveProduct(product);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getProductName()).isEqualTo("수정된상품");
        assertThat(result.getAmount()).isEqualTo(30000L);
        assertThat(result.getQuantity()).isEqualTo(80);
    }

    @Test
    @DisplayName("Entity에서 Domain으로 변환이 정확히 수행된다")
    void toDomainConversion() {
        // given
        ProductJpaEntity entity = createProductJpaEntity(1L, "변환테스트", 12000L, 300);
        when(productRepository.findById(1L)).thenReturn(Optional.of(entity));

        // when
        Product result = productPersistenceAdapter.loadProduct(1L);

        // then
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getProductName()).isEqualTo(entity.getProductName());
        assertThat(result.getAmount()).isEqualTo(entity.getAmount());
        assertThat(result.getQuantity()).isEqualTo(entity.getQuantity());
    }

    @Test
    @DisplayName("Domain에서 Entity로 변환이 정확히 수행된다")
    void toEntityConversion() {
        // given
        Product product = new Product(2L, "변환테스트2", 8000L, 500);
        ProductJpaEntity expectedEntity = createProductJpaEntity(2L, "변환테스트2", 8000L, 500);
        
        when(productRepository.save(any(ProductJpaEntity.class))).thenReturn(expectedEntity);

        // when
        Product result = productPersistenceAdapter.saveProduct(product);

        // then
        assertThat(result.getId()).isEqualTo(product.getId());
        assertThat(result.getProductName()).isEqualTo(product.getProductName());
        assertThat(result.getAmount()).isEqualTo(product.getAmount());
        assertThat(result.getQuantity()).isEqualTo(product.getQuantity());
    }

    private ProductJpaEntity createProductJpaEntity(Long id, String productName, Long amount, int quantity) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(id);
        entity.setProductName(productName);
        entity.setAmount(amount);
        entity.setQuantity(quantity);
        return entity;
    }
}