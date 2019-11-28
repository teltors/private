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

public class UpdateServlet extends HttpServlet{

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
		String id = req.getParameter("id");
		
		PrintWriter out = resp.getWriter();
		out.print("<html><head></head><body>");
		
		try {
			conn=DBconnection.getConnection();
			String sql = "SELECT * FROM member WHERE id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			rs.next();
			String pwd = rs.getString("pwd");
			String email = rs.getString("email");
			String phone = rs.getString("phone");		
				out.print("<form method='post' action='updateok.do'>");
				out.print("<input type='hidden' name='id' value='"+id+"'/>");
				out.print("아이디<input type='text' name='id' value='"+id+"'disabled='disabled'/><br/>");
				out.print("비밀번호<input type='text' name='pwd' value='"+pwd+"'/><br/>");
				out.print("email<input type='text' name='email' value='"+email+"'/><br/>");
				out.print("phone<input type='text' name='phone' value='"+phone+"'/><br/>");
				out.print("<input type='submit' value='저장'/><br/>");
				out.print("</form>");
			
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
