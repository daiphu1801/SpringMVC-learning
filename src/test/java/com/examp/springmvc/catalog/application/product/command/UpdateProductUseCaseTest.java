package com.examp.springmvc.catalog.application.product.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

class UpdateProductUseCaseTest {

    private ProductPersistencePort productPersistencePort;
    private CategoryPersistencePort categoryPersistencePort;
    private ImageStoragePort imageStoragePort;
    private PlatformTransactionManager transactionManager;
    private UpdateProductUseCase updateProductUseCase;

    @BeforeEach
    void setUp() {
        productPersistencePort = mock(ProductPersistencePort.class);
        categoryPersistencePort = mock(CategoryPersistencePort.class);
        imageStoragePort = mock(ImageStoragePort.class);
        transactionManager = mock(PlatformTransactionManager.class);
        TransactionStatus transactionStatus = mock(TransactionStatus.class);
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);

        updateProductUseCase = new UpdateProductUseCase(
                productPersistencePort, categoryPersistencePort, imageStoragePort, transactionManager);
    }

    @Test
    void shouldUpdateProductWithNewImageSuccessfully() {
        Long productId = 1L;
        Long categoryId = 2L;
        String sku = "PROD1-NEW";
        String name = "Product Name New";
        String description = "Description New";
        BigDecimal price = new BigDecimal("120000");
        ProductStatus status = ProductStatus.ACTIVE;
        InputStream imageStream = new ByteArrayInputStream(new byte[] {4, 5, 6});
        String imageName = "new.png";

        Product existingProduct = new Product(
                productId,
                1L,
                "PROD1",
                "Old Name",
                "Old Desc",
                new BigDecimal("100000"),
                ProductStatus.ACTIVE,
                "http://cloudinary.com/old.png",
                100);

        Category mockCategory = mock(Category.class);
        when(productPersistencePort.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryPersistencePort.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(productPersistencePort.findBySku(sku)).thenReturn(Optional.empty());
        when(imageStoragePort.upload(imageStream, imageName)).thenReturn("http://cloudinary.com/new.png");

        UpdateProductCommand command = new UpdateProductCommand(
                productId, categoryId, sku, name, description, price, status, imageStream, imageName);

        updateProductUseCase.execute(command);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productPersistencePort).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();
        assertThat(savedProduct.getId()).isEqualTo(productId);
        assertThat(savedProduct.getCategoryId()).isEqualTo(categoryId);
        assertThat(savedProduct.getSku()).isEqualTo("PROD1-NEW");
        assertThat(savedProduct.getName()).isEqualTo(name);
        assertThat(savedProduct.getDescription()).isEqualTo(description);
        assertThat(savedProduct.getPrice()).isEqualTo(price);
        assertThat(savedProduct.getStatus()).isEqualTo(status);
        assertThat(savedProduct.getImageUrl()).isEqualTo("http://cloudinary.com/new.png");
    }

    @Test
    void shouldKeepExistingImageWhenNoNewImageUploaded() {
        Long productId = 1L;
        Long categoryId = 2L;
        String sku = "PROD1-NEW";
        String name = "Product Name New";
        String description = "Description New";
        BigDecimal price = new BigDecimal("120000");
        ProductStatus status = ProductStatus.ACTIVE;

        Product existingProduct = new Product(
                productId,
                1L,
                "PROD1",
                "Old Name",
                "Old Desc",
                new BigDecimal("100000"),
                ProductStatus.ACTIVE,
                "http://cloudinary.com/old.png",
                100);

        Category mockCategory = mock(Category.class);
        when(productPersistencePort.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryPersistencePort.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(productPersistencePort.findBySku(sku)).thenReturn(Optional.empty());

        UpdateProductCommand command =
                new UpdateProductCommand(productId, categoryId, sku, name, description, price, status, null, null);

        updateProductUseCase.execute(command);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productPersistencePort).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();
        assertThat(savedProduct.getId()).isEqualTo(productId);
        assertThat(savedProduct.getImageUrl()).isEqualTo("http://cloudinary.com/old.png");
    }

    @Test
    void shouldDeleteNewUploadedImageWhenDatabaseUpdateFails() {
        Long productId = 1L;
        Long categoryId = 2L;
        String sku = "PROD1-NEW";
        String name = "Product Name New";
        String description = "Description New";
        BigDecimal price = new BigDecimal("120000");
        ProductStatus status = ProductStatus.ACTIVE;
        InputStream imageStream = new ByteArrayInputStream(new byte[] {4, 5, 6});
        String imageName = "new.png";
        String newUploadedUrl = "http://cloudinary.com/new.png";

        Product existingProduct = new Product(
                productId,
                1L,
                "PROD1",
                "Old Name",
                "Old Desc",
                new BigDecimal("100000"),
                ProductStatus.ACTIVE,
                "http://cloudinary.com/old.png",
                100);

        Category mockCategory = mock(Category.class);
        when(productPersistencePort.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryPersistencePort.findById(categoryId)).thenReturn(Optional.of(mockCategory));
        when(productPersistencePort.findBySku(sku)).thenReturn(Optional.empty());
        when(imageStoragePort.upload(imageStream, imageName)).thenReturn(newUploadedUrl);

        // Cấu hình lưu DB bị lỗi để kích hoạt rollback dọn ảnh
        doThrow(new RuntimeException("Database update error"))
                .when(productPersistencePort)
                .save(any(Product.class));

        UpdateProductCommand command = new UpdateProductCommand(
                productId, categoryId, sku, name, description, price, status, imageStream, imageName);

        assertThrows(RuntimeException.class, () -> updateProductUseCase.execute(command));

        // Xác nhận đã gọi xóa ảnh mới vừa upload
        verify(imageStoragePort).delete(newUploadedUrl);
    }
}
