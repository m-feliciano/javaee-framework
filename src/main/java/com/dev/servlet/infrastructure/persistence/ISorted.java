package com.dev.servlet.infrastructure.persistence;
import com.dev.servlet.domain.records.Sort;

public interface ISorted {
    default Sort getSort() {
        return Sort.unsorted();
    }
}
