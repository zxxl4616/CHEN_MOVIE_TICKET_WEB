package com.movieinfo.controller;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;

import com.newsinfo.model.*;

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
				String str = req.getParameter("news_no");
				if (str == null || (str.trim()).length() == 0) {
					errorMsgs.add("請輸入新聞編號");
				}
				// Send the use back to the form, if there were errors
				if (!errorMsgs.isEmpty()) {
					RequestDispatcher failureView = req.getRequestDispatcher("/back-end/newsinfo/select_page.jsp");
					failureView.forward(req, res);
					return;// 程式中斷
				}

				Integer news_no = null;
				try {
					news_no = new Integer(str);
				} catch (Exception e) {
					errorMsgs.add("新聞編號格式不正確");
				}
				// Send the use back to the form, if there were errors
				if (!errorMsgs.isEmpty()) {
					RequestDispatcher failureView = req.getRequestDispatcher("/back-end/newsinfo/select_page.jsp");
					failureView.forward(req, res);
					return;// 程式中斷
				}

				/*************************** 2.開始查詢資料 *****************************************/
				NewsInfoService newsinfoSvc = new NewsInfoService();
				NewsInfoVO newsinfoVO = newsinfoSvc.getOneNewsInfo(news_no);
				if (newsinfoVO == null) {
					errorMsgs.add("查無資料");
				}
				// Send the use back to the form, if there were errors
				if (!errorMsgs.isEmpty()) {
					RequestDispatcher failureView = req.getRequestDispatcher("/back-end/newsinfo/select_page.jsp");
					failureView.forward(req, res);
					return;// 程式中斷
				}

				/*************************** 3.查詢完成,準備轉交(Send the Success view) *************/
				req.setAttribute("newsinfoVO", newsinfoVO); // 資料庫取出的empVO物件,存入req
				String url = "/back-end/newsinfo/listOneNewsInfo.jsp";
				RequestDispatcher successView = req.getRequestDispatcher(url); // 成功轉交 listOneEmp.jsp
				successView.forward(req, res);

				/*************************** 其他可能的錯誤處理 *************************************/
			} catch (Exception e) {
				errorMsgs.add("無法取得資料:" + e.getMessage());
				RequestDispatcher failureView = req.getRequestDispatcher("/back-end/newsinfo/select_page.jsp");
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
				Integer news_no = new Integer(req.getParameter("news_no"));

				/*************************** 2.開始查詢資料 ****************************************/
				NewsInfoService newsinfoSvc = new NewsInfoService();
				NewsInfoVO newsinfoVO = newsinfoSvc.getOneNewsInfo(news_no);

				/*************************** 3.查詢完成,準備轉交(Send the Success view) ************/
				req.setAttribute("newsinfoVO", newsinfoVO); // 資料庫取出的empVO物件,存入req
				String url = "/back-end/newsinfo/update_NewsInfo_input.jsp";
				RequestDispatcher successView = req.getRequestDispatcher(url);// 成功轉交 update_emp_input.jsp
				successView.forward(req, res);

				/*************************** 其他可能的錯誤處理 **********************************/
			} catch (Exception e) {
				errorMsgs.add("無法取得要修改的資料:" + e.getMessage());
				RequestDispatcher failureView = req.getRequestDispatcher("/back-end/newsinfo/listAllNewsInfo.jsp");
				failureView.forward(req, res);
			}
		}

		if ("update".equals(action)) {
			List<String> errorMsgs = new LinkedList<String>();
			req.setAttribute("errorMsgs", errorMsgs);

			try {
				/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 **********************/
				Integer news_no = new Integer(req.getParameter("news_no").trim());

				String movie_no = req.getParameter("movie_no");
				String movie_noReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{1,10}$";
				if (movie_no == null || movie_no.trim().length() == 0) {
					errorMsgs.add("電影編號: 請勿空白");
				} else if (!movie_no.trim().matches(movie_noReg)) { // 以下練習正則(規)表示式(regular-expression)
					errorMsgs.add("電影編號: 只能是中、英文字母、數字和_ , 且長度必需在2到10之間");
				}

				String news_title = req.getParameter("news_title").trim();
				if (news_title == null || news_title.trim().length() == 0) {
					errorMsgs.add("標題請勿空白");
				}

				String news_auther = req.getParameter("news_auther").trim();
				if (news_auther == null || news_auther.trim().length() == 0) {
					errorMsgs.add("作者請勿空白");
				}

				java.sql.Date news_times = null;
				try {
					news_times = java.sql.Date.valueOf(req.getParameter("news_times").trim());
				} catch (IllegalArgumentException e) {
					news_times = new java.sql.Date(System.currentTimeMillis());
					errorMsgs.add("請輸入日期!");
				}

				String news_con = req.getParameter("news_con").trim();
				if (news_con == null || news_con.trim().length() == 0) {
					errorMsgs.add("內容請勿空白");
				}

//寫入圖片至資料庫
				byte[] news_pic = null;
				try {
					Part part = req.getPart("news_pic");
					if (part.getSubmittedFileName() != "") {
						BufferedInputStream bif = new BufferedInputStream(part.getInputStream());
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						int len;
						byte[] b = new byte[8192];
						while ((len = bif.read(b)) != -1) {
							bao.write(b);
						}
						news_pic = bao.toByteArray();
					}
				} catch (Exception e) {
					errorMsgs.add("上傳照片失敗，請重新上傳");
				}

				NewsInfoVO newsinfoVO = new NewsInfoVO();
				newsinfoVO.setNews_no(news_no);
				newsinfoVO.setMovie_no(movie_no);
				newsinfoVO.setNews_title(news_title);
				newsinfoVO.setNews_auther(news_auther);
				newsinfoVO.setNews_times(news_times);
				newsinfoVO.setNews_con(news_con);
				newsinfoVO.setNews_pic(news_pic);

				if (!errorMsgs.isEmpty()) {
					req.setAttribute("newsinfoVO", newsinfoVO); // 含有輸入格式錯誤的empVO物件,也存入req
					RequestDispatcher failureView = req
							.getRequestDispatcher("/back-end/newsinfo/update_NewsInfo_input.jsp");
					failureView.forward(req, res);
					return; // 程式中斷
				}

				/*************************** 2.開始修改資料 *****************************************/
				System.out.println("hello world");
				NewsInfoService newsinfoSvc = new NewsInfoService();
				newsinfoVO = newsinfoSvc.updateNewsInfo(news_no, movie_no, news_title, news_auther, news_times,
						news_con, news_pic);
				/*************************** 3.修改完成,準備轉交(Send the Success view) *************/
				req.setAttribute("newsinfoVO", newsinfoVO); // 資料庫update成功後,正確的的empVO物件,存入req
				String url = "/back-end/newsinfo/listOneNewsInfo.jsp";
				RequestDispatcher successView = req.getRequestDispatcher(url); // 修改成功後,轉交listOneEmp.jsp
				successView.forward(req, res);

				/*************************** 其他可能的錯誤處理 *************************************/
			} catch (Exception e) {
				errorMsgs.add("修改資料失敗:" + e.getMessage());
				RequestDispatcher failureView = req
						.getRequestDispatcher("/back-end/newsinfo/update_NewsInfo_input.jsp");
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

				String movie_no = req.getParameter("movie_no");
				String movie_noReg = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{1,10}$";
				if (movie_no == null || movie_no.trim().length() == 0) {
					errorMsgs.add("電影編號: 請勿空白");
				} else if (!movie_no.trim().matches(movie_noReg)) { // 以下練習正則(規)表示式(regular-expression)
					errorMsgs.add("電影編號: 只能是中、英文字母、數字和_ , 且長度必需在2到10之間");
				}

				String news_title = req.getParameter("news_title").trim();
				if (news_title == null || news_title.trim().length() == 0) {
					errorMsgs.add("標題請勿空白");
				}

				String news_auther = req.getParameter("news_auther").trim();
				if (news_auther == null || news_auther.trim().length() == 0) {
					errorMsgs.add("作者請勿空白");
				}

				java.sql.Date news_times = null;
				try {
					news_times = java.sql.Date.valueOf(req.getParameter("news_times").trim());
				} catch (IllegalArgumentException e) {
					news_times = new java.sql.Date(System.currentTimeMillis());
					errorMsgs.add("請輸入日期!");
				}

				String news_con = req.getParameter("news_con").trim();
				if (news_con == null || news_con.trim().length() == 0) {
					errorMsgs.add("內容請勿空白");
				}

//寫入圖片至資料庫
				byte[] news_pic = null;
				try {
					Part part = req.getPart("news_pic");
					if (part.getSubmittedFileName() != "") {
						BufferedInputStream bif = new BufferedInputStream(part.getInputStream());
						ByteArrayOutputStream bao = new ByteArrayOutputStream();
						int len;
						byte[] b = new byte[8192];
						while ((len = bif.read(b)) != -1) {
							bao.write(b);
						}
						news_pic = bao.toByteArray();
					}
				} catch (Exception e) {
					errorMsgs.add("上傳照片失敗，請重新上傳");
				}

				NewsInfoVO newsinfoVO = new NewsInfoVO();
				newsinfoVO.setMovie_no(movie_no);
				newsinfoVO.setNews_title(news_title);
				newsinfoVO.setNews_auther(news_auther);
				newsinfoVO.setNews_times(news_times);
				newsinfoVO.setNews_con(news_con);
				newsinfoVO.setNews_pic(news_pic);

				// Send the use back to the form, if there were errors
				if (!errorMsgs.isEmpty()) {
					req.setAttribute("newsinfoVO", newsinfoVO); // 含有輸入格式錯誤的empVO物件,也存入req
					RequestDispatcher failureView = req.getRequestDispatcher("/back-end/newsinfo/addNewsInfo.jsp");
					failureView.forward(req, res);
					return; // 程式中斷
				}

				/*************************** 2.開始新增資料 ***************************************/
				NewsInfoService newsinfoSvc = new NewsInfoService();
				newsinfoVO = newsinfoSvc.addNewsInfo(movie_no, news_title, news_auther, news_times, news_con, news_pic);

				/*************************** 3.新增完成,準備轉交(Send the Success view) ***********/
				String url = "/back-end/newsinfo/listAllNewsInfo.jsp";
				RequestDispatcher successView = req.getRequestDispatcher(url); // 新增成功後轉交listAllEmp.jsp
				successView.forward(req, res);

				/*************************** 其他可能的錯誤處理 **********************************/
			} catch (Exception e) {
				errorMsgs.add(e.getMessage());
				RequestDispatcher failureView = req.getRequestDispatcher("/back-end/newsinfo/addNewsInfo.jsp");
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
				Integer news_no = new Integer(req.getParameter("news_no"));

				/*************************** 2.開始刪除資料 ***************************************/
				NewsInfoService newsinfoSvc = new NewsInfoService();
				newsinfoSvc.deleteNewsInfo(news_no);

				/*************************** 3.刪除完成,準備轉交(Send the Success view) ***********/
				String url = "/back-end/newsinfo/listAllNewsInfo.jsp";
				RequestDispatcher successView = req.getRequestDispatcher(url);// 刪除成功後,轉交回送出刪除的來源網頁
				successView.forward(req, res);

				/*************************** 其他可能的錯誤處理 **********************************/
			} catch (Exception e) {
				errorMsgs.add("刪除資料失敗:" + e.getMessage());
				RequestDispatcher failureView = req.getRequestDispatcher("/back-end/newsinfo/listAllNewsInfo.jsp");
				failureView.forward(req, res);
			}
		}
	}
}