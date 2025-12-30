package com.servletstack.application.port.in.user;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.transfer.request.FileUploadRequest;

public interface UpdateProfilePictureUseCase {
    void updatePicture(FileUploadRequest request, String auth) throws AppException;
}

