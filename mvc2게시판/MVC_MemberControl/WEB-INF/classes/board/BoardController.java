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
	private static String ARTICLE_IMAGE_REPO = "C:\\JSP_article_image"; //�̹��� ���� ��ġ�� ����� ����
	BoardService boardService;
	ArticleVO articleVO;
	
	public void init(ServletConfig config) throws ServletException{
		boardService =new BoardService(); //���� �ʱ�ȭ�� BoardService��ü ����
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
		
		String action = req.getPathInfo(); //��û��������
		System.out.println("action: " + action);
		
		try {
			List<ArticleVO> articlesList = new ArrayList<ArticleVO>(); //�迭ȭ
			if (action==null) { //��û���� null�̸� board list �������� �̵�
				articlesList = boardService.listArticles();
				req.setAttribute("articlesList", articlesList);
				nextPage = "/mvc2_board/listArticles.jsp";  
				
			} else if (action.equals("/listArticles.do")) {  //listArticles.do��û�̸� board list �������� �̵�
				articlesList = boardService.listArticles();
				req.setAttribute("articlesList", articlesList);
				nextPage = "/mvc2_board/listArticles.jsp";  
				
			} else if (action.equals("/articleForm.do")) { // �۾��� â���� �̵�
				nextPage= "/mvc2_board/articleForm.jsp";
				
			} else if (action.equals("/addArticle.do")) {  //���� �߰�
				int articleNO=0;
				Map<String, String> articleMap = upload(req, resp);  //���� ���ε� ����� ����ϱ����� upload()�� ��û�� ����
				String title = articleMap.get("title");		//aritcleMap�� ����� �� ������ �ٽ� ������
				String content = articleMap.get("content");
				String imageFileName = articleMap.get("imageFileName");
				
				articleVO.setParentNO(0); //�� ���� �θ�� ��ȣ�� 0���� ����
				articleVO.setId("hong");
				articleVO.setTitle(title);
				articleVO.setContent(content);
				articleVO.setImageFileName(imageFileName);
				articleNO = boardService.addArticle(articleVO);  //���̺� �� ���� �߰��� �� �� �ۿ� ���� �� ��ȣ�� ������
				
				if(imageFileName != null && imageFileName.length() != 0) {	//������ ÷���� ��쿡�� ����
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp" + "\\" +imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" + articleNO);
					destDir.mkdirs(); //�۹�ȣ�� ���� ����
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
				}
				PrintWriter pw = resp.getWriter();
				// ���� ��� �޽����� ��Ÿ�� �� �ڹٽ�ũ��Ʈ location��ü�� href �Ӽ��� �̿��� �� ��� ��û
				pw.print("<script>" 
				         +"  alert('������ �߰��߽��ϴ�.');" 
						 +" location.href='"+req.getContextPath()+"/board/listArticles.do';"
				         +"</script>");
							
				return;
				
			} else if(action.equals("/viewArticle.do")) {		//�� ��â
				String articleNO = req.getParameter("articleNO");
				articleVO=boardService.viewArticle(Integer.parseInt(articleNO));
				req.setAttribute("article", articleVO);
				nextPage="/mvc2_board/viewArticle.jsp";
				
			}else if(action.equals("/modArticle.do")) {		//�� ����
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
				boardService.modArticle(articleVO);		//modArticle�� ���� ����
				if(imageFileName != null && imageFileName.length() != 0) {
					String originalFileName = articleMap.get("originalFileName");
					File srcFile = new File(ARTICLE_IMAGE_REPO + "\\" + "temp"+ "\\" + imageFileName);
					File destDir = new File(ARTICLE_IMAGE_REPO + "\\" +articleNO);
					destDir.mkdirs();
					FileUtils.moveFileToDirectory(srcFile, destDir, true);
					
					File oldFile = new File(ARTICLE_IMAGE_REPO + "\\" +articleNO + "\\" + originalFileName);	//���۵� originalFileName���� ������ ���� ����
					oldFile.delete();
				}
				PrintWriter pw = resp.getWriter();
				pw.print("<script>" 
				         +"  alert('���� �����߽��ϴ�.');" 
						 +" location.href='"+req.getContextPath()+"/board/viewArticle.do?articleNO=" +articleNO + "';"
				         +"</script>");
				return;
				
			}else if(action.equals("/removeArticle.do")) {	//�� ����
				int articleNO = Integer.parseInt(req.getParameter("articleNO"));
				List<Integer> articleNOList = boardService.removeArticle(articleNO); //articleNO ���� ���� ���� ������ �� ������ �θ�۰� �ڽ� ����articleNO ��� ������
				for(int _articleNO : articleNOList) {
					File imgDir = new File(ARTICLE_IMAGE_REPO + "\\" + _articleNO);
					if(imgDir.exists()) {
						FileUtils.deleteDirectory(imgDir);		//�������� �̹��� ���� ����
					}
				}
				
				PrintWriter pw = resp.getWriter();
				pw.print("<script>" 
				         +"  alert('���� �����߽��ϴ�.');" 
						 +" location.href='"+req.getContextPath()+"/board/listArticles.do';"
				         +"</script>");
				return;
				
			}else if(action.equals("/replyForm.do")) {	//��� ��û
				int parentNO = Integer.parseInt(req.getParameter("parentNO"));
				session = req.getSession();		//���â ��û�� �̸� �θ� �� ��ȣ�� parentNO �Ӽ����� ���ǿ� ����
				session.setAttribute("parentNO", parentNO);
				nextPage = "/mvc2_board/replyForm.jsp";
				
			}else if(action.equals("/addReply.do")) {	//��� �߰�
				session = req.getSession();
				int parentNO = (Integer) session.getAttribute("parentNO");
				session.removeAttribute("parentNO");		//��� ���� �� ���ǿ� ����� parentNO�� �����ɴϴ�.
				
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
				         +"  alert('����� �߰��߽��ϴ�.');" 
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

	//���� ���ε� ���
	private Map<String, String> upload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, String> articleMap = new HashMap<String, String>();
		String encoding = "utf-8";
		File currentDirPath = new File(ARTICLE_IMAGE_REPO); //��ġ
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(currentDirPath);
		factory.setSizeThreshold(1024*1024); //1MB
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List items = upload.parseRequest(req);
			for(int i = 0; i < items.size(); i++) {
				FileItem fileItem = (FileItem) items.get(i);
				if(fileItem.isFormField()) {	//�� �ʵ� �̸� ���۵� �Ű����� ���� ���
					System.out.println(fileItem.getFieldName() +"="+ fileItem.getString(encoding));
					//���� ���ε�� ���� ���۵� �� �� ���� �Ű������� Map�� (key, value)�������� �� ��ȯ�ϰ�, ���۰� ���õ� title, content�� Map�� ����
					articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));  
				}else {	//�� �ʵ尡 �ƴϸ� ���� ���ε� ��� ����
					System.out.println("�Ķ�����̸�:"+fileItem.getFieldName());
					System.out.println("�����̸�:"+fileItem.getName());
					System.out.println("����ũ��:"+fileItem.getSize()+"bytes");
					//���ε�� ������ ���� �̸��� Map�� ("imageFileName","���ε� �����̸�")����  ����
					articleMap.put(fileItem.getFieldName(), fileItem.getName());
					if(fileItem.getSize() > 0) {	//������ �����ϴ� ��� ���ε��� ������ ���� �̸��� ����ҿ� ���ε�
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












