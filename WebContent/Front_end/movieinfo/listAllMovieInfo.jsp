<%@page import="java.sql.Date"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*"%>
<%@ page import="com.movieinfo.model.*"%>
<%@ page import="com.moviegenre.model.*"%>
<%@ page import="com.sun.org.apache.xerces.internal.impl.dv.util.Base64"%>
<%-- 此頁練習採用 EL 的寫法取值 --%>

<%
    MovieInfoService movieinfoSvc = new MovieInfoService();
	MovieGenreService moviegenreSvc = new MovieGenreService();
	pageContext.setAttribute("msc",moviegenreSvc);
	
// 篩選是否為上映中電影	
    List<MovieInfoVO> lista = movieinfoSvc.getAll();
    List<MovieInfoVO> list = new ArrayList<MovieInfoVO>();
    java.util.Date now = new java.util.Date();
    java.sql.Date sqlDate = new java.sql.Date(now.getTime());
    for(MovieInfoVO movVO:lista){
    	if(sqlDate.after(movVO.getMovie_in()) && sqlDate.before(movVO.getMovie_out())){
    		list.add(movVO);
    	}
    }
    pageContext.setAttribute("list",list);
    
%>


<html>
<head>
		<!-- Bootstrap CSS start-->
		<link rel="stylesheet"
			href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.min.css">
		<!-- Bootstrap CSS end-->

<title>揪影MovieInfo </title>

<style>
  table#table-1 {
	background-color: #CCCCFF;
    border: 2px solid black;
    text-align: center;
  }
  table#table-1 h4 {
    color: red;
    display: block;
    margin-bottom: 1px;
  }
  h4 {
    color: blue;
    display: inline;
  }
  img, #level{
  	width:50px;
  	hight:50px;
  }
  
  img, #pic{
  	width:135px;
  	hight:200px;
  }
</style>

<style>
  table {
	width: 800px;
	background-color: white;
	margin-top: 5px;
	margin-bottom: 5px;
  }
  table, th, td {
    border: 1px solid #CCCCFF;
  }
  th, td {
    padding: 5px;
    text-align: center;
  }
</style>

</head>
<body bgcolor='white'>
<jsp:include page="/FrontHeaderFooter/Header.jsp" />
		<h1></h1><br>
		
<!-- 工作區開始 -->

		<div class="container">
		<div class="row justify-content">
			<div class="col-1"></div>
			<div class="col-4">

<table id="table-1">
	<tr><td>
		 <h3>熱映中電影</h3>
		 <h4><a href="<%=request.getContextPath()%>/Front_end/Home.jsp"><img src="<%=request.getContextPath()%>/Back_end/movieinfo/images/eatPopcorn.gif" width="125" height="72" border="0">回首頁</a></h4>
	</td></tr>
</table>

<%-- 錯誤表列 --%>
<c:if test="${not empty errorMsgs}">
	<font style="color:red">請修正以下錯誤:</font>
	<ul>
		<c:forEach var="message" items="${errorMsgs}">
			<li style="color:red">${message}</li>
		</c:forEach>
	</ul>
</c:if>

<table>
	<tr>
		<th>電影名稱</th>
		<th>電影封面</th>
		<th colspan="2">操作按鈕</th>
	</tr>
	<%@ include file="page1.file" %> 
		<c:forEach var="movieinfoVO" items="${list}" begin="<%=pageIndex%>" end="<%=pageIndex+rowsPerPage-1%>">		
		<tr>
			<td>${movieinfoVO.movie_name}</td>
<!-- 新增movie_pic -->
			<c:set var="movie_pic" value="${movieinfoVO.movie_pic}"></c:set>
			<%
				byte b[]= (byte[])pageContext.getAttribute("movie_pic");
				String encode = null;
				if(b != null){
					encode = Base64.encode(b);
			%>
			<td><img id="pic" src="data:image/jpg;base64,<%=encode%>"></td>
			<%}else{%><td></td><%}%>
			<td>
			  <FORM METHOD="post" ACTION="<%=request.getContextPath()%>/Front_end/movieinfo/movieinfo.do" style="margin-bottom: 0px;">
			     <input type="submit" value="MOVIE INFO">
			     <input type="hidden" name="movie_no"  value="${movieinfoVO.movie_no}">
			     <input type="hidden" name="action"	value="getOne_For_Display_Front"></FORM>
			</td>
<!-- 			<td> -->
<%-- 			  <FORM METHOD="post" ACTION="<%=request.getContextPath()%>/Front_end/movieinfo/movieinfo.do" style="margin-bottom: 0px;"> --%>
<!-- 			     Button trigger modal -->
<!-- 				 <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModalLong"> -->
<!-- 					  SHOWTIMES -->
<!-- 				 </button> -->
				 		<!-- Modal -->
<!-- 				<div class="modal fade" id="exampleModalLong" tabindex="-1" role="dialog" aria-labelledby="exampleModalLongTitle" aria-hidden="true"> -->
<!-- 				  <div class="modal-dialog" role="document"> -->
<!-- 				    <div class="modal-content"> -->
<!-- 				      <div class="modal-header"> -->
<!-- 				        <h5 class="modal-title" id="exampleModalLongTitle">Modal title</h5> -->
<!-- 				        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> -->
<!-- 				          <span aria-hidden="true">&times;</span> -->
<!-- 				        </button> -->
<!-- 				      </div> -->
<!-- 				      <div class="modal-body"> -->
<!-- 				        ... -->
<!-- 				      </div> -->
<!-- 				      <div class="modal-footer"> -->
<!-- 				        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button> -->
<!-- 				      </div> -->
<!-- 				    </div> -->
<!-- 				  </div> -->
<!-- 				</div> -->
<%-- 			     <input type="hidden" name="movie_no"  value="${movieinfoVO.movie_no}"> --%>
<!-- 			     <input type="hidden" name="action"	value="getOne_showtimes"></FORM> -->
<!-- 			</td> -->
			<td>
			  <FORM METHOD="post" ACTION="<%=request.getContextPath()%>/Front_end/movieinfo/movieinfo.do" style="margin-bottom: 0px;">
			     <input type="submit" value="CHAT ROOM">
			     <input type="hidden" name="movie_no"  value="${movieinfoVO.movie_no}">
			     <input type="hidden" name="action"	value="getOne_For_Display_Front_Chatroom"></FORM>
			</td>
		</tr>
	</c:forEach>
	
</table>
<%@ include file="page2.file" %>


			</div>
		</div>
	</div>

<!-- 工作區結束 -->
		
		<jsp:include page="/FrontHeaderFooter/Footer.jsp" />
		<!-- Optional JavaScript -->
		<!-- jQuery first, then Popper.js, then Bootstrap JS start-->
		<script src="<%=request.getContextPath()%>/bootstrap/jquery-3.3.1.min.js"></script>
		<script src="<%=request.getContextPath()%>/bootstrap/popper.min.js"></script>
		<script src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.min.js"></script>
		<!-- jQuery first, then Popper.js, then Bootstrap JS end-->
</body>
</html>