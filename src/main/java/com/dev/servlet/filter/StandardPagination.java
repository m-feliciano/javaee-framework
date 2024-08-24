package com.dev.servlet.filter;

public class StandardPagination {
    private int pageSize;
    private int currentPage;
    private int totalRecords;


    public StandardPagination() {
        // Empty constructor
    }

    public static StandardPagination of(int currentPage, int pageSize) {
        StandardPagination pagination = new StandardPagination();
        pagination.setCurrentPage(currentPage);
        pagination.setPageSize(pageSize);
        return pagination;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getTotalPages() {
        return Math.max((int) Math.ceil(totalRecords * 1.0 / pageSize), 1);
    }

    public boolean validate() {
        return currentPage > 0 && currentPage <= getTotalPages();
    }

    public int getFirstResult() {
        return (currentPage - 1) * pageSize;
    }

}
