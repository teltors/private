package test.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpdateOkServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		dolist(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		dolist(req, resp);
	}
	
	protected void dolist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		String id = req.getParameter("id");
		String pwd = req.getParameter("pwd");
		String email = req.getParameter("email");
		String phone = req.getParameter("phone");
		PreparedStatement pstmt = null;
		Connection conn = null;
		
		try {
			conn=DBconnection.getConnection();
			String sql = "UPDATE member SET pwd=?,email=?,phone=? WHERE id =?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, pwd);
			pstmt.setString(2, email);
			pstmt.setString(3, phone);
			pstmt.setString(4, id);
			pstmt.executeUpdate();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			
				try {
					if(pstmt!=null) pstmt.close();
					if(conn!=null) conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out =  resp.getWriter();
		out.print("<html>");
		out.print("<head></head>");
		out.print("<body>");
		out.print("수정되었습니다.<br>");
		out.print("<a href='main.html'>메인페이지로 이동</a>");
		out.print("</body>");
		out.print("</html>");
		out.close();
		}

}


