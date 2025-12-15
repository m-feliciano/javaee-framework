package com.dev.servlet.adapter.out.image;

import jakarta.enterprise.context.ApplicationScoped;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@ApplicationScoped
public class ImageProcessor {

    static {
        ImageIO.scanForPlugins();
    }

    /**
     * Convert image to TYPE_INT_RGB (JPG compatible)
     * TYPE_INT_RGB does not support transparency, so transparent areas
     * will be filled with white color.
     *
     * @param img the source image
     * @return a BufferedImage of type TYPE_INT_RGB
     */
    public BufferedImage normalizeToJpg(BufferedImage img) {
        if (img.getType() == BufferedImage.TYPE_INT_RGB) {
            return img;
        }

        BufferedImage jpg = new BufferedImage(
                img.getWidth(),
                img.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = jpg.createGraphics();
        g.drawImage(img, 0, 0, Color.WHITE, null);
        g.dispose();

        return jpg;
    }

    public BufferedImage cropSquare(BufferedImage source) {
        int min = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - min) / 2;
        int y = (source.getHeight() - min) / 2;

        return Scalr.crop(source, x, y, min, min);
    }

    public BufferedImage resize(BufferedImage source, int size) {
        return Scalr.resize(source, Scalr.Method.ULTRA_QUALITY, size);
    }

    public InputStream toJpgStream(BufferedImage image) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write image", e);
        }
    }
}
