package com.dev.servlet.application.transfer.request;

import com.dev.servlet.domain.vo.BinaryPayload;

import java.util.UUID;

public record FileUploadRequest(BinaryPayload payload, UUID id) {
}
