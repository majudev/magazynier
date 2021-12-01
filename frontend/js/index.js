$(document).ready(function(){
	onReady();
});

function onReady(){
	if(user != null){
		$("#username_div").text(user.username);
		$("#email_div").text(user.email);
		if(user.permissionGroup != null) $("#permission_group_div").text(user.permissionGroup);
		if(user.displayname != null) $("#displayname_div").text(user.displayname);
	}else setTimeout(onReady, 50);
}

$("#edit_displayname_button").click(function(){
	$("#edit_displayname_div").text($("#displayname_div").text());
	$("#edit_displayname_modal").modal('show');
});

$("#edit_email_button").click(function(){
	$("#edit_email_div").text($("#email_div").text());
	$("#edit_email_modal").modal('show');
});

$("#edit_password_button").click(function(){
	$("#edit_password_modal").modal('show');
});

$("#save_displayname_button").click(function(){
	if($("#new_displayname_input").val() == ""){
	    $("#edit_displayname_error_div").text("Błąd: nazwa nie może być pusta!");
	    $("#edit_displayname_error_div").show();
   	}else{
		$("#edit_displayname_error_div").hide();
		$("#save_displayname_button").attr('disabled', true);
		$.ajax({
			url: apiUrl + "/user/edit",
			method: "PUT",
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify({
				displayname: $("#new_displayname_input").val()
			})
		}).done(function(data, textStatus){
			$("#displayname_div").text("w trakcie aktualizacji...");
			$("#save_displayname_button").attr('disabled', false);
			$("#edit_displayname_modal").modal('hide');
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}
});

$("#save_email_button").click(function(){
	if($("#new_email_input").val() == ""){
	    $("#edit_email_error_div").text("Błąd: email nie może być pusty!");
	    $("#edit_email_error_div").show();
   	}else{
		$("#edit_email_error_div").hide();
		$("#save_email_button").attr('disabled', true);
		$.ajax({
			url: apiUrl + "/user/edit",
			method: "PUT",
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify({
				email: $("#new_email_input").val()
			})
		}).done(function(data, textStatus){
			$("#email_div").text("w trakcie aktualizacji...");
			$("#save_email_button").attr('disabled', false);
			$("#edit_email_modal").modal('hide');
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}
});

$("#save_password_button").click(function(){
	if($("#current_password_input").val() == ""){
	    $("#edit_password_error_div").text("Błąd: wpisz aktualne hasło!");
	    $("#edit_password_error_div").show();
   	}else if($("#new_password_input").val() != $("#confirm_password_input").val()){
		$("#edit_password_error_div").text("Błąd: hasła nie są zgodne!");
	    $("#edit_password_error_div").show();
	}else{
		$("#edit_password_error_div").hide();
		$("#save_password_button").attr('disabled', true);
		$.ajax({
			url: apiUrl + "/user/changepassword",
			method: "PUT",
			contentType: "application/json",
			data: JSON.stringify({
				password: $("#current_password_input").val(),
				newpassword: $("#new_password_input").val()
			})
		}).done(function(data, textStatus){
			window.location.replace(baseUrl + "/login.html");
		}).fail(function(xhr, textStatus){
			if(xhr.status == 401){
				$("#edit_password_error_div").text("Błąd: obecne hasło nie jest poprawne");
	    		$("#edit_password_error_div").show();
				$("#save_password_button").attr('disabled', false);
			}else{
				alert("Błąd krytyczny - strona zostanie przeładowana");
				location.reload();
			}
		});
	}
});