package com.dev.servlet.core.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class CloneUtil {

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private abstract static class IgnoreHibernateMixin {
    }

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .addMixIn(Object.class, IgnoreHibernateMixin.class);

    @SuppressWarnings("unchecked")
    public static <T> T forceClone(T object) {
        if (object == null) return null;
        String json = toJson(object);
        Class<T> clazz = (Class<T>) object.getClass();
        return fromJson(json, clazz);
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
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Error serializing object to JSON: {}", e.getMessage());
            return null;
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Error deserializing JSON to class {}: {}", clazz.getName(), e.getMessage());
            return null;
        }
    }
}
