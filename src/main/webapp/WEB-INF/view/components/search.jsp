<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="search-container">
    <form class="search-form" action="${param.action}" method="get">
        <!-- Filtros de Busca -->
        <div class="search-filters">
            <!-- Campo de Busca -->
            <div class="search-field search-query">
                <div class="search-input-wrapper">
                    <input type="text"
                           id="searchQuery"
                           name="q"
                           class="form-control"
                           placeholder="${param.placeholder}"
                           value="${q}">
                    <i class="bi bi-search search-icon"></i>
                </div>
            </div>

            <!-- Filtro de Categoria (se disponível) -->
            <c:if test="${not empty categories}">
                <div class="search-field">
                    <select id="searchCategory"
                            name="category"
                            class="form-control"
                            onchange="this.form.submit()">
                        <option value="">All Categories</option>
                        <c:forEach items="${categories}" var="category">
                            <option value="${category.id}"
                                    ${param.category == category.id ? 'selected' : ''}>
                                <c:out value="${category.name}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </c:if>

            <!-- Filtro de Status (se disponível) -->
            <c:if test="${not empty param.listStatus}">
                <div class="search-field">
                    <select id="searchStatus"
                            name="status"
                            class="form-control"
                            onchange="this.form.submit()">
                        <option value="">All Status</option>
                        <c:forEach items="${param.listStatus}" var="statusItem">
                            <c:set var="plainStatus" value="${statusItem.replaceAll('[^a-zA-Z0-9]', '')}" />
                            <option value="${plainStatus}"
                                    <c:if test="${plainStatus == status}">selected</c:if>>
                                ${plainStatus}
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </c:if>

            <!-- Filtro de Limite (se disponível) -->
            <c:if test="${not empty param.limit}">
                <div class="search-field">
                    <select id="searchLimit"
                            name="limit"
                            class="form-control"
                            onchange="this.form.submit()">
                        <option value="3" ${param.limit == '3' ? 'selected' : ''}>3</option>
                        <option value="5" ${param.limit == '5' ? 'selected' : ''}>5</option>
                        <option value="10" ${param.limit == '10' ? 'selected' : ''}>10</option>
                        <option value="20" ${param.limit == '20' ? 'selected' : ''}>20</option>
                        <option value="50" ${param.limit == '50' ? 'selected' : ''}>50</option>
                        <option value="100" ${param.limit == '100' ? 'selected' : ''}>100</option>
                    </select>
                </div>
            </c:if>
        </div>

        <!-- Campos Hidden -->
        <c:if test="${not empty param.searchType}">
            <input type="hidden" name="k" value="${param.searchType}">
        </c:if>
        <c:if test="${not empty param.sort}">
            <input type="hidden" name="sort" value="${param.sort}">
        </c:if>
        <c:if test="${not empty param.order}">
            <input type="hidden" name="order" value="${param.order}">
        </c:if>

        <!-- Ações -->
        <div class="search-actions">
            <button type="submit" class="btn btn-primary">
                <i class="bi bi-search"></i>
                Search
            </button>
            <c:if test="${not empty param.onclear}">
                <a href="${param.onclear}" class="btn btn-secondary">
                    <i class="bi bi-x-circle"></i>
                    Clear Filters
                </a>
            </c:if>
        </div>
    </form>
</div>

