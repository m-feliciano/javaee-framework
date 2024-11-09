package com.dev.servlet.pojo.records;

public class Pagination implements java.io.Serializable {
    private int totalRecords;
    private final int currentPage;
    private final int pageSize;
    private final Sort sort;
    private final Order order;

    public Pagination(int currentPage, int size, Sort sort, Order order) {
        this.pageSize = size;
        this.currentPage = currentPage;
        this.sort = sort;
        this.order = order;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public Order getOrder() {
        return order;
    }

    public Sort getSort() {
        return sort;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getTotalPages() {
        return Math.max((int) Math.ceil(totalRecords * 1.0 / pageSize), 1);
    }

    public int getFirstResult() {
        return (currentPage - 1) * pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }
}