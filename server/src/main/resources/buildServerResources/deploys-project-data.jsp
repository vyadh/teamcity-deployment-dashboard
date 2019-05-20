<%@ page contentType="application/json;charset=UTF-8" language="java" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
{
  "deploys: ": [
    <c:forEach items="${deploys}" var="deploy" varStatus="loop">
    {
      "name": <c:out value="${deploy.project}"/>,
      "version": <c:out value="${deploy.version}"/>,
      "environment": <c:out value="${deploy.environment}"/>,
      "time": <c:out value="${deploy.time}"/>,
      "status": <c:out value="${deploy.status}"/>,
      "link": <c:out value="${deploy.link}"/>
    }<c:if test="${!loop.last}">,</c:if>
    </c:forEach>
  ]
}
