package com.dev.servlet.infrastructure.persistence.transfer;

import com.dev.servlet.domain.valueobject.Sort;

public interface ISorted {
    default Sort getSort() {
        return Sort.unsorted();
    }
}
