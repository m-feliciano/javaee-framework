package com.dev.servlet.core.util;

import com.dev.servlet.domain.transfer.records.KeyPair;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ClassUtil {

    public static List<Class<?>> scanPackage(String packageName) throws Exception {
        return scanPackage(packageName, null);
    }

    public static List<Class<?>> scanPackage(String packageName, Class<? extends Annotation>[] annotations) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        File[] files = getFiles(packageName);
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(scanPackage(packageName + "." + file.getName(), annotations));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = ClassUtils.getClass(className);
                if (annotations == null) {
                    classes.add(clazz);
                } else {
                    for (var annotation : annotations) {
                        if (clazz.isAnnotationPresent(annotation)) {
                            classes.add(clazz);
                            break;
                        }
                    }
                }
            }
        }
        return classes;
    }

    private static File[] getFiles(String packageName) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Objects.requireNonNull(classLoader);
        String path = packageName.replace('.', File.separatorChar);
        File directory = new File(classLoader.getResource(path).getFile());
        if (!directory.exists()) {
            throw new Exception("Directory does not exist");
        }
        File[] files = directory.listFiles();
        Objects.requireNonNull(files);
        return files;
    }

    public static <T> T castObject(Class<T> clazz, Object object) {
        return clazz.cast(object);
    }

    public static <T> Collection<T> castList(Class<T> clazz, Collection<?> list) {
        List<T> result = new ArrayList<>();
        for (Object object : list) {
            result.add(castObject(clazz, object));
        }
        return result;
    }


    public static <T> List<T> castList(List<?> list) {
        List<T> result = new ArrayList<>();
        for (Object object : list) {
            result.add((T) object);
        }
        return result;
    }


    public static <T> T castWrapper(Class<T> type, Object value) {
        if (value == null) return null;
        try {
            if (type == String.class) {
                value = value.toString();
            } else if (type == Integer.class) {
                value = Integer.parseInt(value.toString());
            } else if (type == Long.class) {
                value = Long.parseLong(value.toString());
            } else if (type == Double.class) {
                value = Double.parseDouble(value.toString());
            } else if (type == Float.class) {
                value = Float.parseFloat(value.toString());
            } else if (type == Boolean.class) {
                value = Boolean.parseBoolean(value.toString());
            } else if (type == BigDecimal.class) {
                value = FormatterUtil.parseCurrency(value.toString());
            } else if (type == Date.class) {
                value = FormatterUtil.toDate(value.toString());
            }
            return type.cast(value);
        } catch (Exception ignored) {
        }
        return null;
    }


    public static <T> Class<T> getSubClassType(Class<?> clazz) {
        return (Class<T>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
    }


    public static <U> Class<U> extractType(Class<?> clazz, int position) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            return (Class<U>) actualTypeArguments[position - 1];
        }
        throw new IllegalArgumentException("Class does not have parameterized types");
    }


    public static <T> void setFieldValue(Field field, T entity, Object value) {
        try {
            Object wrapper = ClassUtil.castWrapper(field.getType(), value);
            field.set(entity, wrapper);
        } catch (Exception ignored) {
        }
    }


    public static <T> void fillObject(T object, List<KeyPair> data) {
        List<Field> fields = FieldUtils.getAllFieldsList(object.getClass())
                .stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()) && !field.getType().getName().startsWith("com.dev.servlet"))
                .toList();
        List<KeyPair> keyPairs = data.stream()
                .filter(pair -> fields.stream().anyMatch(field -> field.getName().equals(pair.getKey())))
                .toList();
        if (keyPairs.isEmpty()) {
            return;
        }
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                keyPairs.stream()
                        .filter(kp -> kp.getKey().equals(field.getName()))
                        .findFirst()
                        .ifPresent(f -> setFieldValue(field, object, f.getValue()));
            } finally {
                field.setAccessible(false);
            }
        }
    }
}
