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
		String path = ARTICLE_IMAGE_REPO + "\\" + articleNO + "\\" + imageFileName;	//글 번호에 대한 파일 경로
		File imageFile = new File(path);
		
		resp.setHeader("Cache-Control", "no-cache");
		resp.addHeader("Content-disposition", "attachment;fileName=" + imageFileName); //이미지 파일을 내려 받는데 필요한 response 헤더 정보
		FileInputStream in =new FileInputStream(imageFile);
		//버퍼를 이용해 8kb씩 전송
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
