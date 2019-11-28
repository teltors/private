<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%@ page import="com.oreilly.servlet.*" %>
<%@ page import="com.oreilly.servlet.multipart.*" %>
<%@ page import="java.util.*" %> <!-- Enumeration 사용을 위해 -->
<%@ page import="java.sql.*" %>
<%@ include file="../dbconn.jsp"%>
<%
	request.setCharacterEncoding("UTF-8");

	String sessionId=(String) session.getAttribute("sessionId");
	String id = request.getParameter("id");
	String comment = request.getParameter("comment");
	String no = request.getParameter("no");
	String nowpage = request.getParameter("nowpage");
	
	PreparedStatement pstmt = null;
	String sql = "INSERT INTO comment values(?,?,?)";
	pstmt = conn.prepareStatement(sql);
	pstmt.setString(1, sessionId);
	pstmt.setString(2, comment);
	pstmt.setInt(3,Integer.parseInt(no));
	pstmt.executeUpdate();	
	
	if(pstmt != null)
		pstmt.close();
	if(conn != null)
		conn.close();
	
	response.sendRedirect("/BoardViewAction.do?num="+no+"&pageNum="+nowpage);
%>


