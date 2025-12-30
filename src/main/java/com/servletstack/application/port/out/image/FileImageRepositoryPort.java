package com.servletstack.application.port.out.image;

import com.servletstack.domain.entity.FileImage;

import java.util.List;

public interface FileImageRepositoryPort {
    List<FileImage> saveAll(List<FileImage> images);

    FileImage save(FileImage image);

    FileImage update(FileImage image);

    void delete(FileImage image);
}
