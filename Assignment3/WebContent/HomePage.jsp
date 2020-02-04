<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>BookWorm - Home</title>
    <script>
    /*
	when user tries to make a search, validate() checks that their 
	search is valid and successful. 
	If unsuccessful, it stays on the Home page and prints
	error messages, returns false. 
	*/
      function validate() {
    	  document.getElementById("error").innerHTML = "";
    	  if(document.myform.terms.value.trim()==""){
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
<link rel="stylesheet" type="text/css" href="CSS/home.css"></link>
<link rel="stylesheet" type="text/css" href="CSS/homeheader.css"></link>
</head>
<body>
<%
	String curruser=(String) session.getAttribute("user");
%>
<div class="header" id="header">
	Current user: <%=curruser %> <br/>
	<%-- 
	Header implements BookWorm icon in top left. If there is no user logged in, 
	place Login/Register buttons in top right. If there is a user logged in, 
	place Profile/SignOut buttons in top right. --%>
	<a rel="noopener" href="HomePage.jsp"><img src="Images/bookworm.png" width="200"></a>
	<%
	if(curruser==null){
	%>
	<a rel="noopener" href="Register.jsp" class="profile" id="profile">Register</a>
	<a rel="noopener" href="Login.jsp" class="profile" id="profile">Login</a>
	<%} 
	else{ %>
	<a rel="noopener" href="SignOutServlet" class="profile" id="profile">Sign Out</a>
	<a rel="noopener" href="Profile.jsp" class="profile" id="profile">Profile</a>
	<%} %>
</div>
<div>
	<%-- Main form for user to search for books --%>
	<p>BookWorm: Just a Mini Program... Happy Days!</p>
	<span id="error"></span>
	<form name="myform" method ="POST" action="SearchServlet" onsubmit="return validate();">
    	<input type="text" name="terms" placeholder="Search for your favorite book!"></input>
    	<table>
    		<tr>
    		<td><input type="radio" name="category" value="intitle:"> Name</td>
    		<td><input type="radio" name="category" value="isbn:"> ISBN</td>
    		<tr>
			<td><input type="radio" name="category" value="inauthor:"> Author</td>
			<td><input type="radio" name="category" value="inpublisher:"> Publisher</td>
    	</table>
    	<input type="submit" name="submit" value="Search!">
    </form>
    <span id="error"></span>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<!-- <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.100.2/js/materialize.min.js"></script> -->
</body>
</html>