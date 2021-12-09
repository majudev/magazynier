var userEditAllowed = false;
var userDeleteAllowed = false;

$(document).ready(function(){
	onReady();
	
	$.ajax({
		url: apiUrl + "/user/permission/check/user.self.edit",
		method: "GET"
	}).done(function(data, textStatus){
		if(data == "ALLOW"){
			$("#edit_displayname_button").attr('disabled', false);
			$("#edit_email_button").attr('disabled', false);
		}
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
	
	$.ajax({
		url: apiUrl + "/user/permission/check/user.self.changepassword",
		method: "GET"
	}).done(function(data, textStatus){
		if(data == "ALLOW"){
			$("#edit_password_button").attr('disabled', false);
		}
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
	
	$.ajax({
		url: apiUrl + "/user/permission/check/user.list",
		method: "GET"
	}).done(function(data, textStatus){
		if(data == "ALLOW"){
			$("#user_management_panel").show();
			
			$.ajax({
				url: apiUrl + "/user/permission/check/user.edit",
				method: "GET"
			}).done(function(data, textStatus){
				if(data == "ALLOW") userEditAllowed = true;
				
				$.ajax({
					url: apiUrl + "/user/permission/check/user.delete",
					method: "GET"
				}).done(function(data, textStatus){
					if(data == "ALLOW") userDeleteAllowed = true;
					refreshUserList();
				}).fail(function(xhr, textStatus){
					refreshUserList();
				});
			}).fail(function(xhr, textStatus){
				refreshUserList();
			});
		}
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
	
	$.ajax({
		url: apiUrl + "/user/permission/check/user.register",
		method: "GET"
	}).done(function(data, textStatus){
		if(data == "ALLOW"){
			$.ajax({
				url: apiUrl + "/user/permission/check/permissiongroup.list",
				method: "GET"
			}).done(function(data, textStatus){
				if(data == "ALLOW"){
					$("#user_register_panel").show();
			
					$.ajax({
						url: apiUrl + "/permissiongroup/list",
						method: "GET",
						contentType: "application/json"
					}).done(function(data, textStatus){
						for(var group of data){
							$(".permissiongroup_select").prepend('<option value="' + group.groupName + '">' + group.groupName + '</option>');
						}
						clearRegistrationForm();
					}).fail(function(xhr, textStatus){
						alert("Błąd krytyczny - strona zostanie przeładowana");
						location.reload();
					});
				}
			}).fail(function(xhr, textStatus){
				alert("Błąd krytyczny - strona zostanie przeładowana");
				location.reload();
			});
		}
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
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

function refreshUserList(){
	$.ajax({
		url: apiUrl + "/user/list",
		method: "GET",
		contentType: "application/json"
	}).done(function(data, textStatus){
		$("#user_list_body").empty();
		for(var user of data){
			var displayname = '<i>brak</i>';
			if(user.displayname != null) displayname = user.displayname;
			
			var email = user.email;
			if(!user.active) email += ' <i>(niepotwierdzony)</i>';
			
			var permissiongroup = 'Domyślna';
			if(user.permissionGroup != null) permissiongroup = user.permissionGroup;
			
			var editbutton = '<button id="userlist_edit_' + user.username + '" class="btn btn-sm btn-dark">Edytuj</button>';
			if(!userEditAllowed) editbutton = '';
			
			var deletebutton = '<button id="userlist_delete_' + user.username + '" class="btn btn-sm btn-danger">Usuń</button>';
			if(!userDeleteAllowed) deletebutton = '';
			
			$("#user_list_body").append('<tr><td>' + displayname + '</td><td>' + user.username + '</td><td>' + email + '</td><td>' + permissiongroup + '</td><td>' + editbutton + ' ' + deletebutton + '</td></tr>');
			
			$("#userlist_edit_" + user.username).attr('username', user.username);
			$("#userlist_edit_" + user.username).attr('displayname', (user.displayname == null) ? '' : user.displayname);
			$("#userlist_edit_" + user.username).attr('email', user.email);
			$("#userlist_edit_" + user.username).attr('permissiongroup', permissiongroup);
			$("#userlist_edit_" + user.username).click(function(){
				$("#edit_user_displayname_input").val($(this).attr('displayname'));
				$("#edit_user_username_div").text($(this).attr('username'));
				$("#edit_user_email_input").val($(this).attr('email'));
				$("#edit_user_permissiongroup_select").val(($(this).attr('permissiongroup') == "Domyślna") ? '' : $(this).attr('permissiongroup') );
				$("#edit_user_modal").modal('show');
			});
			
			$("#userlist_delete_" + user.username).attr('username', user.username);
			$("#userlist_delete_" + user.username).click(function(){
				$("#delete_user_username_div").text($(this).attr('username'));
				$("#delete_user_modal").modal('show');
			});
		}
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
}

function clearRegistrationForm(){
	$("#new_user_username_input").val('');
	$("#new_user_displayname_input").val('');
	$("#new_user_password_input").val('');
	$("#new_user_email_input").val('');
	$("#new_user_permissiongroup_select").val('');
}

function process_register(){
	var username = $("#new_user_username_input").val();
	var displayname = $("#new_user_displayname_input").val();
	var password = $("#new_user_password_input").val();
	var email = $("#new_user_email_input").val();
	var permissiongroup = $("#new_user_permissiongroup_select").val();
	
	if(username == null || username == ""){
		$("#new_user_status").css('color', 'red');
		$("#new_user_status").text('Błąd: nazwa użytkownika nie może być pusta');
		$("#new_user_status").show();
	}else if(password == null || password == ""){
		$("#new_user_status").css('color', 'red');
		$("#new_user_status").text('Błąd: hasło nie może być puste');
		$("#new_user_status").show();
	}else if(email == null || email == ""){
		$("#new_user_status").css('color', 'red');
		$("#new_user_status").text('Błąd: email nie może być pusty');
		$("#new_user_status").show();
	}else{
		$.ajax({
			url: apiUrl + "/user/register",
			method: "POST",
			contentType: "application/json",
			data: JSON.stringify({
				username: username,
				displayname: displayname,
				password: password,
				email: email,
				permissionGroup: permissiongroup
			})
		}).done(function(data, textStatus){
			$("#new_user_status").css('color', 'green');
			$("#new_user_status").text('Dodano użytkownika!');
			$("#new_user_status").show();
			clearRegistrationForm();
			refreshUserList();
		}).fail(function(xhr, textStatus){
			$("#new_user_status").css('color', 'red');
			$("#new_user_status").text('Błąd: ' + xhr.responseJSON.message);
			$("#new_user_status").show();
		});
	}
}

$("#save_user_button").click(function(){
	var username = $("#edit_user_username_div").text();
	var displayname = $("#edit_user_displayname_input").val();
	var email = $("#edit_user_email_input").val();
	var permissiongroup = $("#edit_user_permissiongroup_select").val();
	
	if(email == null || email == ""){
		$("#edit_user_error_div").text('Błąd: email nie może być pusty');
		$("#edit_user_error_div").show();
	}else{
		$.ajax({
			url: apiUrl + "/user/edit/" + username,
			method: "PUT",
			contentType: "application/json",
			data: JSON.stringify({
				displayname: displayname,
				email: email,
				permissionGroup: permissiongroup
			})
		}).done(function(data, textStatus){
			refreshUserList();
			$("#edit_user_modal").modal('hide');
		}).fail(function(xhr, textStatus){
			$("#new_user_status").css('color', 'red');
			$("#new_user_status").text('Błąd: ' + xhr.responseJSON.message);
			$("#new_user_status").show();
		});
	}
});

$("#delete_user_button").click(function(){
	if($("#delete_user_username_div").text() == user.username){
		$("#delete_user_error_div").text("Błąd: Nie możesz usunąć z systemu samego siebie!");
		$("#delete_user_error_div").show();
	}else{
		$.ajax({
			url: apiUrl + "/user/delete/" + $("#delete_user_username_div").text(),
			method: "DELETE"
		}).done(function(data, textStatus){
			refreshUserList();
			$("#delete_user_modal").modal('hide');
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}
});