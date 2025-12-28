package com.dev.servlet.application.port.in.user;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.transfer.request.FileUploadRequest;

public interface UpdateProfilePictureUseCase {
    void updatePicture(FileUploadRequest request, String auth) throws AppException;
}

