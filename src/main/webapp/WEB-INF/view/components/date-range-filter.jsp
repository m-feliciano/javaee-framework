<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="date-range-filter">
    <form id="dateRangeForm" action="${param.action}" method="get">
        <div class="date-range-wrapper">
            <div class="date-input-group">
                <label class="form-label" for="startDate">
                    <i class="bi bi-calendar-event"></i> From
                </label>
                <input type="date"
                       id="startDate"
                       name="startDate"
                       class="form-control date-input"
                       value="${param.startDate}"
                       max="${param.endDate != null ? param.endDate : ''}">
            </div>

            <div class="date-input-group">
                <label class="form-label" for="endDate">
                    <i class="bi bi-calendar-check"></i> To
                </label>
                <input type="date"
                       id="endDate"
                       name="endDate"
                       class="form-control date-input"
                       value="${param.endDate}"
                       min="${param.startDate != null ? param.startDate : ''}">
            </div>

            <div class="date-range-actions">
                <button type="submit" class="btn btn-primary btn-filter">
                    <i class="bi bi-funnel"></i> Filter
                </button>
                <c:if test="${not empty param.startDate or not empty param.endDate}">
                    <a href="${param.onclear}" class="btn btn-outline btn-clear">
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
<style>
    .date-range-filter {
        background: var(--card-background);
        border: 1px solid var(--border-color);
        border-radius: 8px;
        padding: 1.25rem;
        margin-bottom: 1.5rem;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    }

    .date-range-wrapper {
        display: flex;
        gap: 1rem;
        flex-wrap: wrap;
        align-items: flex-end;
    }

    .date-input-group {
        display: flex;
        flex-direction: column;
        min-width: 160px;
    }

    .date-input-group .form-label {
        margin-bottom: 0.5rem;
        font-weight: 500;
        font-size: 0.9rem;
        color: var(--text-color);
    }

    .date-input-group .form-label i {
        margin-right: 0.25rem;
    }

    .date-input {
        padding: 0.5rem 0.75rem;
        border: 1px solid var(--border-color);
        border-radius: 6px;
        font-size: 0.95rem;
        background-color: var(--input-background);
        color: var(--text-color);
        transition: all 0.2s;
    }

    .date-input:focus {
        outline: none;
        border-color: var(--primary-color);
        box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.1);
    }

    .date-range-actions {
        display: flex;
        gap: 0.5rem;
        align-items: flex-end;
        flex-wrap: wrap;
    }

    .btn-filter {
        padding: 0.5rem 1.25rem;
        font-weight: 500;
    }

    @keyframes spin {
        from { transform: rotate(0deg); }
        to { transform: rotate(360deg); }
    }

    .btn-clear {
        padding: 0.5rem 0.875rem;
        border: 1px solid var(--border-color);
        color: var(--text-muted);
        transition: all 0.2s;
    }

    .btn-clear:hover {
        background-color: var(--danger-color);
        color: white;
        border-color: var(--danger-color);
    }

    @media (max-width: 768px) {
        .date-range-wrapper {
            flex-direction: column;
            align-items: stretch;
        }

        .date-input-group {
            min-width: 100%;
        }

        .date-range-actions {
            width: 100%;
            justify-content: space-between;
        }
    }

    @media (prefers-color-scheme: dark) {
        .date-range-filter {
            background: var(--card-background-dark);
            border-color: var(--border-color-dark);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
        }

        .date-input {
            background-color: var(--input-background-dark);
            border-color: var(--border-color-dark);
            color: var(--text-color-dark);
        }

        .date-input::-webkit-calendar-picker-indicator {
            filter: invert(1);
        }
    }
</style>

