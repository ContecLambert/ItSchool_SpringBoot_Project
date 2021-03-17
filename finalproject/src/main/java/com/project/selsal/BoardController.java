package com.project.selsal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.project.selsal.dao.BoardDao;
import com.project.selsal.entities.BoardPaging;
import com.project.selsal.entities.Freeboard;
import com.project.selsal.entities.Freeboardcomment;


@Controller
public class BoardController {
	
	@Autowired
	private SqlSession sqlSession;
	
	@Autowired
	Freeboard freeboard;
	
	@Autowired
	Freeboardcomment freeboardcomment;
	
	@Autowired
	BoardPaging boardpaging;
	
	public static String find;
	
	@RequestMapping(value = "/freeBoardWrite", method = RequestMethod.GET)
	public String freeBoard(Locale locale, Model model) {
		return "board/freeboard_write";
	}
	
	@RequestMapping(value = "/freeBoardWriteSave", method = RequestMethod.POST)
	public String freeboardWriteSave(Model model, @ModelAttribute Freeboard freeboard,
			@RequestParam("f_attachfile") MultipartFile f_attachfile, HttpServletRequest request) throws Exception {
		String filename = f_attachfile.getOriginalFilename();
		String path = "F:/SPRINGBOOTPROJECT/finalproject/src/main/resources/static/uploadattaches/";
		String realpath = "/uploadattaches/";
		if (!filename.equals("")) {
			byte bytes[] = f_attachfile.getBytes();
			try {
				BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(path + filename));
				output.write(bytes);
				output.flush();
				output.close();
				freeboard.setF_attach(realpath + filename);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm:ss");
		Date date = new Date();
		String today = df.format(date);
		freeboard.setF_inputtime(today);
		BoardDao dao = sqlSession.getMapper(BoardDao.class);
		dao.insertRowFreeBoard(freeboard);

		return "redirect:freeBoardList";
	}
	
	@RequestMapping(value = "/freeBoardList", method = RequestMethod.GET)
	public String freeBoardList(Locale locale, Model model) throws Exception {
		BoardDao dao = sqlSession.getMapper(BoardDao.class);

		int rowcount = dao.selectCountFirstFreeBoard();
		int pagesize = 10;
		int page = 1;
		int startrow = (page - 1) * pagesize;
		int endrow = 10;
		if (boardpaging.getFind() == null) {
			boardpaging.setFind("");
		}
		boardpaging.setStartrow(startrow);
		boardpaging.setEndrow(endrow);
		int absPage = 1;
		if (rowcount % pagesize == 0) {
			absPage = 0;
		}

		int pagecount = rowcount / pagesize + absPage;
		int pages[] = new int[pagecount];
		for (int i = 0; i < pagecount; i++) {
			pages[i] = i + 1;
		}

		ArrayList<Freeboard> boards = dao.selectPageListFreeBoard(boardpaging);

		model.addAttribute("rowcount",rowcount);
		model.addAttribute("boards", boards);
		model.addAttribute("pages", pages);
		return "/board/freeboard_list";
	}
	
	@RequestMapping(value = "/freeBoardDetail", method = RequestMethod.GET)
	public String freeBoardDetail(@RequestParam int f_seq, Model model, HttpSession session) throws Exception {
		BoardDao dao = sqlSession.getMapper(BoardDao.class);
		freeboard = dao.selectOneFreeBoard(f_seq);
		String cursession = (String) session.getAttribute("sessionemail");
		if (cursession == null || !cursession.equals(freeboard.getF_email())) {
			dao.addHitFreeBoard(f_seq);
		}
		int like = dao.selectLike(f_seq);
		freeboard.setF_sessionemail(cursession);
		int likecheck = dao.selectLikeCheck(freeboard);
		ArrayList<Freeboardcomment> freeboardcomments = dao.selectOneFreeBoardComment(f_seq);
		model.addAttribute("freeboard", freeboard);
		model.addAttribute("like",like);
		model.addAttribute("likecheck",likecheck);
		model.addAttribute("freeboardcomments",freeboardcomments);
		model.addAttribute("freeboardcomment",freeboardcomment);
		return "board/freeboard_detail";
	}
	
	@RequestMapping(value = "/fileDownloadFreeBoard")
	@ResponseBody
	public void fileDownloadFreeBoard(@RequestParam String f_attach, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		request.setCharacterEncoding("UTF-8");
		File file = new File(
				"F:/SPRINGBOOTPROJECT/finalproject/src/main/resources/static/"
						+ f_attach);
		String oriFileName = file.getName();
		String filePath = "F:/SPRINGBOOTPROJECT/finalproject/src/main/resources/static/uploadattaches/";
		InputStream in = null;
		OutputStream os = null;
		File newfile = null;
		boolean skip = false;
		String client = "";
		// 파일을 읽어 스트림에 담기
		try {
			newfile = new File(filePath, oriFileName);
			in = new FileInputStream(newfile);
		} catch (FileNotFoundException fe) {
			skip = true;
		}

		client = request.getHeader("User-Agent");
		response.reset();
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Description", "HTML Generated Data");

		if (!skip) {
			// IE
			if (client.indexOf("MSIE") != -1) {
				response.setHeader("Content-Disposition", "attachment; filename=\""
						+ java.net.URLEncoder.encode(oriFileName, "UTF-8").replaceAll("\\+", "\\ ") + "\"");
				// IE 11 이상.
			} else if (client.indexOf("Trident") != -1) {
				response.setHeader("Content-Disposition", "attachment; filename=\""
						+ java.net.URLEncoder.encode(oriFileName, "UTF-8").replaceAll("\\+", "\\ ") + "\"");
			} else {
				// 한글 파일명 처리
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + new String(oriFileName.getBytes("UTF-8"), "ISO8859_1") + "\"");
				response.setHeader("Content-Type", "application/octet-stream; charset=utf-8");
			}
			response.setHeader("Content-Length", "" + file.length());
			os = response.getOutputStream();
			byte b[] = new byte[(int) file.length()];
			int leng = 0;
			while ((leng = in.read(b)) > 0) {
				os.write(b, 0, leng);
			}
		} else {
			response.setContentType("text/html;charset=UTF-8");
			System.out.println("<script language='javascript'>alert('파일을 찾을 수 없습니다');history.back();</script>");
		}
		in.close();
		os.close();
	}
	
