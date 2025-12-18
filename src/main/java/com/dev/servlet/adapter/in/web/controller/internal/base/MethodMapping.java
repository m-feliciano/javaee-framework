package com.dev.servlet.adapter.in.web.controller.internal.base;

import com.dev.servlet.adapter.in.web.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * MethodMapping - A record that associates a RequestMapping annotation with its corresponding method implementation.
 *
 * @param parent         - The RequestMapping annotation associated with the method.
 * @param implementation - The method implementation.
 */
@Slf4j
public record MethodMapping(RequestMapping parent, Method implementation) {
}
