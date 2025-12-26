package com.dev.servlet.application.transfer.request;

import com.dev.servlet.domain.vo.BinaryPayload;

import java.util.UUID;

/**
 * FileUploadRequest record representing a file upload request.
 *
 * @param payload {@link BinaryPayload} - The binary payload of the file
 * @param id      - The id of the entity to which the file is associated
 */
public record FileUploadRequest(BinaryPayload payload, UUID id) {
}
