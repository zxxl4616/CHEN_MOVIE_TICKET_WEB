<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.mem.model.* , java.util.*"%>

<jsp:useBean id="memVO" class="com.mem.model.MemVO" scope="session" />

<!doctype html>
<html lang="en">
	<head>
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.min.css">
	<title></title>
	
	<style>
	table {
		table-layout: fixed;
	}
	
	<
	th style ="width: 63px" > </th>td {
		white-space: nowrap;
		overflow: hidden;
		word-break: break-all;
	} /*防止換行*/
	
		.card-img{
            position: fixed;
            top: 0;
            left: 0;
            bottom: 0;
            right: 0;
            z-index: -999;
            min-height: 100%;
            width: 100%;
            opacity: 0.3;
			}
	
	
	</style>
	
	
	</head>
	<body>
	<div>
		<img src="<%=request.getContextPath()%>/FrontHeaderFooter/FrontBootstrapHeaderFooter/Front_end_img.jpg" class="card-img" alt="" style="">
	</div>
	<%
		java.sql.Date member_birthday = null;
		try{
			member_birthday = memVO.getMember_birthday();
		}catch (Exception e){
			member_birthday = new java.sql.Date(System.currentTimeMillis());
		}
	%>
	<% String[] arrayCity  = new String[] {"台北市","基隆市","新北市","桃園市","新竹市","新竹縣","苗栗縣","台中市","彰化縣","南投縣","雲林縣","嘉義縣","台南市","高雄市","屏東縣","宜蘭縣","花蓮縣","台東縣","澎湖縣","金門縣","連江縣"};
	 	List<String> listCity = Arrays.asList(arrayCity);  
	 	pageContext.setAttribute("listCity", listCity);
	 %>
	
		<h1></h1>
	
		<!-- 工作區開始 -->
	
		<%--錯誤表列 --%>
		<c:if test="${not empty errorMsgs }">
			<font style="color: red">請修正以下錯誤</font>
			<ul>
				<c:forEach var="message" items="${errorMsgs}">
					<li style="color: red">${message}</li>
				</c:forEach>
			</ul>
		</c:if>
	
		<FORM METHOD="post" ACTION="mem.do" name="form1"
			enctype="multipart/form-data">
			<table class="table table-sm table-hover">
<!-- 				<tr> -->
<!-- 					<td>會員編號:</td> -->
<%-- 					<td><%=memVO.getMember_no() %></td> --%>
<!-- 				</tr> -->
	
