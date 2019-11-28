<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String name = (String) request.getAttribute("name");
%>
<!-- 불법적 접근 막기 -->
<%
String sessionId=(String) session.getAttribute("sessionId");
if(sessionId==null){ %>
<script language="javascript">
	alert("로그인 해주세요.");
	location.href="./loginMember.jsp";
</script>
<% return; } %>
<!-- //불법적 접근 막기 -->
<html>
<head>
<link rel="stylesheet" href="./resources/css/bootstrap.min.css" />
<title>Board</title>
</head>
<script type="text/javascript">
	function checkForm() {
		if (!document.newWrite.subject.value) {
			alert("제목을 입력하세요.");
			return false;
		}
		if (!document.newWrite.content.value) {
			alert("내용을 입력하세요.");
			return false;
		}
		alert("작성이 완료되었습니다.");
		location.href="./board/list.jsp";
	}
</script>
<!-- 새로고침 불가 상태로 만들기 -->
<script language='javascript'>

function noEvent() {
if (event.keyCode == 116) {
event.keyCode= 2;
return false;
}
else if(event.ctrlKey && (event.keyCode==78 || event.keyCode == 82))
{
return false;
}
}
document.onkeydown = noEvent;

</script>

<body>
	<jsp:include page="../menu.jsp" />
	<div class="jumbotron">
		<div class="container">
			<h1 class="display-3">게시판</h1>
		</div>
	</div>

	<div class="container">

		<form name="newWrite" action="./BoardWriteAction.do"
			class="form-horizontal" method="post" onsubmit="return checkForm()">
			<input name="id" type="hidden" class="form-control"
				value="${sessionId}">
			<div class="form-group row">
				<label class="col-sm-2 control-label" >성명</label>
				<div class="col-sm-3">
					<input name="name" type="text" class="form-control" value="<%=name %>"
						placeholder="name" readonly>
				</div>
			</div>
			<div class="form-group row">
				<label class="col-sm-2 control-label" >제목</label>
				<div class="col-sm-5">

					<input name="subject" type="text" class="form-control"
						placeholder="subject">
				</div>
			</div>
			<div class="form-group row">
				<label class="col-sm-2 control-label" >내용</label>
				<div class="col-sm-8">
					<textarea name="content" cols="50" rows="5" class="form-control"
						placeholder="content"></textarea>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-offset-2 col-sm-10 ">
				 <input type="submit" class="btn btn-primary " value="등록 ">				
					 <input type="button" class="btn btn-primary " value="취소 " onClick="window.href('./BoardListAction.do')">
				</div>
			</div>
		</form>
		<hr>
	</div>
	<jsp:include page="../footer.jsp" />
</body>
</html>





