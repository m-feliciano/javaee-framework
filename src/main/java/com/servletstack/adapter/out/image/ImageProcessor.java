package com.servletstack.adapter.out.image;

import jakarta.enterprise.context.ApplicationScoped;
import org.imgscalr.Scalr;

import java.awt.*;
import java.awt.image.BufferedImage;

@ApplicationScoped
public class ImageProcessor {

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
        g.setComposite(AlphaComposite.Src);
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
        return Scalr.resize(
                source,
                Scalr.Method.ULTRA_QUALITY,
                Scalr.Mode.FIT_EXACT,
                size,
                size
        );
    }
}
