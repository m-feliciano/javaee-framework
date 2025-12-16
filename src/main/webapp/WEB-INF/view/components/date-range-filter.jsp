<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="date-range-filter">
    <form id="dateRangeForm" action="${param.action}" method="get">
        <div class="grid-container grid-auto-fit grid-gap-md" style="align-items: end;">
            <div class="form-group" style="margin-bottom: 0;">
                <label class="form-label" for="startDate">
                    <i class="bi bi-calendar-event"></i> From
                </label>
                <input type="date"
                       id="startDate"
                       name="startDate"
                       class="form-control"
                       value="${param.startDate}"
                       max="${param.endDate != null ? param.endDate : ''}">
            </div>

            <div class="form-group" style="margin-bottom: 0;">
                <label class="form-label" for="endDate">
                    <i class="bi bi-calendar-check"></i> To
                </label>
                <input type="date"
                       id="endDate"
                       name="endDate"
                       class="form-control"
                       value="${param.endDate}"
                       min="${param.startDate != null ? param.startDate : ''}">
            </div>

            <div class="grid-container" style="grid-template-columns: auto auto; gap: var(--spacing-2); margin-bottom: 0;">
                <button type="submit" class="btn btn-primary">
                    <i class="bi bi-funnel"></i> Filter
                </button>
                <c:if test="${not empty param.startDate or not empty param.endDate}">
                    <a href="${param.onclear}" class="btn btn-secondary">
                        <i class="bi bi-x-circle"></i> Clear
                    </a>
                </c:if>
            </div>
        </div>

        <c:if test="${not empty param.limit}">
            <input type="hidden" name="limit" value="${param.limit}">
        </c:if>
        <c:if test="${not empty param.sort}">
            <input type="hidden" name="sort" value="${param.sort}">
        </c:if>
        <c:if test="${not empty param.order}">
            <input type="hidden" name="order" value="${param.order}">
        </c:if>
    </form>
</div>

