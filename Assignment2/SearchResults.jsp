<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>BookWorm - Search Results</title>
<link rel="stylesheet" type="text/css" href="results.css"></link>
</head>
<body>
<%-- div class header has the bookworm image/link back to the home page
	 and a form that will stay on the page and print error messages if 
	 the search is invalid or unsuccessful --%>
<div class="header">
	<div id="top" class="top"><a rel="noopener" href="HomePage.jsp"><img src="bookworm.png" width="150"></a>
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
<%-- div class message contains the html for "Results for ___" --%>
<div id="message"></div>
<%-- div class content contains the printed html of the 
	 book cover and other information --%>
<div id="content" class="content"></div>
<script>
	/*
	when user tries to make another search in the header's form, 
	validate() checks that their search is valid and successful. 
	If unsuccessful, it stays on the SearchResults page and prints
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
	/* det function stores information of the book in sessionStorage so that
	 	it can be passed on to Details.jsp. i is the index of the book within the
	 	result.items array, and result is the array. 
	 */
	function det(i, result){
		sessionStorage.setItem("image", result.items[i].volumeInfo.imageLinks.thumbnail);
		sessionStorage.setItem("title", result.items[i].volumeInfo.title);
        sessionStorage.setItem("author", result.items[i].volumeInfo.authors);
        sessionStorage.setItem("publisher", result.items[i].volumeInfo.publisher);
        sessionStorage.setItem("publishedDate", result.items[i].volumeInfo.publishedDate);
        sessionStorage.setItem("isbn", result.items[i].volumeInfo.industryIdentifiers[0].identifier);
        sessionStorage.setItem("summary", result.items[i].volumeInfo.description);
        sessionStorage.setItem("rating", result.items[i].volumeInfo.averageRating);
        return true;
	}
	let terms = sessionStorage.getItem("terms");
	let text = sessionStorage.getItem("text");
	result = JSON.parse(text);
	document.getElementById("message").innerHTML += "<span class=\"mesg\">Results for \"" + terms + "\"</span>";
	// loop through results and print information
	for (var i = 0; i < result.items.length; i++) {
        var item = result.items[i];
        if(item.volumeInfo.title==null){continue;}
        let image = item.volumeInfo.imageLinks.thumbnail;
        if(image == null){
        	let image = item.volumeInfo.imageLinks.smallThumbnail;
        }
        let authors = item.volumeInfo.authors;
        if(authors==null){
        	authors = "N/A";
        }
        let summ = item.volumeInfo.description;
        if(summ==null){
        	summ = "N/A";
        }
        document.getElementById("content").innerHTML += "<h2></h2>";
        document.getElementById("content").innerHTML += "<a rel=\"noopener\" href=\"Details.jsp\" onclick=\"return det(" 
        	+ i + ",result);\"><img src=\""
        	+ image
        	+ "\" style=\"width:150px;height:250px;margin-right:100px;\"></a>";
        document.getElementById("content").innerHTML += "<div id=\"book\" class=\"book\"><br><span class=\"title\">" 
        	+ item.volumeInfo.title + "</span>"
        	+ "<br><span class=\"author\"><i>" + authors + "</i></span>"
        	+ "<br><b>Summary: </b>" + summ
        	+ "</div>";
      }
</script>
</body>
</html>