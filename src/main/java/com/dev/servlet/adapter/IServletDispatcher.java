package com.dev.servlet.adapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface IServletDispatcher {
    void dispatch(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
