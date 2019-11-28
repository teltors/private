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

public class DeleteServlet extends HttpServlet{

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
		
		PreparedStatement pstmt = null;
		Connection conn = null;
		
		try {
			conn=DBconnection.getConnection();
			String sql = "DELETE FROM member WHERE id =?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, id);
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
		out.print("삭제되었습니다.<br>");
		out.print("<a href='main.html'>메인페이지로 이동</a>");
		out.print("</body>");
		out.print("</html>");
		out.close();
		}

}
