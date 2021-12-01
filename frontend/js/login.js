$(document).ready(function(){
	$(".set-title").text(appName);
	$(".inject-baseurl").each(function(index, element){
		var hrefValue = $(element).attr("href");
		hrefValue = baseUrl + "/" + hrefValue;
		$(element).attr("href", hrefValue);
	});
	
	$.ajax({
		url: apiUrl + "/user",
		method: "GET",
		dataType: "json"
	}).done(function(data, textStatus){
		window.location.replace(baseUrl + "/index.html");
	});
});

function process_login(){
	var username = $("#username_input").val();
	var password = $("#password_input").val();
	var remember_me = $("#remember_me_input").is(":checked");
	//validate login
	//validate password

	$.ajax({
		url: apiUrl + "/user/login",
		method: "POST",
		contentType: "application/json",
		dataType: "json",
		data: JSON.stringify({
			username: username,
			password: password,
			rememberMe: remember_me,
		})
	}).done(function(data, textStatus){
		window.location.replace(baseUrl + "/index.html");
	}).fail(function(xhr, textStatus){
		if(xhr.status == 401){
			$("#error").text("Błąd: niepoprawne dane logowania");
			$("#error").show();
		}else if(xhr.status == 400){
			$("#error").text("Błąd: nie ma takiego użytkownika");
			$("#error").show();
		}else{
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		}
	});
}