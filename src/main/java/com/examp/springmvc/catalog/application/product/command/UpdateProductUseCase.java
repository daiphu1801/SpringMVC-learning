package com.examp.springmvc.catalog.application.product.command;

import com.examp.springmvc.catalog.domain.model.Product;
import com.examp.springmvc.catalog.domain.ports.output.CategoryPersistencePort;
import com.examp.springmvc.catalog.domain.ports.output.ImageStoragePort;
import com.examp.springmvc.catalog.domain.ports.output.ProductPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProductUseCase {
    private final ProductPersistencePort productPersistencePort;
    private final CategoryPersistencePort categoryPersistencePort;
    private final ImageStoragePort imageStoragePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public UpdateProductUseCase(
            ProductPersistencePort productPersistencePort,
            CategoryPersistencePort categoryPersistencePort,
            ImageStoragePort imageStoragePort) {
        this.productPersistencePort = productPersistencePort;
        this.categoryPersistencePort = categoryPersistencePort;
        this.imageStoragePort = imageStoragePort;
    }

    @Transactional
    public void execute(UpdateProductCommand command) {
        Product existingProduct = productPersistencePort
                .findById(command.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm có ID: " + command.getId()));

        if (!categoryPersistencePort.findById(command.getCategoryId()).isPresent()) {
            throw new IllegalArgumentException("Không tồn tại danh mục có ID: " + command.getCategoryId());
        }

        String cleanSku = command.getSku() != null ? command.getSku().trim().toUpperCase() : "";
        Optional<Product> productWithSku = productPersistencePort.findBySku(cleanSku);
        if (productWithSku.isPresent() && !productWithSku.get().getId().equals(command.getId())) {
            throw new IllegalArgumentException("Mã SKU '" + cleanSku + "' đã được sử dụng bởi sản phẩm khác!");
        }

        String imageUrl = existingProduct.getImageUrl();
        if (command.getImageStream() != null) {
            imageUrl = imageStoragePort.upload(command.getImageStream(), command.getImageName());
        }

        Product updatedProduct = new Product(
                existingProduct.getId(),
                command.getCategoryId(),
                command.getSku(),
                command.getName(),
                command.getDescription(),
                command.getPrice(),
                command.getStatus(),
                imageUrl);

        productPersistencePort.save(updatedProduct);
    }
}
