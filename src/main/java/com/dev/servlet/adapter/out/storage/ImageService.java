package com.dev.servlet.adapter.out.storage;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class ImageService {
    static {
        // make sure to load all image formats, including png
        ImageIO.scanForPlugins();
    }

    private OkHttpClient client;

    @PostConstruct
    public void init() {
        ConnectionPool pool = new ConnectionPool(10, 5, TimeUnit.MINUTES);
        client = new OkHttpClient.Builder()
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .callTimeout(0, java.util.concurrent.TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .connectionPool(pool)
                .build();
    }

    public BufferedImage getJpgImageFromFile(File fileImg) {
        String ext = FilenameUtils.getExtension(fileImg.getName());
        if (!"png".equals(ext) && !"jpg".equals(ext)) {
            throw new RuntimeException("It's only allowed JPG or PNG images");
        }

        try {
            BufferedImage img = ImageIO.read(fileImg);
            if ("png".equals(ext)) {
                img = pngToJpg(img);
            }
            return img;
        } catch (IOException e) {
            throw new RuntimeException("Error reading file");
        }
    }

    private BufferedImage pngToJpg(BufferedImage img) {
        BufferedImage jpgImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        // fill the background transparent png images with white
        jpgImage.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
        return jpgImage;
    }

    public InputStream getInputStream(BufferedImage img, String extension) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, extension, os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error reading file");
        }
    }

    public BufferedImage cropSquare(BufferedImage sourceImg) {
        int min = (Math.min(sourceImg.getHeight(), sourceImg.getWidth()));
        int x = (sourceImg.getWidth() / 2) - (min / 2);
        int y = (sourceImg.getHeight() / 2) - (min / 2);

        return Scalr.crop(sourceImg, x, y, min, min);
    }

    public BufferedImage resize(BufferedImage sourceImg, int size) {
        return Scalr.resize(sourceImg, Scalr.Method.ULTRA_QUALITY, size);
    }

    public InputStream writePicture(InputStream in, int targetWidth) throws IOException {
        BufferedImage image = ImageIO.read(in);
        image = cropSquare(image);
        image = resize(image, targetWidth);
        return getInputStream(image, "jpg");
    }

    public void uploadImageToUrl(URI uploadUrl, InputStream in, String contentType) {
        try {
            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(in.readAllBytes(),
                    okhttp3.MediaType.parse(contentType));

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(uploadUrl.toString())
                    .put(requestBody)
                    .build();

            try (okhttp3.Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Failed to image image: " + response);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error uploading image to URL", e);
        }
    }

    public InputStream getJpgImageFromUrl(String sourceUrl) {
        try {
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(sourceUrl)
                    .get()
                    .build();

            try (okhttp3.Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new RuntimeException("Failed to download image: " + response);
                }

                BufferedImage img = ImageIO.read(response.body().byteStream());
                if (img == null) throw new RuntimeException("Failed to decode image from URL: " + sourceUrl);

                return getInputStream(pngToJpg(img), "jpg");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error downloading image from URL", e);
        }
    }
}
