$(document).ready(function(){
	onReadyHeader();
	
	$(".set-title").text(appName);
	$(".inject-baseurl").each(function(index, element){
		var hrefValue = $(element).attr("href");
		hrefValue = slashBaseUrl + "/" + hrefValue;
		$(element).attr("href", hrefValue);
	});
	
	$.ajax({
		url: apiUrl + "/warehouse/get",
		method: "GET",
		dataType: "json"
	}).done(function(data){
		for(var warehouse of data){
			$("#warehouses_link").after('<a class="dropdown-item inject-baseurl" href="warehouse.html?id=' + warehouse.id + '">' + warehouse.name + '</a>');
			$(".append-storageunits").after('<li id="su_entry_warehouse' + warehouse.id + '" class="nav-item dropdown"><a class="nav-link dropdown-toggle" data-bs-toggle="dropdown">' + warehouse.name + '</a><div id="su_entry_warehouse' + warehouse.id + '_sus" class="dropdown-menu"></div></li>');
			for(var storageunit of warehouse.storageUnits){
				$("#su_entry_warehouse" + warehouse.id + "_sus").append('<a class="dropdown-item inject-baseurl" href="storageunit.html?id=' + storageunit.id + '">' + storageunit.name + '</a>');
			}
		}
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
	
	$.ajax({
		url: apiUrl + "/itemgroup/meta",
		method: "GET",
		dataType: "json"
	}).done(function(data){
		for(var itemgroup of data){
			$("#itemgroups_link").after('<a class="dropdown-item inject-baseurl" href="itemgroup.html?id=' + itemgroup.id + '">' + itemgroup.name + '</a>');
		}
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
});

function onReadyHeader(){
	if(user != null){
		if(user.permissionGroup == "admin"){
			//$(".enable-admin").show();
		}
	}else setTimeout(onReadyHeader, 50);
}