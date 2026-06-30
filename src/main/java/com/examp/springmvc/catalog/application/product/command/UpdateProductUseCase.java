package com.examp.springmvc.catalog.application.product.command;

import com.examp.springmvc.catalog.domain.model.Product;
import com.examp.springmvc.catalog.domain.ports.output.CategoryPersistencePort;
import com.examp.springmvc.catalog.domain.ports.output.ImageStoragePort;
import com.examp.springmvc.catalog.domain.ports.output.ProductPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class UpdateProductUseCase {
    private final ProductPersistencePort productPersistencePort;
    private final CategoryPersistencePort categoryPersistencePort;
    private final ImageStoragePort imageStoragePort;
    private final TransactionTemplate transactionTemplate;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public UpdateProductUseCase(
            ProductPersistencePort productPersistencePort,
            CategoryPersistencePort categoryPersistencePort,
            ImageStoragePort imageStoragePort,
            PlatformTransactionManager transactionManager) {
        this.productPersistencePort = productPersistencePort;
        this.categoryPersistencePort = categoryPersistencePort;
        this.imageStoragePort = imageStoragePort;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

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

        String newImageUrl = null;
        boolean uploadedNewImage = false;
        if (command.getImageStream() != null) {
            newImageUrl = imageStoragePort.upload(command.getImageStream(), command.getImageName());
            uploadedNewImage = true;
        }

        final String finalImageUrl = uploadedNewImage ? newImageUrl : existingProduct.getImageUrl();
        final String finalNewImageUrl = newImageUrl;
        try {
            transactionTemplate.executeWithoutResult(status -> {
                existingProduct.updateDetails(
                        command.getCategoryId(),
                        command.getSku(),
                        command.getName(),
                        command.getDescription(),
                        command.getPrice(),
                        command.getStatus(),
                        finalImageUrl,
                        existingProduct.getStock());

                productPersistencePort.save(existingProduct);
            });
        } catch (Exception e) {
            if (finalNewImageUrl != null) {
                imageStoragePort.delete(finalNewImageUrl);
            }
            throw e;
        }
    }
}
