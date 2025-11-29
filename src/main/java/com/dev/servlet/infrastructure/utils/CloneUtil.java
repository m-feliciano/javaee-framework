package com.dev.servlet.infrastructure.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@SuppressWarnings("unchecked")
public final class CloneUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    public static <T> T forceClone(T object) {
        if (object == null) return null;
        String json = toJson(object);
        return fromJson(json, (Class<T>) object.getClass());
    }

    public static <T> List<T> cloneList(Collection<T> objects) {
        if (objects == null || objects.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            String json = toJson(objects);
            CollectionType valueType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, objects.iterator().next().getClass());
            return objectMapper.readValue(json, valueType);
        } catch (Exception e) {
            log.error("Error cloning list: {}", e.getMessage());
            return null;
        }
    }

    public static String toJson(Object object) {
        try {
            if (object == null) return null;
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Error serializing object to JSON: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return json != null ? objectMapper.readValue(json, clazz) : null;
        } catch (Exception e) {
            log.error("Error deserializing JSON to {}: {}", clazz.getName(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
