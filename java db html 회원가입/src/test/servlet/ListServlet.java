package test.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ListServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		dolist(req, resp);
		}

		@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			dolist(req, resp);
		}
		
	protected void dolist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=utf-8");
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;
		
		PrintWriter out = resp.getWriter();
		out.print("<html><head></head><body>");
		
		try {
			conn=DBconnection.getConnection();
			String sql = "SELECT * FROM member";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			out.print("<div><table border='1' width='1200'>");
			out.print("<tr><td>아이디</td><td>비밀번호</td><td>이메일</td><td>전화번호</td><td>삭제</td><td>수정</td></tr>");
			while(rs.next()) {
				String id = rs.getString("id");
				String pwd = rs.getString("pwd");
				String email = rs.getString("email");
				String phone = rs.getString("phone");
				out.print("<tr>");
				out.print("<td>"+id+"</td>");
				out.print("<td>"+pwd+"</td>");
				out.print("<td>"+email+"</td>");
				out.print("<td>"+phone+"</td>");
				out.print("<td><a href='delete.do?id="+id+"'>삭제</a></td>");
				out.print("<td><a href='update.do?id="+id+"'>수정</a></td>");
				out.print("</tr>");
			}
			out.print("</table></div><a href='main.html'>메인페이지로</a>");
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
				if(conn!=null) conn.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		out.print("</body></html>");
		out.close();
	}
	
	
}
