package board.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/download.do")
public class FileDownloadController extends HttpServlet {
	private static String ARTICLE_IMAGE_REPO = "C:\\JSP_article_image";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doHandle(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doHandle(req, resp);
	}

	private void doHandle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html; charset=utf-8");
		String imageFileName = (String) req.getParameter("imageFileName");
		String articleNO = req.getParameter("articleNO");
		System.out.println("imageFileName="+imageFileName);
		OutputStream out = resp.getOutputStream();
		String path = ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + imageFileName;	//�� ��ȣ�� ���� ���� ���
		File imageFile = new File(path);
		
		resp.setHeader("Cache-Control", "no-cache");
		resp.addHeader("Content-disposition", "attachment;fileName=" + imageFileName); //�̹��� ������ ���� �޴µ� �ʿ��� response ��� ����
		FileInputStream in =new FileInputStream(imageFile);
		//���۸� �̿��� 8kb�� ����
		byte[] buffer = new byte[1024 * 8];
		while(true) {
			int count = in.read(buffer);
			if (count == -1) 
				break;
			out.write(buffer, 0, count);
			
		}
		in.close();
		out.close();
	}
	
	
}
