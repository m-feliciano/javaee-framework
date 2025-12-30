package com.servletstack.application.port.in.product;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.FileUploadRequest;

public interface UpdateProductThumbUseCase {
    void updateThumb(FileUploadRequest request, String auth) throws AppException;
}