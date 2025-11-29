package com.dev.servlet.infrastructure.persistence.transfer;

public interface IPageRequest extends ISorted {
    Object getFilter();
    void setFilter(Object filter);
    int getInitialPage();

    int getPageSize();

    default int getFirstResult() {
        return (getInitialPage() - 1) * getPageSize();
    }
}
