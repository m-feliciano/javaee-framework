package com.dev.servlet.infrastructure.persistence.transfer.internal;

import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.shared.vo.Sort;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public final class PageRequest implements IPageRequest, Serializable {
    private Sort sort;
    private int initialPage;
    private int pageSize;
    private Object filter;

    public static PageRequest of(int initialPage, int pageSize, Object filter, Sort sort) {
        return PageRequest.builder().initialPage(initialPage).pageSize(pageSize).filter(filter).sort(sort).build();
    }

    @Override
    public String toString() {
        return "PageRequest{" +
               "initialPage=" + initialPage +
               ", pageSize=" + pageSize +
               ", sort=" + sort +
               '}';
    }
}
