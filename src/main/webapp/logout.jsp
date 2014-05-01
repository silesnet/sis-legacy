<%
  session.invalidate();
  response.sendRedirect("https://localhost:8443/login.jsp");
%>