<!-- 				<tr> -->
<!-- 					<td>會員帳號:</td> -->
<%-- 					<td><%=memVO.getMember_account() %></td> --%>
<!-- 				</tr> -->
	
	
	
				<tr>
					<td>*密碼:</td>
					<td><input type="password" name="member_password2" name="member_password2" id="member_password2" value="<%= (memVO==null)?"":""%>" required></td>
					<td>*新密碼:</td>
					<td><input type="password" name="member_password" id="member_password" value="<%= (memVO==null)?"":""%>" required></td>
				</tr>
	
				<tr>
					<td>*再次確認新密碼:</td>
					<td><input type="password" name="member_password1" id="member_password1" value="<%= (memVO==null)?"":""%>" required></td>
				</tr>
	
				<tr>
					<td>*姓名:</td>
					<td><input type="text" name="member_name" id="member_name" value="<%= (memVO==null)?"":memVO.getMember_name()%>"></td>
					<td>暱稱:</td>
					<td><input type="text" name="member_nick" id="member_nick" value="<%= (memVO==null)?"":memVO.getMember_nick()%>"></td>
				</tr>
	
				<tr>
					<td>*性別:</td>
					<td><input type="radio" name="member_sex" value="1" ${(memVO.member_sex==1)? "checked": ""}>男性 
					<input type="radio" name="member_sex" value="0" ${(memVO.member_sex==0)? "checked": ""}>女性
					</td>
					<td>*生日:</td>
					<td><input type="date" name="member_birthday" id="f_date" value="<%= (memVO==null)? member_birthday:memVO.getMember_birthday()%>"></td>
				</tr>
	
	
				<tr>
					<td>*地址:</td>
					<td>
						<select id="twCityName">
							<option>--請選擇縣市--</option>
							<c:forEach var="city" items="${listCity}">
								<option value="${city}">${city}</option>
							</c:forEach>
						</select>
					</td>
					<td>
						<select id="CityAreaName">
							<option>--請選擇區域--</option>
						</select>
					</td>
					<td>
						<select id="AreaRoadName">
							<option>--請選擇路名--</option>
						</select> 
					</td>
				</tr>
				<tr>
					<td></td>
					<td>
						<input type="text" placeholder="請輸入門牌號碼" id="num" style="">
						<input type="button" value="確認" id="btnLoc">
					</td>
					<td>
						<input id="addressTotal" type="text" name="member_address" size="50" style="width:410px">
					</td>
				</tr>
				
				<tr>
					<td>*電話:</td>
					<td><input type="tel" name="member_telephone" id="member_telephone" value="<%= (memVO==null)?"":memVO.getMember_telephone()%>"></td>
				</tr>
	
				<tr>
					<td>*信箱:</td>
					<td><input type="email" name="member_email" id="member_email" value="<%= (memVO==null)?"":memVO.getMember_email()%>" style="width:640px"></td>
				</tr>
	
				<tr>
					<td>會員圖像:</td>
					<td><img id="preview_progressbarTW_img" src="#" width="100px"
						height="100px" style="display: none" /> <input type="file"
						id="progressbarTWInput" name="member_picture" size="25"
						accept="image/gif, image/jpeg, image/png"
						value="${memVO.member_picture}" /></td>
				</tr>
	
				<tr>
					<td>信用卡號碼:</td>
					<td><input type="text" name="member_credit_number"
						id="member_credit_number" value="${memVO.member_credit_number}"></td>
				</tr>
	
				<tr>
					<td>背面後三碼:</td>
					<td><input type="text" name="member_back_verification"
						id="member_back_verification"
						value="${memVO.member_back_verification}"></td>
				</tr>
	
			</table>
			<br> 
			<input type="hidden" name="action" value="update"> 
			<input type="submit" value="送出修改"> 
			<img src="img/popcorn.jpg" height="20" width="20" onclick="idwrite(this)">
			<img src="img/popcorn.jpg" height="20" width="20"
						onclick="idwrite2(this)">
			<script>
				function idwrite(name){
					form1.member_password.value="1314520";
					form1.member_password1.value="1314520";
					form1.member_name.value="DAVID";
					form1.member_nick.value="seafood";
					form1.member_sex.value="1";
					form1.f_date.value="2018-03-29";
					form1.member_address.value="桃園市中壢區中大路1號";
					form1.member_telephone.value="0982102271";
					form1.member_email.value="zxxl3617@gmail.com";
					form1.member_credit_number.value="498231678597";
					form1.member_back_verification.value="798";
				}
				
				function idwrite2(name){
					
					
					form1.member_password.value="123456";
					form1.member_password1.value="123456";
					form1.member_name.value="吳神";
					form1.member_nick.value="大吳";
					form1.member_sex.value="1";
					form1.f_date.value="2018-03-29";
					form1.member_address.value="桃園市中壢區中大路1號";
					form1.member_telephone.value="0982102271";
					form1.member_email.value="jack.36202620@gmail.com";
					form1.member_credit_number.value="498231678597";
					form1.member_back_verification.value="798";
					
				}
			</script>
		</FORM>
	
		<!-- 工作區結束 -->
		<script src="<%=request.getContextPath()%>/bootstrap/jquery-3.3.1.min.js"></script>
		<script>
			$("#progressbarTWInput").ready(function(){
			  readURL(this);
			});
	
			$("#progressbarTWInput").change(function(){
			  readURL(this);
			});
			function readURL(input){
			  if(input.files && input.files[0]){
			    var reader = new FileReader();
			    reader.onload = function (e) {
			       $("#preview_progressbarTW_img").attr('src', e.target.result);
			       $("#preview_progressbarTW_img").removeAttr("style");
			    }
			    reader.readAsDataURL(input.files[0]);
			  }
			}
		</script>
	
		<script> 
	
	$(document).ready(function(){
		
		$("#twCityName").change(function(){
			$.ajax({
				 type: "POST",
				 url: "<%=request.getContextPath()%>/Json2Read",
				 data: {"action":"twCityName",
					 	"twCityName":$('#twCityName option:selected').val()},
				 dataType: "json",
				 success: function(result){
					 $("#CityAreaName").empty();
					
					 $("#CityAreaName").append("<option >--請選擇區域--</option>")
					 for(var i=0; i<result.length; i++){
					 	$("#CityAreaName").append('<option value="'+result[i]+'">'+result[i]+'</option>');
					 }
				 },
		         error: function(){
		        	 alert("AJAX-grade發生錯誤囉!")
		        	 }
		    });
		});
		
		$("#CityAreaName").change(function(){
			$.ajax({
				 type: "POST",
				 url: "<%=request.getContextPath()%>/Json2Read",
				 data : {
					 "action" : "CityAreaName",
					 "twCityName" : $('#twCityName option:selected')
				 .val(),
				 "CityAreaName" : $('#CityAreaName option:selected').val()
				 	},
					 dataType : "json",
					 success : function(result) {
						 $("#AreaRoadName").empty();
						 $("#AreaRoadName").append("<option >--請選擇區域--</option>")
				 for (var i = 0; i < result.length; i++) {
					 $("#AreaRoadName").append('<option value="'+result[i]+'">'+ result[i]+ '</option>');
					 }
					},
					 error : function() {
						 alert("AJAX-grade發生錯誤囉!")
						 }
					 });
			 });
	
		 $("#btnLoc").click(function() {
			 var twCityName = (
					 $('#twCityName').get(0).selectedIndex) > 0 ? $('#twCityName option:selected').val(): '';
					 var CityAreaName = ($('#CityAreaName').get(0).selectedIndex) > 0 ? $('#CityAreaName option:selected').val(): '';
					 var AreaRoadName = ($('#AreaRoadName').get(0).selectedIndex) > 0 ? $('#AreaRoadName option:selected').val(): '';
					 var num = $('#num').val().trim().length != 0 ? $('#num').val()+ "號": '';
					 var locTotal = twCityName+ CityAreaName+ AreaRoadName + num;
					 $("#addressTotal").attr("value", locTotal);
					 });
		 })
		</script>
	
		<!-- Optional JavaScript -->
		<!-- jQuery first, then Popper.js, then Bootstrap JS start-->
		<script src="<%=request.getContextPath()%>/bootstrap/jquery-3.3.1.slim.min.js"></script>
		<script src="<%=request.getContextPath()%>/bootstrap/popper.min.js"></script>
		<script src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.min.js"></script>
		<!-- jQuery first, then Popper.js, then Bootstrap JS end-->
	
	</body>
</html>