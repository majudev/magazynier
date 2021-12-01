$(document).ready(function(){
	$(".set-title").text(appName);
	$(".inject-baseurl").each(function(index, element){
		var hrefValue = $(element).attr("href");
		hrefValue = baseUrl + "/" + hrefValue;
		$(element).attr("href", hrefValue);
	});
	
	$.ajax({
		url: apiUrl + "/user/logout",
		method: "POST",
		contentType: "application/json",
		dataType: "json",
	}).always(function(){
		window.location.replace(baseUrl + "/login.html");
	});
});