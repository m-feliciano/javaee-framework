package com.servletstack.adapter.out.image;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@ApplicationScoped
public class ImageService {

    @Inject
    private ImageProcessor processor;

    public InputStream processToOptimizedJpg(InputStream in, int size, String intendedUsage) {
        try {
            BufferedImage img = ImageIO.read(in);
            if (img == null) {
                throw new IllegalArgumentException("Invalid image");
            }

            img = processor.normalizeToJpg(img);
            img = processor.cropSquare(img);
            img = processor.resize(img, size);

            var messageDigest = MessageDigest.getInstance("SHA-256");

            ByteArrayOutputStream output = new ByteArrayOutputStream(64 * 1024);
            DigestOutputStream digest = new DigestOutputStream(output, messageDigest);

            ImageIO.write(img, "jpg", digest);
            digest.flush();

            String sha = "sha256:" + HexFormat.of().formatHex(messageDigest.digest());

            ImageMetadataContract metadata = buildMetadataContract(intendedUsage, sha);

            ByteArrayOutputStream packaged = new ByteArrayOutputStream(output.size() + 512);
            JpegCloudWriter.write(output.toByteArray(), metadata, packaged);

            return new ByteArrayInputStream(packaged.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Image processing failed", e);
        }
    }

    private ImageMetadataContract buildMetadataContract(String intendedUsage, String sha256) {
        var asset = new ImageMetadataContract.Asset(
                UUID.randomUUID().toString(),
                "jpg",
                sha256,
                Instant.now().toString()
        );

        var source = new ImageMetadataContract.Source(
                "upload",
                "image-service",
                "prod"
        );

        var processing = new ImageMetadataContract.Processing(
                new String[]{"resize", "optimize"},
                3,
                85
        );

        var delivery = new ImageMetadataContract.Delivery(true, intendedUsage);

        return new ImageMetadataContract(
                "1.0",
                asset,
                source,
                processing,
                delivery
        );
    }
}
