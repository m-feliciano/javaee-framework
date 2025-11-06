package com.dev.servlet.core.mapper;

@FunctionalInterface
public interface Mapper<R, U> {
    U map(R object);
}
