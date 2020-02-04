<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="sharonxi_CSCI201L_Assignment3.DatabaseManager" 
    import="sharonxi_CSCI201L_Assignment3.LoginServlet"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>BookWorm - Login</title>
<link rel="stylesheet" type="text/css" href="CSS/login.css"></link>
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
<script>
	/*
	When user tries to make another search in the header's form, 
	validate() checks that their search is valid and successful. 
	If unsuccessful, it stays on the SearchResults page and prints
	error messages, returns false. 
	*/
	function validate() {
		document.getElementById("error").innerHTML = "";
  	  	if(document.myform.terms.value.trim()=="") {
  		  	document.getElementById("error").innerHTML = "<span id=\"error\" class=\"error\">Please enter search terms.</span>";
  		  	return false;
  	  	}
  		var xhttp = new XMLHttpRequest();
    	xhttp.open("GET", "https://www.googleapis.com/books/v1/volumes?q=" 
    		  + document.myform.category.value + document.myform.terms.value.replace(' ','+')
    		  + "&key=" + "AIzaSyDgtNCqeYvj1BBw94U1TLT7OTDQY2oJaNc", false);
    	xhttp.send();
    	var text = xhttp.responseText.trim();
		var result = JSON.parse(xhttp.responseText);
  	 	if (result.totalItems > 0) {
        	console.log(result);
        	console.log(result.items[0].volumeInfo.title);
        }
  	  	else{
  			document.getElementById("error").innerHTML = "<span id=\"error\" class=\"error\">Unsuccessful search.</span>";
  			return false;
  	  	}
    	sessionStorage.setItem("text", text);
    	sessionStorage.setItem("terms", document.myform.terms.value);
    	sessionStorage.setItem("category", document.myform.category.value);
    	return true;
	}
</script>
<%-- 
	div class header has the bookworm image/link back to the home page
	and a form that will stay on the page and print error messages if 
	the search is invalid or unsuccessful. The inputs in the header are
	all under the class "top". 
--%>
<div class="header">
	<div id="top" class="top"><a rel="noopener" href="HomePage.jsp"><img src="Images/bookworm.png" width="150"></a>
	<form class="top" name="myform" method ="GET" action="SearchServlet" onsubmit="return validate();">
    	<input class="top" type="text" name="terms" placeholder="What book is on your mind?"></input>
    	<input class="top" type="submit" name="submit" value="">
    	<table>
    		<tr>
    		<td><input class="top" type="radio" name="category" value="intitle:"> Name</td>
    		<td><input class="top" type="radio" name="category" value="isbn:"> ISBN</td>
    		<tr>
			<td><input class="top" type="radio" name="category" value="inauthor:"> Author</td>
			<td><input class="top" type="radio" name="category" value="inpublisher:"> Publisher</td>
    	</table>
    </form>
    <span id="error"></span>
    </div>
    <h2></h2>
</div>
<%-- 
	div class content includes the form for the user to sign in. 
	The form asks for a username and password. If the user has invalid
	entries, it will print an error message. Otherwise, the user will
	be logged in and sent back to the home page. 
--%>
<div class="content">
	<form class="login" name="login" method="GET" action="LoginServlet">
		Username <br/>
		<input class="login" type="text" name="username"></input> <br/>
		Password <br/>
		<input type="password" id="pass" name="password" required> <br/>
		<input class="login" type="submit" name="sign-in" value="Sign In"></input>
	</form>	
	<span id="emptyform"><%=errform %></span><br/>
	<span id="baduser"><%=erruser %></span><br/>
	<span id="badpass"><%=errpass %></span><br/>
</div>
</body>
</html>