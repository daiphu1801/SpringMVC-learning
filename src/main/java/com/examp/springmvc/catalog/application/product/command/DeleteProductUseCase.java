package com.examp.springmvc.catalog.application.product.command;

import com.examp.springmvc.catalog.domain.ports.output.ProductPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteProductUseCase {
    private final ProductPersistencePort productPersistencePort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public DeleteProductUseCase(ProductPersistencePort productPersistencePort) {
        this.productPersistencePort = productPersistencePort;
    }

    @Transactional
    public void execute(Long id) {
        if (!productPersistencePort.findById(id).isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có ID: " + id);
        }
        productPersistencePort.deleteById(id);
    }
}
