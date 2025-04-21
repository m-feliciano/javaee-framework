package com.dev.servlet.pojo;

import com.dev.servlet.pojo.records.Sort;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter(AccessLevel.NONE)
@Builder
@AllArgsConstructor
public class Pagination implements Serializable {
    private final Integer currentPage;
    private final Integer pageSize;
    private final Sort sort;

    @Setter(AccessLevel.PUBLIC)
    private Integer totalRecords;

    public int getTotalPages() {
        double totalPerPage = Math.ceil(totalRecords * 1.0 / pageSize);
        return Math.max((int) totalPerPage, 1);
    }

    public int getFirstResult() {
        return (currentPage - 1) * pageSize;
    }

}