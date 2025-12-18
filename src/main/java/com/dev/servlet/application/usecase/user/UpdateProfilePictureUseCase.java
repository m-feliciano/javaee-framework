package com.dev.servlet.application.usecase.user;

import com.dev.servlet.adapter.out.image.ImageService;
import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.in.user.UpdateProfilePicturePort;
import com.dev.servlet.application.port.out.image.FileImageRepositoryPort;
import com.dev.servlet.application.port.out.security.AuthenticationPort;
import com.dev.servlet.application.port.out.storage.StorageService;
import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.application.transfer.request.FileUploadRequest;
import com.dev.servlet.domain.entity.FileImage;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.domain.vo.BinaryPayload;
import com.dev.servlet.infrastructure.config.Properties;
import com.dev.servlet.infrastructure.http.FileHttpClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class UpdateProfilePictureUseCase implements UpdateProfilePicturePort {
    private static final long MAX_IMAGE_SIZE = 1024 * 1024; // 1MB
    private static final String CONTENT_TYPE_JPG = "image/jpeg";

    @Inject
    private AuthenticationPort authPort;
    @Inject
    private UserRepositoryPort repository;
    @Inject
    private StorageService storageService;
    @Inject
    private ImageService imageService;
    @Inject
    private FileHttpClient fileHttpClient;
    @Inject
    private FileImageRepositoryPort fileImageRepositoryPort;

    public void updatePicture(FileUploadRequest request, String auth) throws AppException {
        if (Properties.isDemoModeEnabled()) {
            throw new AppException("Operation not allowed in demo mode.");
        }

        BinaryPayload payload = request.payload();
        if (payload.size() > MAX_IMAGE_SIZE) {
            throw new AppException("Image size exceeds 1MB.");
        }

        User user = repository.findById(authPort.extractUserId(auth))
                .orElseThrow(() -> new AppException("User not found."));

        final String filename = UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        final String path = "private/users/" + user.getId() + "/profile/" + filename;

        FileImage image = user.getProfileImage();
        if (image == null) {
            image = FileImage.builder()
                    .user(user)
                    .uri(path)
                    .fileName(filename)
                    .fileType(CONTENT_TYPE_JPG)
                    .status(Status.ACTIVE.getValue())
                    .build();
            fileImageRepositoryPort.save(image);

        } else {
            String old = image.getUri();
            image.setUri(path);
            image.setFileName(filename);
            image.setFileType(CONTENT_TYPE_JPG);
            fileImageRepositoryPort.update(image);
            storageService.deleteFile(old);
        }

        try (InputStream in = payload.openStream();
             InputStream pic = imageService.processToOptimizedJpg(in, 400, "profile-picture")) {

            URI uri = storageService.generateUploadUri(path, "image/jpeg", Duration.ofMinutes(1));
            fileHttpClient.upload(uri, pic, CONTENT_TYPE_JPG);
        } catch (Exception e) {
            log.error("Error updating profile picture", e);
            throw new AppException("Failed to update profile picture");

        } finally {
            payload.close();
        }
    }
}
