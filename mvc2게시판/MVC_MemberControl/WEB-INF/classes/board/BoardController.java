package board;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

@WebServlet("/board/*")
public class BoardController extends HttpServlet{
	private static String ARTICLE_IMAGE_REPO = "C:\\JSP_article_image"; //이미지 저장 위치를 상수로 선언
	BoardService boardService;
	ArticleVO articleVO;
	
	public void init(ServletConfig config) throws ServletException{
		boardService =new BoardService(); //서블릿 초기화시 BoardService객체 생성
		articleVO = new ArticleVO();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req, resp);
	}

	private void doHandle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String nextPage="";
		req.setCharacterEncoding("utf-8");
		resp.setContentType("text/html; charset=utf-8");
		HttpSession session;
		
		String action = req.getPathInfo(); //요청명가져오기
		System.out.println("action: " + action);
		
		try {
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>(); //배열화
			if (action==null) { //요청명이 null이면 board list 페이지로 이동
				articlesList = boardService.listArticles();
				req.setAttribute("articlesList", articlesList);
				nextPage = "/mvc2_board/listArticles.jsp";  
				
			} else if (action.equals("/listArticles.do")) {  //listArticles.do요청이면 board list 페이지로 이동
				articlesList = boardService.listArticles();
				req.setAttribute("articlesList", articlesList);
				nextPage = "/mvc2_board/listArticles.jsp";  
				
			} else if (action.equals("/articleForm.do")) { // 글쓰기 창으로 이동
				nextPage= "/mvc2_board/articleForm.jsp";
				
			} else if (action.equals("/addArticle.do")) {  //새글 추가
				int articleNO=0;
				Map<String, String> articleMap = upload(req, resp);  //파일 업로드 기능을 사용하기위해 upload()로 요청을 전달
				String title = articleMap.get("title");		//aritcleMap에 저장된 글 정보를 다시 가져옴
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				
				articleVO.setParentNO(0); //새 글의 부모글 번호를 0으로 설정
				articleVO.setId("hong");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				articleNO = boardService.addArticle(articleVO);  //테이블에 새 글을 추가한 후 새 글에 대한 글 번호를 가져옴
				
				if(imageFileName != null && imageFileName.length() != 0) {	//파일을 첨부한 경우에만 수행
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" +imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs(); //글번호로 폴더 생성
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
				PrintWriter pw = resp.getWriter();
				// 새글 등록 메시지를 나타낸 후 자바스크립트 location객체의 href 속성을 이용해 글 목록 요청
				pw.print("<script>" 
				         +"  alert('새글을 추가했습니다.');" 
						 +" location.href='"+req.getContextPath()+"/board/listArticles.do';"
				         +"</script>");
							
				return;
				
			} else if(action.equals("/viewArticle.do")) {		//글 상세창
				String articleNO = req.getParameter("articleNO");
				articleVO=boardService.viewArticle(Integer.parseInt(articleNO));
				req.setAttribute("article", articleVO);
				nextPage="/mvc2_board/viewArticle.jsp";
				
			}else if(action.equals("/modArticle.do")) {		//글 수정
				Map<String, String> articleMap = upload(req, resp);
				int articleNO = Integer.parseInt(articleMap.get("articleNO"));
				articleVO.setArticleNO(articleNO);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				articleVO.setParentNO(0);
				articleVO.setId("hong");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				boardService.modArticle(articleVO);		//modArticle로 정보 전송
				if(imageFileName != null && imageFileName.length() != 0) {
					String originalFileName = articleMap.get("originalFileName");
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp"+ "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" +articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
					
					File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" +articleNO + "\\" + originalFileName);	//전송된 originalFileName으로 기존의 파일 삭제
					oldFile.delete();
				}
				PrintWriter pw = resp.getWriter();
				pw.print("<script>" 
				         +"  alert('글을 수정했습니다.');" 
						 +" location.href='"+req.getContextPath()+"/board/viewArticle.do?articleNO=" +articleNO + "';"
				         +"</script>");
				return;
				
			}else if(action.equals("/removeArticle.do")) {	//글 제거
				int articleNO = Integer.parseInt(req.getParameter("articleNO"));
				List<Integer> articleNOList = boardService.removeArticle(articleNO); //articleNO 값에 대한 글을 삭제한 후 삭제된 부모글과 자식 글의articleNO 목록 가저옴
				for(int _articleNO : articleNOList) {
					File imgDir = new File(ARTICLE_IMAGE_REPO + "\\" + _articleNO);
					if(imgDir.exists()) {
						FileUtils.deleteDirectory(imgDir);		//삭제글의 이미지 폴더 삭제
					}
				}
				
				PrintWriter pw = resp.getWriter();
				pw.print("<script>" 
				         +"  alert('글을 삭제했습니다.');" 
						 +" location.href='"+req.getContextPath()+"/board/listArticles.do';"
				         +"</script>");
				return;
				
			}else if(action.equals("/replyForm.do")) {	//답글 요청
				int parentNO = Integer.parseInt(req.getParameter("parentNO"));
				session = req.getSession();		//답글창 요청시 미리 부모 글 번호를 parentNO 속성으로 세션에 저장
				session.setAttribute("parentNO", parentNO);
				nextPage = "/mvc2_board/replyForm.jsp";
				
			}else if(action.equals("/addReply.do")) {	//답글 추가
				session = req.getSession();
				int parentNO = (Integer) session.getAttribute("parentNO");
				session.removeAttribute("parentNO");		//답글 전송 시 세션에 저장된 parentNO를 가져옵니다.
				
				Map<String, String> articleMap = upload(req, resp);
				String title = articleMap.get("title");
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				articleVO.setParentNO(parentNO);
				articleVO.setId("lee");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				int articleNO = boardService.addReply(articleVO);
				if(imageFileName != null && imageFileName.length() != 0) {
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
							
				}
				PrintWriter pw = resp.getWriter();
				pw.print("<script>" 
				         +"  alert('답글을 추가했습니다.');" 
						 +" location.href='"+req.getContextPath()+"/board/viewArticle.do?articleNO="+articleNO+"';"
				         +"</script>");
				return;
			}
			
			RequestDispatcher dispatch = req.getRequestDispatcher(nextPage);
			dispatch.forward(req, resp);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	//파일 업로드 기능
	private Map<String, String> upload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, String> articleMap = new HashMap<String, String>();
		String encoding = "utf-8";
		File currentDirPath = new File(ARTICLE_IMAGE_REPO); //위치
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(currentDirPath);
		factory.setSizeThreshold(1024*1024); //1MB
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List items = upload.parseRequest(req);
			for(int i = 0; i < items.size(); i++) {
				FileItem fileItem = (FileItem) items.get(i);
				if(fileItem.isFormField()) {	//폼 필드 이면 전송된 매개변수 값을 출력
					System.out.println(fileItem.getFieldName() +"="+ fileItem.getString(encoding));
					//파일 업로드로 같이 전송된 새 글 관련 매개변수를 Map에 (key, value)로저장한 후 반환하고, 새글과 관련된 title, content를 Map에 저장
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));  
				}else {	//폼 필드가 아니면 파일 업로드 기능 수행
					System.out.println("파라미터이름:"+fileItem.getFieldName());
					System.out.println("파일이름:"+fileItem.getName());
					System.out.println("파일크기:"+fileItem.getSize()+"bytes");
					//업로드된 파일의 파일 이름을 Map에 ("imageFileName","업로드 파일이름")으로  저장
					articleMap.put(fileItem.getFieldName(), fileItem.getName());
					if(fileItem.getSize() > 0) {	//파일이 존재하는 경우 업로드한 파일의 파일 이름을 저장소에 업로드
						int idx=fileItem.getName().lastIndexOf("\\");
						if(idx==-1) {
							idx=fileItem.getName().lastIndexOf("/");
						}
						
						String fileName = fileItem.getName().substring(idx + 1);
						File uploadFile = new File(currentDirPath + "\\temp\\" + fileName);
						fileItem.write(uploadFile);
						
					}
					
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return articleMap;
	}
}












