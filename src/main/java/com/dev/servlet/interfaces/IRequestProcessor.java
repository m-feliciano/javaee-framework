package com.dev.servlet.interfaces;

import com.dev.servlet.pojo.records.StandardRequest;

@FunctionalInterface
public interface IRequestProcessor {
    Object process(StandardRequest standardRequest) throws Exception;

}