	@RequestMapping(value = "/freeBoardUpdateSave", method = RequestMethod.POST)
	public String freeBoardUpdateSave(@ModelAttribute Freeboard freeboard) throws Exception {
		BoardDao dao = sqlSession.getMapper(BoardDao.class);
		dao.updateRowFreeBoard(freeboard);
		return "redirect:freeBoardList";
	}
	
	@RequestMapping(value = "/freeBoardDelete", method = RequestMethod.GET)
	public String freeBoardDelete(@RequestParam int f_seq) throws Exception {
		BoardDao dao = sqlSession.getMapper(BoardDao.class);
		dao.deleteRowFreeBoard(f_seq);
		return "redirect:freeBoardList";
	}
	
	@RequestMapping(value = "/findListFreeBoard", method = RequestMethod.POST)
	public String findListFreeBoard(Locale locale, Model model, @RequestParam String find) throws Exception {
		BoardDao dao = sqlSession.getMapper(BoardDao.class);

		int pagesize = 10;
		int page = 1;
		int startrow = (page - 1) * pagesize;
		int endrow = 10;
		boardpaging.setFind(find);
		if (boardpaging.getFind() == null) {
			boardpaging.setFind("");
		}
		boardpaging.setStartrow(startrow);
		boardpaging.setEndrow(endrow);
		int rowcount = dao.selectCountFreeBoard(boardpaging);
		int absPage = 1;
		if (rowcount % pagesize == 0) {
			absPage = 0;
		}

		int pagecount = rowcount / pagesize + absPage;
		int pages[] = new int[pagecount];
		for (int i = 0; i < pagecount; i++) {
			pages[i] = i + 1;
		}

		ArrayList<Freeboard> boards = dao.findListFreeBoard(boardpaging);
		model.addAttribute("boards", boards);
		model.addAttribute("pages", pages);
		model.addAttribute("find", find);

		return "board/freeboard_list";
	}
	
