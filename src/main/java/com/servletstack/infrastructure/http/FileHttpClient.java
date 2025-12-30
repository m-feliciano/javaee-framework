package com.servletstack.infrastructure.http;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class FileHttpClient {

    private OkHttpClient client;

    @PostConstruct
    void init() {
        ConnectionPool pool = new ConnectionPool(10, 5, TimeUnit.MINUTES);
        client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .connectionPool(pool)
                .build();
    }

    public InputStream download(String url) {
        try {
            Request request = new Request.Builder().url(url).get().build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new RuntimeException("Failed to download image: " + response);
                }

                byte[] bytes = response.body().bytes();
                return new ByteArrayInputStream(bytes);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error downloading image", e);
        }
    }

    public void upload(URI uri, InputStream data, String contentType) {
        try {
            RequestBody body = RequestBody.create(
                    data.readAllBytes(),
                    okhttp3.MediaType.parse(contentType)
            );

            Request request = new Request.Builder().url(uri.toString()).put(body).build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Upload failed: " + response);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error uploading image", e);
        }
    }
}
