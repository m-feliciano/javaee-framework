package com.dev.servlet.core.util;

import com.dev.servlet.controller.base.BaseRouterController;
import com.dev.servlet.core.annotation.Controller;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ClassUtils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class ClassUtil {

    public static List<Class<?>> scanPackage(String packageName, Class<? extends Annotation> annotation) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        File[] files = getFiles(packageName);

        for (File file : files) {
            final String fileName = file.getName();

            if (file.isDirectory()) {
                classes.addAll(scanPackage(packageName + "." + fileName, annotation));

            } else if (fileName.endsWith(".class")) {
                String className = packageName + '.' + fileName.substring(0, fileName.length() - 6);
                Class<?> clazz = ClassUtils.getClass(className);

                if (annotation != null && !clazz.isAnnotationPresent(annotation)) break;
                classes.add(clazz);
            }
        }

        return classes;
    }

    private static File[] getFiles(String packageName) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Objects.requireNonNull(classLoader);
        String path = packageName.replace('.', File.separatorChar);
        URL resource = Objects.requireNonNull(classLoader.getResource(path));

        File directory = new File(resource.getFile());
        if (!directory.exists()) {
            throw new Exception("Directory does not exist");
        }

        File[] files = directory.listFiles();
        Objects.requireNonNull(files);
        return files;
    }

    public static <T> List<T> castList(List<?> list) {
        List<T> result = new ArrayList<>();
        for (Object object : list) {
            result.add((T) object);
        }
        return result;
    }

    public static <T> Class<T> getSubClassType(Class<?> clazz) {
        return (Class<T>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public static String findControllerOnInterfaceRecursive(Class<?> itf) {
        Controller c = itf.getAnnotation(Controller.class);
        if (c != null) return c.value();

        for (Class<?> sup : itf.getInterfaces()) {
            String found = findControllerOnInterfaceRecursive(sup);
            if (found != null) return found;
        }

        return null;
    }

    public static List<Method> findMethodsOnInterfaceRecursive(Class<? extends BaseRouterController> aClass) {
        List<Method> methods = new ArrayList<>();
        for (Class<?> itf : aClass.getInterfaces()) {
            methods.addAll(Arrays.asList(itf.getDeclaredMethods()));
            methods.addAll(findMethodsOnInterfaceRecursive((Class<? extends BaseRouterController>) itf));
        }
        return methods;
    }
}
