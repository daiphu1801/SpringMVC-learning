package com.examp.springmvc.catalog.application.product.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examp.springmvc.catalog.domain.model.ProductStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductQueryUseCasesTest {

    @Mock
    private ProductQueryPort productQueryPort;

    @InjectMocks
    private FindAllProductsUseCase findAllProductsUseCase;

    @InjectMocks
    private FindProductByIdUseCase findProductByIdUseCase;

    private ProductDTO testProductDTO(Long id, String name) {
        return new ProductDTO(
                id,
                10L,
                "Category 10",
                "SKU-" + id,
                name,
                "Description " + name,
                BigDecimal.valueOf(100.0),
                ProductStatus.ACTIVE,
                "image.png");
    }

    @Test
    @DisplayName("Should return all products")
    void shouldReturnAllProducts() {
        List<ProductDTO> dtos = List.of(testProductDTO(1L, "Prod 1"), testProductDTO(2L, "Prod 2"));
        when(productQueryPort.findAll()).thenReturn(dtos);

        List<ProductDTO> result = findAllProductsUseCase.execute();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Prod 1", result.get(0).getName());
        verify(productQueryPort).findAll();
    }

    @Test
    @DisplayName("Should return products by category ID")
    void shouldReturnProductsByCategoryId() {
        List<ProductDTO> dtos = List.of(testProductDTO(1L, "Prod 1"));
        when(productQueryPort.findByCategoryId(10L)).thenReturn(dtos);

        List<ProductDTO> result = findAllProductsUseCase.executeByCategoryId(10L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Prod 1", result.get(0).getName());
        verify(productQueryPort).findByCategoryId(10L);
    }

    @Test
    @DisplayName("Should return product by ID when found")
    void shouldReturnProductByIdWhenFound() {
        ProductDTO dto = testProductDTO(1L, "Prod 1");
        when(productQueryPort.findById(1L)).thenReturn(Optional.of(dto));

        ProductDTO result = findProductByIdUseCase.execute(1L);

        assertNotNull(result);
        assertEquals("Prod 1", result.getName());
        verify(productQueryPort).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void shouldThrowExceptionWhenProductNotFound() {
        when(productQueryPort.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> findProductByIdUseCase.execute(99L));

        assertEquals("Không tìm thấy sản phẩm có ID: 99", exception.getMessage());
    }
}
