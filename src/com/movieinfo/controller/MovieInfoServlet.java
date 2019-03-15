package com.movieinfo.controller;

import java.io.*;
import java.sql.Date;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;

import com.movieinfo.model.*;

@MultipartConfig
public class MovieInfoServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		String action = req.getParameter("action");

		if ("getOne_For_Display".equals(action)) { // 來自select_page.jsp的請求

			List<String> errorMsgs = new LinkedList<String>();
			// Store this set in the request scope, in case we need to
			// send the ErrorPage view.
			req.setAttribute("errorMsgs", errorMsgs);

			try {
				/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 **********************/
				String str = req.getParameter("movie_no");
				if (str == null || (str.trim()).length() == 0) {
					errorMsgs.add("請輸入電影編號");
				}
				// Send the use back to the form, if there were errors
				if (!errorMsgs.isEmpty()) {
					RequestDispatcher failureView = req.getRequestDispatcher("/back-end/movieinfo/select_page.jsp");
					failureView.forward(req, res);
					return;// 程式中斷
				}

				String movie_no = null;
				try {
					movie_no = new String(str);
				} catch (Exception e) {
					errorMsgs.add("電影編號格式不正確");
				}
				// Send the use back to the form, if there were errors
				if (!errorMsgs.isEmpty()) {
					RequestDispatcher failureView = req.getRequestDispatcher("/back-end/movieinfo/select_page.jsp");
					failureView.forward(req, res);
					return;// 程式中斷
				}

				/*************************** 2.開始查詢資料 *****************************************/
				MovieInfoService movieinfoSvc = new MovieInfoService();
				MovieInfoVO movieinfoVO = movieinfoSvc.getOneMovieInfo(movie_no);
				if (movieinfoVO == null) {
					errorMsgs.add("查無資料");
				}
				// Send the use back to the form, if there were errors
				if (!errorMsgs.isEmpty()) {
					RequestDispatcher failureView = req.getRequestDispatcher("/back-end/movieinfo/select_page.jsp");
					failureView.forward(req, res);
					return;// 程式中斷
				}

				/*************************** 3.查詢完成,準備轉交(Send the Success view) *************/
				req.setAttribute("movieinfoVO", movieinfoVO); // 資料庫取出的empVO物件,存入req
				String url = "/back-end/movieinfo/listOneMovieInfo.jsp";
				RequestDispatcher successView = req.getRequestDispatcher(url); // 成功轉交 listOneEmp.jsp
				successView.forward(req, res);

				/*************************** 其他可能的錯誤處理 *************************************/
			} catch (Exception e) {
				errorMsgs.add("無法取得資料:" + e.getMessage());
				RequestDispatcher failureView = req.getRequestDispatcher("/back-end/movieinfo/select_page.jsp");
				failureView.forward(req, res);
			}
		}

		if ("getOne_For_Update".equals(action)) { // 來自listAllEmp.jsp的請求

			List<String> errorMsgs = new LinkedList<String>();
			// Store this set in the request scope, in case we need to
			// send the ErrorPage view.
			req.setAttribute("errorMsgs", errorMsgs);

			try {
				/*************************** 1.接收請求參數 ****************************************/
				String movie_no = new String(req.getParameter("movie_no"));

				/*************************** 2.開始查詢資料 ****************************************/
				MovieInfoService movieinfoSvc = new MovieInfoService();
				MovieInfoVO movieinfoVO = movieinfoSvc.getOneMovieInfo(movie_no);

				/*************************** 3.查詢完成,準備轉交(Send the Success view) ************/
				req.setAttribute("movieinfoVO", movieinfoVO); // 資料庫取出的empVO物件,存入req
				String url = "/back-end/movieinfo/update_MovieInfo_input.jsp";
				RequestDispatcher successView = req.getRequestDispatcher(url);// 成功轉交 update_emp_input.jsp
				successView.forward(req, res);

				/*************************** 其他可能的錯誤處理 **********************************/
			} catch (Exception e) {
				errorMsgs.add("無法取得要修改的資料:" + e.getMessage());
				RequestDispatcher failureView = req.getRequestDispatcher("/back-end/movieinfo/listAllMovieInfo.jsp");
				failureView.forward(req, res);
			}
		}

		if ("update".equals(action)) {
			List<String> errorMsgs = new LinkedList<String>();
			req.setAttribute("errorMsgs", errorMsgs);

			try {
				/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 **********************/
				String movie_no = new String(req.getParameter("movie_no").trim());

				
				Integer genre_no = null;
				try {
					genre_no = new Integer(req.getParameter("genre_no").trim());
				}catch(Exception e) {
					errorMsgs.add("電影種類請勿空白");
				}
				
				String movie_name = req.getParameter("movie_name");
				String movie_nameReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{1,100}$";
				if (movie_name == null || movie_name.trim().length() == 0) {
					errorMsgs.add("電影名稱: 請勿空白");
				} 
				
				byte[] movie_level = null;
				try {
					Part part = req.getPart("movie_level");
					if (part.getSubmittedFileName() != "") {
						BufferedInputStream bif = new BufferedInputStream(part.getInputStream());
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						int len;
						byte[] b = new byte[8192];
						while ((len = bif.read(b)) != -1) {
							bao.write(b);
						}
						movie_level = bao.toByteArray();
					}
				} catch (Exception e) {
					errorMsgs.add("上傳照片失敗，請重新上傳");
				}

				String movie_director = req.getParameter("movie_director").trim();
				if (movie_director == null || movie_director.trim().length() == 0) {
					errorMsgs.add("導演請勿空白");
				}
				
				String movie_intro = req.getParameter("movie_intro").trim();
				if (movie_intro == null || movie_intro.trim().length() == 0) {
					errorMsgs.add("簡介請勿空白");
				}
				
				byte[] movie_trailer = null;
				try {
					Part part = req.getPart("movie_trailer");
					if (part.getSubmittedFileName() != ""||part.getSize() != 0) {
						BufferedInputStream bif = new BufferedInputStream(part.getInputStream());
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						int len;
						byte[] b = new byte[8192];
						while ((len = bif.read(b)) != -1) {
							bao.write(b);
						}
						movie_trailer = bao.toByteArray();
					}
				} catch (Exception e) {
					errorMsgs.add("上傳影片失敗，請重新上傳");
				}
				
				byte[] movie_pic = null;
				try {
					Part part = req.getPart("movie_pic");
					if (part.getSubmittedFileName() != "") {
						BufferedInputStream bif = new BufferedInputStream(part.getInputStream());
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						int len;
						byte[] b = new byte[8192];
						while ((len = bif.read(b)) != -1) {
							bao.write(b);
						}
						movie_pic = bao.toByteArray();
					}
				} catch (Exception e) {
					errorMsgs.add("上傳封面失敗，請重新上傳");
				}
				
				java.sql.Date movie_in = null;
				try {
					movie_in = java.sql.Date.valueOf(req.getParameter("movie_in").trim());
				} catch (IllegalArgumentException e) {
					movie_in = new java.sql.Date(System.currentTimeMillis());
					errorMsgs.add("請輸入上映日期!");
				}
				
				java.sql.Date movie_out = null;
				try {
					movie_out = java.sql.Date.valueOf(req.getParameter("movie_out").trim());
				} catch (IllegalArgumentException e) {
					movie_out = new java.sql.Date(System.currentTimeMillis());
					errorMsgs.add("請輸入下映日期!");
				}
				
				String movie_length = req.getParameter("movie_length").trim();
				if (movie_length == null || movie_length.trim().length() == 0) {
					errorMsgs.add("片長請勿空白");
				}
				
				String movie_cast = req.getParameter("movie_cast").trim();
				if (movie_cast == null || movie_cast.trim().length() == 0) {
					errorMsgs.add("演員請勿空白");
				}
				
				Integer movie_count = null;
				try {
					movie_count = new Integer(req.getParameter("movie_count").trim());
				} catch (Exception e) {
					errorMsgs.add("票房請勿空白");
				}
				
				Integer movie_exp = null;
				try {
					movie_exp = new Integer(req.getParameter("movie_exp").trim());
				} catch (Exception e) {
					errorMsgs.add("期待度請勿空白");
				}
				
				Integer movie_noexp = null;
				try {
					movie_noexp = new Integer(req.getParameter("movie_noexp").trim());
				} catch (Exception e) {
					errorMsgs.add("不期待度請勿空白");
				}
				
				Integer movie_touch = null;
				try {
					movie_touch = new Integer(req.getParameter("movie_touch").trim());
				} catch (Exception e) {
					errorMsgs.add("點擊次數請勿空白");
				}
				
				Integer movie_ticket = null;
				try {
					movie_ticket = new Integer(req.getParameter("movie_ticket").trim());
				} catch (Exception e) {
					errorMsgs.add("片長加價請勿空白");
				}
				

				MovieInfoVO movieinfoVO = new MovieInfoVO();
				
				movieinfoVO.setMovie_no(movie_no);
				movieinfoVO.setGenre_no(genre_no);
				movieinfoVO.setMovie_name(movie_name);
				movieinfoVO.setMovie_level(movie_level);
				movieinfoVO.setMovie_director(movie_director);
				movieinfoVO.setMovie_cast(movie_cast);
				movieinfoVO.setMovie_intro(movie_intro);
				movieinfoVO.setMovie_length(movie_length);
				movieinfoVO.setMovie_trailer(movie_trailer);
				movieinfoVO.setMovie_pic(movie_pic);
				movieinfoVO.setMovie_in(movie_in);
				movieinfoVO.setMovie_out(movie_out);
				movieinfoVO.setMovie_count(movie_count);
				movieinfoVO.setMovie_exp(movie_exp);
				movieinfoVO.setMovie_noexp(movie_noexp);
				movieinfoVO.setMovie_touch(movie_touch);
				movieinfoVO.setMovie_ticket(movie_ticket);
				

				if (!errorMsgs.isEmpty()) {
					req.setAttribute("movieinfoVO", movieinfoVO); // 含有輸入格式錯誤的empVO物件,也存入req
					RequestDispatcher failureView = req
							.getRequestDispatcher("/back-end/movieinfo/update_MovieInfo_input.jsp");
					failureView.forward(req, res);
					return; // 程式中斷
				}

				/*************************** 2.開始修改資料 *****************************************/
				System.out.println("hello world");
				MovieInfoService movieinfoSvc = new MovieInfoService();
				movieinfoVO = movieinfoSvc.updateMovieInfo(movie_no, genre_no, movie_name, movie_level, movie_director,
						movie_cast, movie_intro, movie_length, movie_trailer, movie_pic, movie_in, movie_out, movie_count, 
						movie_exp, movie_noexp, movie_touch, movie_ticket);
				/*************************** 3.修改完成,準備轉交(Send the Success view) *************/
				req.setAttribute("movieinfoVO", movieinfoVO); // 資料庫update成功後,正確的的empVO物件,存入req
				String url = "/back-end/movieinfo/listOneMovieInfo.jsp";
				RequestDispatcher successView = req.getRequestDispatcher(url); // 修改成功後,轉交listOneEmp.jsp
				successView.forward(req, res);

				/*************************** 其他可能的錯誤處理 *************************************/
			} catch (Exception e) {
				errorMsgs.add("修改資料失敗:" + e.getMessage());
				RequestDispatcher failureView = req
						.getRequestDispatcher("/back-end/movieinfo/update_MovieInfo_input.jsp");
				failureView.forward(req, res);
			}
		}

		if ("insert".equals(action)) { // 來自addEmp.jsp的請求

			List<String> errorMsgs = new LinkedList<String>();
			// Store this set in the request scope, in case we need to
			// send the ErrorPage view.
			req.setAttribute("errorMsgs", errorMsgs);

			try {
				/*********************** 1.接收請求參數 - 輸入格式的錯誤處理 *************************/

				Integer genre_no = null;
				try {
					genre_no = new Integer(req.getParameter("genre_no").trim());
				}catch(Exception e) {
					errorMsgs.add("電影種類請勿空白");
				}
				
				String movie_name = req.getParameter("movie_name");
				String movie_nameReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{1,100}$";
				if (movie_name == null || movie_name.trim().length() == 0) {
					errorMsgs.add("電影名稱: 請勿空白");
				} else if (!movie_name.trim().matches(movie_nameReg)) { // 以下練習正則(規)表示式(regular-expression)
					errorMsgs.add("電影名稱: 只能是中、英文字母、數字和_ , 且長度必需在1到10之間");
				}
				
				byte[] movie_level = null;
				try {
					Part part = req.getPart("movie_level");
					if (part.getSubmittedFileName() != "") {
						BufferedInputStream bif = new BufferedInputStream(part.getInputStream());
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						int len;
						byte[] b = new byte[8192];
						while ((len = bif.read(b)) != -1) {
							bao.write(b);
						}
						movie_level = bao.toByteArray();
					}
				} catch (Exception e) {
					errorMsgs.add("上傳照片失敗，請重新上傳");
				}

				String movie_director = req.getParameter("movie_director").trim();
				if (movie_director == null || movie_director.trim().length() == 0) {
					errorMsgs.add("導演請勿空白");
				}
				
				String movie_intro = req.getParameter("movie_intro").trim();
				if (movie_intro == null || movie_intro.trim().length() == 0) {
					errorMsgs.add("簡介請勿空白");
				}
				
				byte[] movie_trailer = null;
				try {
					Part part = req.getPart("movie_trailer");
					if (part.getSubmittedFileName() != "") {
						BufferedInputStream bif = new BufferedInputStream(part.getInputStream());
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						int len;
						byte[] b = new byte[8192];
						while ((len = bif.read(b)) != -1) {
							bao.write(b);
						}
						movie_trailer = bao.toByteArray();
					}
				} catch (Exception e) {
					errorMsgs.add("上傳影片失敗，請重新上傳");
				}
				
				byte[] movie_pic = null;
				try {
					Part part = req.getPart("movie_pic");
					if (part.getSubmittedFileName() != "") {
						BufferedInputStream bif = new BufferedInputStream(part.getInputStream());
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						int len;
						byte[] b = new byte[8192];
						while ((len = bif.read(b)) != -1) {
							bao.write(b);
						}
						movie_pic = bao.toByteArray();
					}
				} catch (Exception e) {
					errorMsgs.add("上傳封面失敗，請重新上傳");
				}
				
				java.sql.Date movie_in = null;
				try {
					movie_in = java.sql.Date.valueOf(req.getParameter("movie_in").trim());
				} catch (IllegalArgumentException e) {
					movie_in = new java.sql.Date(System.currentTimeMillis());
					errorMsgs.add("請輸入上映日期!");
				}
				
				java.sql.Date movie_out = null;
				try {
					movie_out = java.sql.Date.valueOf(req.getParameter("movie_out").trim());
				} catch (IllegalArgumentException e) {
					movie_out = new java.sql.Date(System.currentTimeMillis());
					errorMsgs.add("請輸入下映日期!");
				}
				
				String movie_length = req.getParameter("movie_length").trim();
				if (movie_length == null || movie_length.trim().length() == 0) {
					errorMsgs.add("片長請勿空白");
				}
				
				String movie_cast = req.getParameter("movie_cast").trim();
				if (movie_cast == null || movie_cast.trim().length() == 0) {
					errorMsgs.add("演員請勿空白");
				}
				
				Integer movie_count = null;
				try {
					movie_count = new Integer(req.getParameter("movie_count").trim());
				} catch (Exception e) {
					errorMsgs.add("票房請勿空白");
				}
				
				Integer movie_exp = null;
				try {
					movie_exp = new Integer(req.getParameter("movie_exp").trim());
				} catch (Exception e) {
					errorMsgs.add("期待度請勿空白");
				}
				
				Integer movie_noexp = null;
				try {
					movie_noexp = new Integer(req.getParameter("movie_noexp").trim());
				} catch (Exception e) {
					errorMsgs.add("不期待度請勿空白");
				}
				
				Integer movie_touch = null;
				try {
					movie_touch = new Integer(req.getParameter("movie_touch").trim());
				} catch (Exception e) {
					errorMsgs.add("點擊次數請勿空白");
				}
				
				Integer movie_ticket = null;
				try {
					movie_ticket = new Integer(req.getParameter("movie_ticket").trim());
				} catch (Exception e) {
					errorMsgs.add("片長加價請勿空白");
				}

				MovieInfoVO movieinfoVO = new MovieInfoVO();
				
				movieinfoVO.setGenre_no(genre_no);
				movieinfoVO.setMovie_name(movie_name);
				movieinfoVO.setMovie_level(movie_level);
				movieinfoVO.setMovie_director(movie_director);
				movieinfoVO.setMovie_cast(movie_cast);
				movieinfoVO.setMovie_intro(movie_intro);
				movieinfoVO.setMovie_length(movie_length);
				movieinfoVO.setMovie_trailer(movie_trailer);
				movieinfoVO.setMovie_pic(movie_pic);
				movieinfoVO.setMovie_in(movie_in);
				movieinfoVO.setMovie_out(movie_out);
				movieinfoVO.setMovie_count(movie_count);
				movieinfoVO.setMovie_exp(movie_exp);
				movieinfoVO.setMovie_noexp(movie_noexp);
				movieinfoVO.setMovie_touch(movie_touch);
				movieinfoVO.setMovie_ticket(movie_ticket);

				// Send the use back to the form, if there were errors
				if (!errorMsgs.isEmpty()) {
					req.setAttribute("movieinfoVO", movieinfoVO); // 含有輸入格式錯誤的empVO物件,也存入req
					RequestDispatcher failureView = req.getRequestDispatcher("/back-end/movieinfo/addMovieInfo.jsp");
					failureView.forward(req, res);
					return; // 程式中斷
				}

				/*************************** 2.開始新增資料 ***************************************/
				MovieInfoService movieinfoSvc = new MovieInfoService();
				movieinfoVO = movieinfoSvc.addMovieInfo(genre_no, movie_name, movie_level, movie_director,
						movie_cast, movie_intro, movie_length, movie_trailer, movie_pic, movie_in, movie_out, movie_count, movie_exp, movie_noexp, movie_touch, movie_ticket);

				/*************************** 3.新增完成,準備轉交(Send the Success view) ***********/
				String url = "/back-end/movieinfo/listAllMovieInfo.jsp";
				RequestDispatcher successView = req.getRequestDispatcher(url); // 新增成功後轉交listAllEmp.jsp
				successView.forward(req, res);

				/*************************** 其他可能的錯誤處理 **********************************/
			} catch (Exception e) {
				errorMsgs.add(e.getMessage());
				RequestDispatcher failureView = req.getRequestDispatcher("/back-end/movieinfo/addMovieInfo.jsp");
				failureView.forward(req, res);
			}
		}

		if ("delete".equals(action)) { // 來自listAllEmp.jsp

			List<String> errorMsgs = new LinkedList<String>();
			// Store this set in the request scope, in case we need to
			// send the ErrorPage view.
			req.setAttribute("errorMsgs", errorMsgs);

			try {
				/*************************** 1.接收請求參數 ***************************************/
				String movie_no = new String(req.getParameter("movie_no"));

				/*************************** 2.開始刪除資料 ***************************************/
				MovieInfoService movieinfoSvc = new MovieInfoService();
				movieinfoSvc.deleteMovieInfo(movie_no);

				/*************************** 3.刪除完成,準備轉交(Send the Success view) ***********/
				String url = "/back-end/movieinfo/listAllMovieInfo.jsp";
				RequestDispatcher successView = req.getRequestDispatcher(url);// 刪除成功後,轉交回送出刪除的來源網頁
				successView.forward(req, res);

				/*************************** 其他可能的錯誤處理 **********************************/
			} catch (Exception e) {
				errorMsgs.add("刪除資料失敗:" + e.getMessage());
				RequestDispatcher failureView = req.getRequestDispatcher("/back-end/movieinfo/listAllMovieInfo.jsp");
				failureView.forward(req, res);
			}
		}
	}
}
