package com.dev.servlet.application.mapper;

@FunctionalInterface
public interface Mapper<R, U> {
    U map(R object);
}
