<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${param.totalPages > 1}">
    <div class="pagination-container">
        <div class="pagination-info">
            Showing ${(param.currentPage - 1) * param.pageSize + 1} -
            ${param.currentPage * param.pageSize > param.totalRecords ? param.totalRecords : param.currentPage * param.pageSize}
            of ${param.totalRecords} results&nbsp;&nbsp;
        </div>

        <ul class="pagination">
            <c:choose>
                <c:when test="${param.currentPage > 1}">
                    <li class="page-item">
                        <a class="page-link"
                           href="${param.href}?page=${param.currentPage - 1}&limit=${param.pageSize}&sort=${param.sort}&direction=${param.direction}">
                            <i class="bi bi-chevron-left"></i>
                        </a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li class="page-item disabled">
                        <span class="page-link">
                            <i class="bi bi-chevron-left"></i>
                        </span>
                    </li>
                </c:otherwise>
            </c:choose>

            <c:if test="${param.currentPage > 3}">
                <li class="page-item">
                    <a class="page-link"
                       href="${param.href}?page=1&limit=${param.pageSize}&sort=${param.sort}&direction=${param.direction}">
                        1
                    </a>
                </li>
                <c:if test="${param.currentPage > 4}">
                    <li class="page-item disabled">
                        <span class="page-link">...</span>
                    </li>
                </c:if>
            </c:if>

            <c:forEach begin="${param.currentPage - 2 < 1 ? 1 : param.currentPage - 2}"
                       end="${param.currentPage + 2 > param.totalPages ? param.totalPages : param.currentPage + 2}"
                       var="i">
                <c:choose>
                    <c:when test="${i == param.currentPage}">
                        <li class="page-item active">
                            <span class="page-link">${i}</span>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="page-item">
                            <a class="page-link"
                               href="${param.href}?page=${i}&limit=${param.pageSize}&sort=${param.sort}&direction=${param.direction}">
                                ${i}
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${param.currentPage < param.totalPages - 2}">
                <c:if test="${param.currentPage < param.totalPages - 3}">
                    <li class="page-item disabled">
                        <span class="page-link">...</span>
                    </li>
                </c:if>
                <li class="page-item">
                    <a class="page-link"
                       href="${param.href}?page=${param.totalPages}&limit=${param.pageSize}&sort=${param.sort}&direction=${param.direction}">
                        ${param.totalPages}
                    </a>
                </li>
            </c:if>

            <c:choose>
                <c:when test="${param.currentPage < param.totalPages}">
                    <li class="page-item">
                        <a class="page-link"
                           href="${param.href}?page=${param.currentPage + 1}&limit=${param.pageSize}&sort=${param.sort}&direction=${param.direction}">
                            <i class="bi bi-chevron-right"></i>
                        </a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li class="page-item disabled">
                        <span class="page-link">
                            <i class="bi bi-chevron-right"></i>
                        </span>
                    </li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
</c:if>

