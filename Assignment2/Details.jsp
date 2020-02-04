<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>BookWorm - Details</title>
<link rel="stylesheet" type="text/css" href="details.css"></link>
</head>
<body>
<%-- div class header has the bookworm image/link back to the home page
	 and a form that will stay on the page and print error messages if 
	 the search is invalid or unsuccessful --%>
<div class="header">
	<div id="top" class="top"><a rel="noopener" href="HomePage.jsp"><img id ="left" src="bookworm.png" width="150"></a>
	<form name="myform" method ="GET" action="SearchResults.jsp" onsubmit="return validate();">
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
<%-- div class content contains the printed html of the 
	 book cover and other information --%>
<div id="content" class="content"></div>
<script>
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
	let image = sessionStorage.getItem("image");
	let title = sessionStorage.getItem("title");
	let author = sessionStorage.getItem("author");
	let publisher = sessionStorage.getItem("publisher");
	let publishedDate = sessionStorage.getItem("publishedDate");
	let isbn = sessionStorage.getItem("isbn");
	let summary = sessionStorage.getItem("summary");
	let rating = sessionStorage.getItem("rating");
	if(author=="undefined"){author="N/A";}
	if(publisher=="undefined"){publisher="N/A";}
	if(publishedDate=="undefined"){publishedDate="N/A";}
	if(isbn=="undefined"){isbn="N/A";}
	if(summary=="undefined"){summary="N/A";}
	
	// print all of the book data
	document.getElementById("content").innerHTML += "<br>";
	document.getElementById("content").innerHTML += "<a rel=\"noopener\" href=\"SearchResults.jsp\"><img id=\"left\" src=\""
    	+ image + "\" style=\"width:300px;height:500px;margin-right:100px;\"></a>";
	document.getElementById("content").innerHTML += "<br><span class=\"title\">" + title + "</span>";
	document.getElementById("content").innerHTML += "<br><span class=\"author\"><b><i>Author: " + author + "</i></b></span>";
	document.getElementById("content").innerHTML += "<br><span class=\"other\"><b>Publisher: </b>" + publisher + "</span>";
	document.getElementById("content").innerHTML += "<br><span class=\"other\"><b>Published Date: </b>" + publishedDate + "</span>";
	document.getElementById("content").innerHTML += "<br><span class=\"other\"><b>ISBN: </b>" + isbn + "</span>";
	document.getElementById("content").innerHTML += "<br><span class=\"summ\"><b>Summary: </b>" + summary + "</span>";
	document.getElementById("content").innerHTML += "<br>";
	// print out stars
	if(rating=="undefined"){
		document.getElementById("content").innerHTML += "<br><span class=\"other\"><b>Rating: </b>No ratings</span>";
	}
	else{
		document.getElementById("content").innerHTML += "<br><span class=\"other\"><b>Rating: </b></span>";
		let i = 0;
		while(i<5){
			if(i>=rating){
				document.getElementById("content").innerHTML += "<img src=\"emptystar.png\">";
			}
			else if((i+1)<=rating){
				document.getElementById("content").innerHTML += "<img src=\"fullstar.png\">";
			}
			else {
				document.getElementById("content").innerHTML += "<img src=\"halfstar.png\">";
			}
			i++;
		}
	}
</script>
</body>
</html>