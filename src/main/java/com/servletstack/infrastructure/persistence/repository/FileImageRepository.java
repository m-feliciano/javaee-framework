package com.servletstack.infrastructure.persistence.repository;

import com.servletstack.application.exception.AppException;
import com.servletstack.application.port.out.image.FileImageRepositoryPort;
import com.servletstack.domain.entity.FileImage;
import com.servletstack.domain.entity.enums.Status;
import com.servletstack.infrastructure.persistence.repository.base.BaseRepository;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.enterprise.context.RequestScoped;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@NoArgsConstructor
@RequestScoped
public class FileImageRepository extends BaseRepository<FileImage, UUID> implements FileImageRepositoryPort {

    @Override
    public Collection<FileImage> findAll(FileImage object) {
        log.warn("FileImageRepository: findAll implementation is not implemented yet.");
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<FileImage> saveAll(List<FileImage> images) {
        log.debug("Saving {} fileImages in batch.", images.size());

        Session session = em.unwrap(Session.class);
        beginTransaction();
        try {
            session.doWork(connection -> {
                String copies = String.join(", ", Collections.nCopies(7, "?"));
                String sql = "INSERT INTO tb_file_image (id, file_name, file_type, uri, status, product_id, user_id) VALUES (" + copies + ")";

                try (var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    for (FileImage entity : images) {
                        entity.setId(UuidCreator.getTimeOrdered());

                        ps.setObject(1, entity.getId());
                        ps.setString(2, entity.getFileName());
                        ps.setString(3, entity.getFileType());
                        ps.setString(4, entity.getUri());
                        ps.setString(5, Status.ACTIVE.getValue());
                        ps.setObject(6, entity.getProduct() != null ? entity.getProduct().getId() : null);
                        ps.setObject(7, entity.getUser() != null ? entity.getUser().getId() : null);
                        ps.addBatch();
                    }

                    ps.executeBatch();
                }
            });

            commitTransaction(true);
            return images;

        } catch (Exception e) {
            log.error("Transaction rolled back due to error", e);
            throw new AppException("Transaction failed.");
        }
    }
}
