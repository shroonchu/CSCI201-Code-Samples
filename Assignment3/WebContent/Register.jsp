<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>BookWorm - Register</title>
<link rel="stylesheet" type="text/css" href="CSS/register.css"></link>
</head>
<body>
<%
	String curruser=(String) session.getAttribute("user");
	String errform = "";
	if(request.getAttribute("emptyform")!=""&&request.getAttribute("emptyform")!=null){
		errform=(String)request.getAttribute("emptyform");
	}
	String erruser = "";
	if(request.getAttribute("baduser")!=""&&request.getAttribute("baduser")!=null){
		erruser=(String)request.getAttribute("baduser");
	}
	String errpass = "";
	if(request.getAttribute("badpass")!=""&&request.getAttribute("badpass")!=null){
		errpass=(String)request.getAttribute("badpass");
	}
%>
<%-- div class header has the bookworm image/link back to the home page
	 and a form that will stay on the page and print error messages if
	 the search is invalid or unsuccessful. The inputs in the header are
	 all under the class "top". --%>
<div class="header">
	<div id="top" class="top"><a rel="noopener" href="HomePage.jsp"><img src="Images/bookworm.png" width="150"></a>
	<form name="myform" method ="GET" action="SearchServlet" onsubmit="return validate();">
    	<input type="text" name="terms" placeholder="What book is on your mind?"></input>
    	<input type="submit" name="submit" value="">
    	<table>
    		<tr>
    		<td><input type="radio" name="category" value="intitle:"> Name</td>
    		<td><input type="radio" name="category" value="isbn:"> ISBN</td>
    		<tr>
			<td><input type="radio" name="category" value="inauthor:"> Author</td>
			<td><input type="radio" name="category" value="inpublisher:"> Publisher</td>
    	</table>
    </form>
    <span id="error"></span>
    </div>
    <h2></h2>
</div>
<div class="content">
	<form class="register" name="register" method="GET" action="RegisterServlet">
		Username <br/>
		<input class="register" type="text" name="username"></input> <br/>
		Password <br/>
		<input type="password" id="pass" name="password" required> <br/>
		Confirm Password <br/>
		<input type="password" id="pass" name="confirmPassword" required> <br/>
		<input class="register" type="submit" name="sign-in" value="Sign In"></input>
	</form>	
	<span id="emptyform"><%=errform %></span><br/>
	<span id="baduser"><%=erruser %></span><br/>
	<span id="badpass"><%=errpass %></span><br/>
</div>
</body>
</html>