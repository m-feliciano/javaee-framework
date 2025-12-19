package com.dev.servlet.adapter.out.image;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@ApplicationScoped
public class ImageService {

    static {
        ImageIO.scanForPlugins();
    }

    @Inject
    private ImageProcessor processor;

    private static ImageMetadataContract composeMetadataContract(String intendedUsage,
                                                                 ByteArrayOutputStream baos) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(baos.toByteArray());
        String sha = "sha256:" + HexFormat.of().formatHex(hash);
        return buildMetadataContract(intendedUsage, sha);
    }

    private static ImageMetadataContract buildMetadataContract(String intendedUsage, String sha256) {
        var asset = new ImageMetadataContract.Asset(
                UUID.randomUUID().toString(),
                "png",
                sha256,
                DateFormat.getDateTimeInstance().format(Instant.now().toEpochMilli())

        );
        var source = new ImageMetadataContract.Source("upload", "image-service", "prod");
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

    // Works as a director implementation to process images
    public InputStream processToOptimizedJpg(InputStream in, int size, String intendedUsage) {
        try {
            BufferedImage img = ImageIO.read(in);
            if (img == null) throw new RuntimeException("Invalid image");

            img = processor.normalizeToJpg(img);
            img = processor.cropSquare(img);
            img = processor.resize(img, size);

            var baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", baos);
            ImageMetadataContract contract = composeMetadataContract(intendedUsage, baos);

            var output = new ByteArrayOutputStream();
            JpegCloudWriter.write(baos.toByteArray(), contract, output);

            return new ByteArrayInputStream(output.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Image processing failed", e);
        }
    }
}
