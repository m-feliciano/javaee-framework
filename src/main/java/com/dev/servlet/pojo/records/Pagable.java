package com.dev.servlet.pojo.records;

/**
 * This class represents a pagination object.
 * <br>
 * It contains the following fields: <br>
 * - pageSize: the number of records per page <br>
 * - page: the current page <br>
 * - totalRecords: the total number of records <br>
 * - sort: the sort field <br>
 * - order: the order field <br>
 *
 * @since 1.4
 */
public class Pagable {

    private int pageSize;
    private int page;
    private int totalRecords;
    private Sort sort;
    private Order order;


    public Pagable() {
        // Empty constructor
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return page;
    }

    public void setCurrentPage(int currentPage) {
        this.page = currentPage;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
        validate();
    }

    public int getTotalPages() {
        return Math.max((int) Math.ceil(totalRecords * 1.0 / pageSize), 1);
    }

    public void validate() {
        if (!(page > 0 && page <= getTotalPages())) page = 1;
    }

    public int getFirstResult() {
        return (page - 1) * pageSize;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
