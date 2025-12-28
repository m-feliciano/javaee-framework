package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.port.in.product.CreateProductWithThumbUseCase;
import com.dev.servlet.application.port.in.product.RegisterProductUseCase;
import com.dev.servlet.application.port.in.product.UpdateProductThumbUseCase;
import com.dev.servlet.application.transfer.request.FileUploadRequest;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.ProductResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateProductFlowService implements CreateProductWithThumbUseCase {

    @Inject
    private RegisterProductUseCase registerProductUseCase;
    @Inject
    private UpdateProductThumbUseCase updateProductUseCase;

    public ProductResponse execute(ProductRequest req, String auth) {
        ProductResponse response = registerProductUseCase.register(req, auth);
        if (req.payload() != null) {
            FileUploadRequest upload = new FileUploadRequest(req.payload(), response.getId());
            updateProductUseCase.updateThumb(upload, auth);
        }
        return response;
    }
}
