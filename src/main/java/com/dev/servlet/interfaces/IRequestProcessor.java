package com.dev.servlet.interfaces;

import com.dev.servlet.filter.StandardRequest;

@FunctionalInterface
public interface IRequestProcessor {

    /**
     * Process
     *
     * @param standardRequest
     * @return the string
     */
    String process(StandardRequest standardRequest) throws Exception;

}
