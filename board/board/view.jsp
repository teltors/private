<%@ page contentType="text/html; charset=utf-8" import="java.sql.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%@ page import="mvc.model.BoardDTO"%>

<%
	BoardDTO notice = (BoardDTO) request.getAttribute("board");
	int num = ((Integer) request.getAttribute("num")).intValue();
	int nowpage = ((Integer) request.getAttribute("page")).intValue();
%>
<html>
<head>
<script type="text/javascript">
	function checkForm() {
		if (!document.newUpdate.comment.value) {
			alert("내용을 입력하세요");
			return false;
		}
	}
</script>
<link rel="stylesheet" href="./resources/css/bootstrap.min.css" />
<title>Board</title>
</head>
<body>
	<jsp:include page="../menu.jsp" />
	<div class="jumbotron">
		<div class="container">
			<h1 class="display-3">게시판</h1>
		</div>
	</div>

	<div class="container">
		<form name="newUpdate"
			action="BoardUpdateAction.do?num=<%=notice.getNum()%>&pageNum=<%=nowpage%>"
			class="form-horizontal" method="post" onsubmit="return checkForm()">
			<div class="form-group row">
				<label class="col-sm-2 control-label" >성명</label>
				<div class="col-sm-3">
					<input name="name" class="form-control"	value=" <%=notice.getName()%>" readonly>
				</div>
			</div>
			<div class="form-group row">
				<label class="col-sm-2 control-label" >제목</label>
				<div class="col-sm-5">
					<input name="subject" class="form-control"	value=" <%=notice.getSubject()%>" >
				</div>
			</div>
			<div class="form-group row">
				<label class="col-sm-2 control-label" >내용</label>
				<div class="col-sm-8" style="word-break: break-all;">
					<textarea name="content" class="form-control" cols="50" rows="5"> <%=notice.getContent()%></textarea>
				</div>
			</div>
			<div class="form-group row">
				<label class="col-sm-2 control-label" ></label>
				<div class="col-sm-offset-2 col-sm-10 ">
					<c:set var="userId" value="<%=notice.getId()%>" />
					<c:if test="${sessionId==userId}">
						<p>
							<a	href="./BoardDeleteAction.do?num=<%=notice.getNum()%>&pageNum=<%=nowpage%>"	class="btn btn-danger"> 삭제</a> 
							<input type="submit" class="btn btn-success" value="수정 ">
					</c:if>
					<a href="./BoardListAction.do?pageNum=<%=nowpage%>"		class="btn btn-primary"> 목록</a>
				</div>
			</div>
				<!-- 댓글 -->
				<%
				String nonono = request.getParameter("num");
				Connection conn = null;
				Statement stmt = null;
				ResultSet rs1 = null;
				try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pepper","root","1111");
				stmt = conn.createStatement();
			
				%>
				
				
				
				
				<div class="form-group row">
				<label class="col-sm-2 control-label" ></label>
				<div class="col-sm-8" style="word-break: break-all;">
					<input type="hidden" name="id" value="<%=notice.getId()%>" />
					<input class="form-control col-sm-8" type="text" name="comment" />				 
					<input type="hidden" name="no" value="<%=Integer.parseInt(nonono)%>" />
					<input type="hidden" name="nowpage" value="<%=nowpage%>" />
					<input class="btn btn-warning col-sm-8" type="submit" formaction="./board/commentaction.jsp" value="댓글작성" />			 
					</div>
				</div>
				<div class="form-group">
				<label class="col-sm-2 control-label" ></label>
				<div class="col-sm-8" style="word-break: break-all;">
				<%
				String query1 = "select content, id from comment where parent_num="+Integer.parseInt(nonono);
				rs1 = stmt.executeQuery(query1);
				while(rs1.next()) {
				%>
				<b><%=rs1.getString("id") %> : <%=rs1.getString("content")%></b><br>
				 		 
				<%
				}
				rs1.close();
				%>
				 				 
				<%				 
				}catch(Exception e) {
				e.printStackTrace();
				}finally {
					stmt.close();
					conn.close();			
				}
				%>
				</div>
				</div>
				
			
		</form>
		<hr>
	</div>
	<jsp:include page="../footer.jsp" />
</body>
</html>


