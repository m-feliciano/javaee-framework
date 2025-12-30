package com.servletstack.adapter.out.image;

public record ImageMetadataContract(
        String schemaVersion,
        Asset asset,
        Source source,
        Processing processing,
        Delivery delivery) {

    public record Asset(
            String assetId,
            String originalFormat,
            String contentHash,
            String createdAt) {
    }

    public record Source(
            String origin,
            String ownerService,
            String environment) {
    }

    public record Processing(
            String[] pipeline,
            int version,
            int quality) {
    }

    public record Delivery(
            boolean cdnCacheable,
            String intendedUsage) {
    }
}
