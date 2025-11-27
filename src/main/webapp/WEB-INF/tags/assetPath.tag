<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="name" required="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:choose>
  <c:when test="${not empty applicationScope.assetsManifest && not empty applicationScope.assetsManifest[name]}">
    <c:set var="assetPath" value="${applicationScope.assetsManifest[name]}"/>
  </c:when>
  <c:otherwise>
    <c:set var="assetPath" value="${name}"/>
  </c:otherwise>
</c:choose>
<c:out value="${pageContext.request.contextPath}/resources/dist/${assetPath}"/>
