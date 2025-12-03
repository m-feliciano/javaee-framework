package com.dev.servlet.infrastructure.utils;

import com.dev.servlet.shared.vo.KeyPair;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SuppressWarnings("unchecked")
public class KeyPairJsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(List<KeyPair> pairs) {
        Map<String, Object> root = new HashMap<>();
        for (KeyPair pair : pairs) {
            insert(root, pair.getKey().split("\\."), 0, pair.value());
        }

        try {
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            log.error("Error serializing KeyPair list to JSON: {}", e.getMessage());
            return null;
        }
    }

    private static void insert(Map<String, Object> map, String[] keys, int idx, Object value) {
        String key = keys[idx];
        if (idx == keys.length - 1) {
            map.put(key, value);
        } else {
            Map<String, Object> child = (Map<String, Object>) map.get(key);
            if (child == null) {
                child = new HashMap<>();
                map.put(key, child);
            }
            insert(child, keys, idx + 1, value);
        }
    }
}
