package com.dev.servlet.infrastructure.persistence.transfer;

import com.dev.servlet.shared.vo.Sort;

public interface ISorted {
    default Sort getSort() {
        return Sort.unsorted();
    }
}
