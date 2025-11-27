<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="search-container">
    <form class="search-form" action="${param.action}" method="get">
        <div class="search-input-wrapper">
            <label class="form-label" for="searchQuery">Search</label>
            <div class="search-input">
                <input type="text"
                       id="searchQuery"
                       name="q"
                       class="form-control"
                       placeholder="${param.placeholder}"
                       value="${param.query}">
                <i class="bi bi-search"></i>
            </div>
        </div>

        <c:if test="${not empty categories and categories != null}">
            <div class="search-input-wrapper" style="max-width: 200px;">
                <label class="form-label" for="searchCategory">Category</label>
                <select id="searchCategory" name="category" class="form-control" onchange="this.form.submit()">
                    <option value="">All</option>
                    <c:forEach items="${categories}" var="category">
                        <option value="${category.id}" ${param.category == category.id ? 'selected' : ''}>${category.name}</option>
                    </c:forEach>
                </select>
            </div>
        </c:if>

        <c:if test="${not empty param.searchType}">
            <input type="hidden" name="k" value="${param.searchType}">
        </c:if>
        <c:if test="${not empty param.listStatus}">
            <div class="search-input-wrapper" style="max-width: 150px;">
                <label class="form-label" for="searchStatus">Status</label>
                <select id="searchStatus" name="status" class="form-control" onchange="this.form.submit()">
                    <option value="">All</option>
                    <c:forEach items="${param.listStatus}" var="statusItem">
                        <c:set var="plainStatus" value="${statusItem.replaceAll('[^a-zA-Z0-9]', '')}" />
                        <option value="${plainStatus}"
                                <c:if test="${plainStatus == status}">selected</c:if>>${plainStatus}</option>
                    </c:forEach>
                </select>
            </div>
        </c:if>
        <c:if test="${not empty param.limit}">
            <div class="search-input-wrapper" style="max-width: 100px;">
                <label class="form-label" for="searchLimit">Limit</label>
                <select id="searchLimit" name="limit" class="form-control" onchange="this.form.submit()">
                    <option value="5" ${param.limit == '5' ? 'selected' : ''}>5</option>
                    <option value="10" ${param.limit == '10' ? 'selected' : ''}>10</option>
                    <option value="20" ${param.limit == '20' ? 'selected' : ''}>20</option>
                    <option value="50" ${param.limit == '50' ? 'selected' : ''}>50</option>
                    <option value="100" ${param.limit == '100' ? 'selected' : ''}>100</option>
                </select>
            </div>
        </c:if>
        <c:if test="${not empty param.sort}">
            <input type="hidden" name="sort" value="${param.sort}">
        </c:if>
        <c:if test="${not empty param.order}">
            <input type="hidden" name="order" value="${param.order}">
        </c:if>

        <div class="search-actions">
            <button type="submit" class="btn btn-primary">
                <i class="bi bi-search"></i>
                Search
            </button>
            <c:if test="${not empty param.onclear}">
                <a href="${param.onclear}" class="btn btn-secondary">
                    <i class="bi bi-x-circle"></i>
                    Clear
                </a>
            </c:if>
        </div>
    </form>
</div>

