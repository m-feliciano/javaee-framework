package com.dev.servlet.adapter.out.image;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

@ApplicationScoped
public class ImageService {

    static {
        ImageIO.scanForPlugins();
    }

    @Inject
    private ImageProcessor processor;

    public InputStream processToSquareJpg(InputStream in, int size) {
        try {
            BufferedImage img = ImageIO.read(in);
            if (img == null) throw new RuntimeException("Invalid image");

            img = processor.normalizeToJpg(img);
            img = processor.cropSquare(img);
            img = processor.resize(img, size);

            return processor.toJpgStream(img);
        } catch (Exception e) {
            throw new RuntimeException("Image processing failed", e);
        }
    }
}
