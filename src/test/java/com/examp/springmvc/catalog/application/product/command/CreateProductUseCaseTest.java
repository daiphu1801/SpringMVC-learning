package com.examp.springmvc.catalog.application.product.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.examp.springmvc.catalog.domain.model.Category;
import com.examp.springmvc.catalog.domain.model.Product;
import com.examp.springmvc.catalog.domain.model.ProductStatus;
import com.examp.springmvc.catalog.domain.ports.output.CategoryPersistencePort;
import com.examp.springmvc.catalog.domain.ports.output.ImageStoragePort;
import com.examp.springmvc.catalog.domain.ports.output.ProductPersistencePort;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CreateProductUseCaseTest {

    private ProductPersistencePort productPersistencePort;
    private CategoryPersistencePort categoryPersistencePort;
    private ImageStoragePort imageStoragePort;
    private CreateProductUseCase createProductUseCase;

    @BeforeEach
    void setUp() {
        productPersistencePort = mock(ProductPersistencePort.class);
        categoryPersistencePort = mock(CategoryPersistencePort.class);
        imageStoragePort = mock(ImageStoragePort.class);
        createProductUseCase =
                new CreateProductUseCase(productPersistencePort, categoryPersistencePort, imageStoragePort);
    }

    @Test
    void shouldCreateProductWithImageSuccessfully() {
        Long categoryId = 1L;
        String sku = "PROD1";
        String name = "Product Name";
        String description = "Description";
        BigDecimal price = new BigDecimal("100000");
        ProductStatus status = ProductStatus.ACTIVE;
        InputStream imageStream = new ByteArrayInputStream(new byte[] {1, 2, 3});
        String imageName = "test.png";

        Category mockCategory = mock(Category.class);
        when(categoryPersistencePort.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(productPersistencePort.findBySku(sku)).thenReturn(Optional.empty());
        when(imageStoragePort.upload(imageStream, imageName)).thenReturn("http://cloudinary.com/test.png");

        CreateProductCommand command =
                new CreateProductCommand(categoryId, sku, name, description, price, status, imageStream, imageName);

        createProductUseCase.execute(command);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productPersistencePort).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();
        assertThat(savedProduct.getCategoryId()).isEqualTo(categoryId);
        assertThat(savedProduct.getSku()).isEqualTo("PROD1");
        assertThat(savedProduct.getName()).isEqualTo(name);
        assertThat(savedProduct.getDescription()).isEqualTo(description);
        assertThat(savedProduct.getPrice()).isEqualTo(price);
        assertThat(savedProduct.getStatus()).isEqualTo(status);
        assertThat(savedProduct.getImageUrl()).isEqualTo("http://cloudinary.com/test.png");
    }
}
