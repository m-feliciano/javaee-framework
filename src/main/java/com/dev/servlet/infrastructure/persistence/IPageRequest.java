package com.dev.servlet.infrastructure.persistence;

/**
 * Interface representing a request for paginated data with filtering and sorting capabilities.
 * Extends sorting functionality to support ordered data retrieval.
 * 
 * <p>This interface defines the contract for pagination requests, encapsulating
 * page parameters, filtering criteria, and sorting options. It provides a standardized
 * way to request paginated data from repositories and services, enabling consistent
 * pagination behavior across the application.</p>
 *
 * @author servlets-team
 * @since 1.0
 */
public interface IPageRequest extends ISorted {
    Object getFilter();
    void setFilter(Object filter);
    int getInitialPage();
    int getPageSize();

    default int getFirstResult() {
        return (getInitialPage() - 1) * getPageSize();
    }
}
