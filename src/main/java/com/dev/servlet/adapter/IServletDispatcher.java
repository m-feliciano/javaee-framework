package com.dev.servlet.adapter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface IServletDispatcher {
    void dispatch(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
