package com.dev.servlet.application.usecase.product;

import com.dev.servlet.application.port.in.product.CreateProductWithThumbPort;
import com.dev.servlet.application.port.in.product.RegisterProductPort;
import com.dev.servlet.application.port.in.product.UpdateProductThumbPort;
import com.dev.servlet.application.transfer.request.FileUploadRequest;
import com.dev.servlet.application.transfer.request.ProductRequest;
import com.dev.servlet.application.transfer.response.ProductResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateProductFlowUseCase implements CreateProductWithThumbPort {

    @Inject
    private RegisterProductPort registerProductPort;
    @Inject
    private UpdateProductThumbPort updateProductThumbPort;

    public ProductResponse execute(ProductRequest req, String auth) {
        ProductResponse response = registerProductPort.register(req, auth);
        if (req.payload() != null) {
            FileUploadRequest upload = new FileUploadRequest(req.payload(), response.getId());
            updateProductThumbPort.updateThumb(upload, auth);
        }
        return response;
    }
}