	@RequestMapping(value = "/freeBoardPageSelect", method = RequestMethod.GET)
	public String freeBoardPageSelect(Locale locale, Model model, @RequestParam int page) throws Exception {
		BoardDao dao = sqlSession.getMapper(BoardDao.class);

		int pagesize = 10;
		int startrow = (page - 1) * pagesize;
		int endrow = 10;
		boardpaging.setFind(find);
		if (boardpaging.getFind() == null) {
			boardpaging.setFind("");
		}
		boardpaging.setStartrow(startrow);
		boardpaging.setEndrow(endrow);
		int rowcount = dao.selectCountFreeBoard(boardpaging);
		int absPage = 1;
		if (rowcount % pagesize == 0) {
			absPage = 0;
		}

		int pagecount = rowcount / pagesize + absPage;
		int pages[] = new int[pagecount];
		for (int i = 0; i < pagecount; i++) {
			pages[i] = i + 1;
		}

		ArrayList<Freeboard> boards = dao.findListFreeBoard(boardpaging);
		model.addAttribute("boards", boards);
		model.addAttribute("pages", pages);
		model.addAttribute("find", find);

		return "board/freeboard_list";
	}
	
	@RequestMapping(value = "/freeBoardLike", method = RequestMethod.POST)
	@ResponseBody
	public HashMap freeBoardLike(@RequestParam int f_seq, HttpSession session) throws Exception {
		HashMap<String, Object> hashmap = new HashMap<String, Object>();
		BoardDao dao = sqlSession.getMapper(BoardDao.class);
		freeboard = dao.selectOneFreeBoard(f_seq);
		String cursession = (String) session.getAttribute("sessionemail");
		String result = "";
		if(cursession == null) {
			result = "n";
		} else {
			result = "y";
		}
		freeboard.setF_sessionemail(cursession);
		freeboard.setF_seq(f_seq);
		int likecheck = dao.selectLikeCheck(freeboard);
		hashmap.put("likecheck", likecheck);
		if (likecheck == 0) {
			dao.freeBoardLikeInsert(freeboard);
		} else {
			dao.freeBoardLikeDelete(freeboard);
		}
		int selectlike = dao.selectLike(f_seq);
		hashmap.put("selectlike", selectlike);
		hashmap.put("result", result);
		return hashmap;
	}
	
	@RequestMapping(value = "/freeBoardUpdate", method = RequestMethod.GET)
	public String freeBoardUpdate(Model model,@RequestParam int f_seq) throws Exception {
		BoardDao dao = sqlSession.getMapper(BoardDao.class);
		freeboard = dao.selectOneFreeBoard(f_seq);
		model.addAttribute("freeboard", freeboard);
		return "board/freeboard_update";
	}
	
	@RequestMapping(value = "/freeBoardComment", method = RequestMethod.POST)
	public String freeBoardComment(Model model, @ModelAttribute Freeboardcomment freeboardcomment, HttpServletRequest request,@RequestParam int f_seq,HttpSession session) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm:ss");
		Date date = new Date();
		String today = df.format(date);
		freeboardcomment.setComment_date(today);
		BoardDao dao = sqlSession.getMapper(BoardDao.class);
		dao.insertRowFreeBoardComment(freeboardcomment);
		freeboard = dao.selectOneFreeBoard(f_seq);
		String cursession = (String) session.getAttribute("sessionemail");
		if (!cursession.equals(freeboard.getF_email())) {
			dao.addHitFreeBoard(f_seq);
		}
		int like = dao.selectLike(f_seq);
		freeboard.setF_sessionemail(cursession);
		int likecheck = dao.selectLikeCheck(freeboard);
		ArrayList<Freeboardcomment> freeboardcomments = dao.selectOneFreeBoardComment(f_seq);
		model.addAttribute("freeboard", freeboard);
		model.addAttribute("like",like);
		model.addAttribute("likecheck",likecheck);
		model.addAttribute("freeboardcomments",freeboardcomments);
		return "board/freeboard_detail";
	}

	@RequestMapping(value = "/freeBoardcommentDelete", method = RequestMethod.POST)
	@ResponseBody
	public String freeBoardcommentDelete(@RequestParam int comment_num, @RequestParam int f_seq) throws Exception {
		BoardDao dao = sqlSession.getMapper(BoardDao.class);
		int result = dao.deleteRowFreeBoardComment(comment_num);
		if (result > 0) {
			return "y";
		} else {
			return "n";
		}
	}
}
