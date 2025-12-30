package com.servletstack.infrastructure.persistence.transfer;

import java.util.List;

public interface IPageable<T> extends ISorted {
    List<T> getContent();
    long getTotalElements();
    int getCurrentPage();
    int getPageSize();
}
