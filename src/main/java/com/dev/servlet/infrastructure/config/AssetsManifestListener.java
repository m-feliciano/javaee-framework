package com.dev.servlet.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

@WebListener
public class AssetsManifestListener implements ServletContextListener {
    private static final String ATTR_NAME = "assetsManifest";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream is = ctx.getResourceAsStream("/resources/dist/manifest.json")) {
            ctx.setAttribute(ATTR_NAME, is != null ? mapper.readValue(is, Map.class) : Collections.emptyMap());

        } catch (IOException e) {
            ctx.log("Failed to read assets manifest", e);
            ctx.setAttribute(ATTR_NAME, Collections.emptyMap());
        }
    }
}
