package com.dev.servlet.shared.util;

import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import com.dev.servlet.shared.vo.KeyPair;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @SuppressWarnings("unchecked")
    public static Object summarizeResponseBody(Object object) {
        if (object instanceof IPageable<?> pageable) {
            return pageableSummary(pageable);
        }

        if (object instanceof Set<?> container && !container.isEmpty()) {
            Set<Object> responseSet = new java.util.HashSet<>((Set<Object>) object);

            Iterator<?> iterator = container.iterator();
            Object element;
            while (iterator.hasNext()) {
                element = iterator.next();

                if (element instanceof KeyPair(String key, Object value)) {
                    if (value instanceof IPageable<?> pg) {
                        responseSet.remove(element);
                        Object summary = pageableSummary(pg);
                        responseSet.add(new KeyPair(key, summary));

                    } else if (value instanceof Collection<?> coll) {
                        responseSet.remove(element);
                        responseSet.add(new KeyPair(key, Map.of("total_elements", coll.size())));
                    }

                } else if (element instanceof Collection<?> coll) {
                    responseSet.remove(element);
                    responseSet.add(new KeyPair("collection", Map.of("total_elements", coll.size())));
                }
            }

            return responseSet;
        }

        return object;
    }

    @NotNull
    private static Object pageableSummary(IPageable<?> pageable) {
        return Map.of(
                "total_elements", pageable.getTotalElements(),
                "current_page", pageable.getCurrentPage(),
                "page_size", pageable.getPageSize()
        );
    }
}
