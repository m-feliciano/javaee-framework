package com.servletstack.infrastructure.persistence.transfer;

import com.servletstack.shared.vo.Sort;

public interface ISorted {
    default Sort getSort() {
        return Sort.unsorted();
    }
}
