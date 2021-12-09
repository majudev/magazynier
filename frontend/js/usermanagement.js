const urlParams = new URLSearchParams(window.location.search);
const actionId = urlParams.get('action');

$(document).ready(function(){
	$(".set-title").text(appName);
	$(".inject-baseurl").each(function(index, element){
		var hrefValue = $(element).attr("href");
		hrefValue = baseUrl + "/" + hrefValue;
		$(element).attr("href", hrefValue);
	});
	
	if(actionId == "forgotpassword"){
		$("#forgotpassword").show();
	}else if(actionId == "resetpassword"){
		$("#resetpassword").show();
	}else if(actionId == "activate"){
		const code = urlParams.get('code');
		const username = urlParams.get('username');
		$.ajax({
			url: apiUrl + "/user/activate/" + username + "/code/" + code,
			method: "POST",
			//dataType: "json"
		}).done(function(data, textStatus){
			$("#activate_status").text("Sukces!");
			$("#activate").show();
			setTimeout(function(){
				window.location.replace(baseUrl + "/login.html");
			}, 1000);
		}).fail(function(){
			$("#activate_status").text("Błąd!");
			$("#activate").show();
		});
	}
});

function process_resetpassword(){
	const code = urlParams.get('code');
	var password = $("#password_input").val();
	var password2 = $("#password2_input").val();
	
	if(password != password2){
		$("#resetpassword_error").text("Błąd! Hasła nie pasują do siebie.");
		$("#resetpassword_error").show();
		return;
	}

	$.ajax({
		url: apiUrl + "/user/forgotpassword/" + code + "/reset/" + password,
		method: "POST"
	}).done(function(data, textStatus){
		$("#forgotpassword_error").removeClass("text-danger");
		$("#forgotpassword_error").text("Sukces! Hasło zostało zresetowane.");
		$("#forgotpassword_error").show();
		
		setTimeout(function(){
			window.location.replace(baseUrl + "/login.html");
		}, 1000);
	}).fail(function(xhr, textStatus){
		$("#resetpassword_error").text("Błąd! Nie znaleziono takiego użytkownika, lub nie masz uprawnień do resetu własnego hasła.");
		$("#resetpassword_error").show();
	});
}

function process_forgotpassword(){
	var username = $("#username_input").val();

	$.ajax({
		url: apiUrl + "/user/forgotpassword/" + username,
		method: "POST"
	}).done(function(data, textStatus){
		$("#forgotpassword_error").removeClass("text-danger");
		$("#forgotpassword_error").text("Sukces! Otrzymałeś maila z linkiem do resetu hasła.");
		$("#forgotpassword_error").show();
	}).fail(function(xhr, textStatus){
		$("#forgotpassword_error").text("Błąd! Nie znaleziono takiego użytkownika, lub nie masz uprawnień do resetu własnego hasła.");
		$("#forgotpassword_error").show();
	});
}

/*function process_login(){
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
}*/