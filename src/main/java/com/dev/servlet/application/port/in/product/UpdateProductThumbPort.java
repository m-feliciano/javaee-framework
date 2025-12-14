package com.dev.servlet.application.port.in.product;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.request.FileUploadRequest;

public interface UpdateProductThumbPort {
    void updateThumb(FileUploadRequest request, String auth) throws AppException;
}