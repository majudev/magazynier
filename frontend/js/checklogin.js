var user = null;
$.ajax({
	url: apiUrl + "/user",
	method: "GET",
	dataType: "json"
}).done(function(data){
	user = data;
}).fail(function(xhr, textStatus){
	if(xhr.status == 401){
		window.stop();
		window.location.replace(baseUrl + "/login.html");
	}else{
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	}
});