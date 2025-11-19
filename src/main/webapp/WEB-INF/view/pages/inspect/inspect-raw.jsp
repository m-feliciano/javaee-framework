<%@ page import="com.dev.servlet.core.response.IHttpResponse" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="/WEB-INF/routes/inspect-routes.jspf" %>
<jsp:include page="/WEB-INF/view/components/header.jsp"/>

<link rel="stylesheet"
      href="https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@11.8.0/build/styles/atom-one-dark.min.css">
<link rel="stylesheet" href="<c:url value='/resources/css/inspect.css'/>"/>

<%
    request.setAttribute("rawJson", ((IHttpResponse<?>) request.getAttribute("response")).body());
%>

<title>Inspect - Raw</title>

<div class="main">
    <div class="action-bar"
         style="background: linear-gradient(90deg, rgba(255,255,255,0.01), rgba(255,255,255,0.02)); padding:12px; border-radius:8px;">
        <div class="action-bar-title">
            <h1><i class="bi bi-file-earmark-code-fill"></i> Inspect (Raw)</h1>
            <p class="action-bar-subtitle">Raw JSON Data</p>
        </div>
        <div class="action-buttons">
            <a href="${baseLink}${version}${inspectPage}" class="btn btn-secondary">
                <i class="bi bi-box-arrow-left"></i>
                Back
            </a>
            <button id="copyBtn" class="btn btn-info">
                <i class="bi bi-clipboard"></i>
                Copy
            </button>
            <a id="downloadLink" class="btn btn-success" href="#" download="inspect.json">
                <i class="bi bi-download"></i>
                Download
            </a>
        </div>
    </div>

    <div class="card" style="margin-top:16px;">
        <div id="raw-container">
                <pre id="rawJson" class="raw-json"
                     style="background: linear-gradient(180deg,#071025 0%,#08151c 60%); color: #cfe9ff; padding:18px 18px 18px 56px; border-radius:10px; box-shadow: inset 0 6px 18px rgba(2,6,23,0.6); border:1px solid rgba(255,255,255,0.04);"><c:out
                        value="${rawJson}" escapeXml="true"/></pre>
        </div>
    </div>
</div>

<div id="toast" class="toast"></div>
<script src="<c:url value='/resources/js/pretty-json.js'/>"></script>
<script src="<c:url value='/resources/js/inspect-raw.js'/>"></script>
<jsp:include page="/WEB-INF/view/components/footer.jsp"/>
