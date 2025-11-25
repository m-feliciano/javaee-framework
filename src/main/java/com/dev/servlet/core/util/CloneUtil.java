package com.dev.servlet.core.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@SuppressWarnings("unchecked")
public final class CloneUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

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
