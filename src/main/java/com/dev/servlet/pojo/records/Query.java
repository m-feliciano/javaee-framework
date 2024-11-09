package com.dev.servlet.pojo.records;

/**
 * @since 1.4
 */
public record Query(Pagination pagination,
                    String search,
                    String type) {

    public Pagination getPagination() {
        return pagination;
    }
}