package com.examp.springmvc.catalog.application.product.command;

import com.examp.springmvc.catalog.domain.model.Product;
import com.examp.springmvc.catalog.domain.ports.output.CategoryPersistencePort;
import com.examp.springmvc.catalog.domain.ports.output.ImageStoragePort;
import com.examp.springmvc.catalog.domain.ports.output.ProductPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductUseCase {
    private final ProductPersistencePort productPersistencePort;
    private final CategoryPersistencePort categoryPersistencePort;
    private final ImageStoragePort imageStoragePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CreateProductUseCase(
            ProductPersistencePort productPersistencePort,
            CategoryPersistencePort categoryPersistencePort,
            ImageStoragePort imageStoragePort) {
        this.productPersistencePort = productPersistencePort;
        this.categoryPersistencePort = categoryPersistencePort;
        this.imageStoragePort = imageStoragePort;
    }

    @Transactional
    public void execute(CreateProductCommand command) {
        if (!categoryPersistencePort.findById(command.getCategoryId()).isPresent()) {
            throw new IllegalArgumentException("Không tồn tại danh mục có ID: " + command.getCategoryId());
        }

        String cleanSku = command.getSku() != null ? command.getSku().trim().toUpperCase() : "";
        if (productPersistencePort.findBySku(cleanSku).isPresent()) {
            throw new IllegalArgumentException("Mã SKU '" + cleanSku + "' đã tồn tại!");
        }

        String imageUrl = null;
        if (command.getImageStream() != null) {
            imageUrl = imageStoragePort.upload(command.getImageStream(), command.getImageName());
        }

        Product product = new Product(
                null,
                command.getCategoryId(),
                command.getSku(),
                command.getName(),
                command.getDescription(),
                command.getPrice(),
                command.getStatus(),
                imageUrl);

        productPersistencePort.save(product);
    }
}
