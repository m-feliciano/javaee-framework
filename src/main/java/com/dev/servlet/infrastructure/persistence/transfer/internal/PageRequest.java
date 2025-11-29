package com.dev.servlet.infrastructure.persistence.transfer.internal;

import com.dev.servlet.domain.valueobject.Sort;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public final class PageRequest implements IPageRequest, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Sort sort;
    private int initialPage;
    private int pageSize;
    private Object filter;

    public static PageRequest of(int initialPage, int pageSize, Object filter, Sort sort) {
        return PageRequest.builder().initialPage(initialPage).pageSize(pageSize).filter(filter).sort(sort).build();
    }
}
