$(document).ready(function(){
	$.ajax({
		url: apiUrl + "/warehouse/get",
		method: "GET",
		dataType: "json"
	}).done(function(data){
		for(var warehouse of data){
			$("#new_warehouse").before(warehouseBuilder(warehouse));
			warehouseBuilderRegisterEvents(warehouse);
			
			$("#move_storageunit_new_warehouse_select").append('<option id="move_storageunit_new_warehouse_select_warehouse' + warehouse.id + '" class="move-su-entry" value="' + warehouse.id + '">' + warehouse.name + '</option>');
		}
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
});

function warehouseBuilder(warehouse){
	var builder = '';
	//object open
	builder += '<div id="warehouse_' + warehouse.id + '" class="col-lg-4 col-sm-12">';
	builder += '<div class="p-2">';
	builder += '<ul class="list-group">';
	
	//header
	builder += '<li class="list-group-item list-group-item-info d-flex justify-content-center bg-dark text-center text-white">'
	builder += '<h4 class="mb-1 mt-1">' + warehouse.name + '</h4>';
	builder += '</li>';
	
	//description & location
	if(warehouse.description != null || warehouse.location != null){
		builder += '<li id="descfield_warehouse_' + warehouse.id + '" class="list-group-item d-flex justify-content-around align-items-center align-text-center p-3">';
		if(warehouse.description != null) builder += '<div id="description_warehouse_' + warehouse.id + '" style="display: inline">Opis: ' + warehouse.description + '</div>';
		if(warehouse.location != null) builder += '<div id="location_warehouse_' + warehouse.id + '" style="display: inline">Lokalizacja: ' + warehouse.location + '</div>';
		builder += '</li>';
	}
	
	for(var storageUnit of warehouse.storageUnits){
		builder += '<li class="list-group-item d-flex justify-content-between align-items-center">';
		builder += '<div>';
		builder += '<b>' + storageUnit.name + '</b> ';
		if(storageUnit.description != null) builder += '- ' + storageUnit.description;
		if(storageUnit.location != null) builder += ' <i>' + storageUnit.location + '</i>';
		builder += '</div>';
		builder += '<div>';
		builder += '<button id="move_storageunit_' + storageUnit.id + '_warehouse_' + warehouse.id + '" type="button" class="btn btn-dark btn-sm">Przenieś</button> ';
		builder += '<button id="show_storageunit_' + storageUnit.id + '_warehouse_' + warehouse.id + '" type="button" class="btn btn-dark btn-sm">Pokaż</button> ';
		builder += '</div>';
		builder += '</li>';
	}
	
	//edit & add new
	builder += '<li class="list-group-item d-flex justify-content-between align-items-center">';
    builder += '<div></div>';
    builder += '<div>';
    if(warehouse.storageUnits.length == 0) builder += '<button id="delete_warehouse_' + warehouse.id + '" type="button" class="btn btn-danger btn-sm">Usuń magazyn</button> ';
    builder += '<button id="edit_warehouse_' + warehouse.id + '" type="button" class="btn btn-dark btn-sm">Edytuj właściwości magazynu</button> ';
    builder += '<button id="new_storageunit_warehouse_' + warehouse.id + '" type="button" class="btn btn-dark btn-sm">Nowy element</button> ';
    builder += '<button id="show_storageunit_warehouse_' + warehouse.id + '" type="button" class="btn btn-dark btn-sm">Pokaż</button> ';
    builder += '</div>';
    builder += '</li>';
	
	//object close
	builder += '</ul>';
	builder += '</div>';
	builder += '</div>';
	
	return builder;
}

function warehouseBuilderRegisterEvents(warehouse){
	//register events
	$("#edit_warehouse_" + warehouse.id).click(function(){
		$("#edit_warehouse_id_input").val(warehouse.id);
		$("#edit_warehouse_name_input").val(warehouse.name);
		$("#edit_warehouse_location_input").val(warehouse.location);
		$("#edit_warehouse_description_input").val(warehouse.description);
		$("#edit_warehouse_modal").modal('show');
	});
	$("#delete_warehouse_" + warehouse.id).click(function(){
		$("#delete_warehouse_div").text(warehouse.name);
		$("#delete_warehouse_id").val(warehouse.id);
		$("#delete_warehouse_modal").modal('show');
	});
	
	$("#new_storageunit_warehouse_" + warehouse.id).click(function(){
		$("#new_storageunit_warehouse_div").text(warehouse.name);
		$("#new_storageunit_warehouseid_input").val(warehouse.id);
		$("#new_storageunit_modal").modal('show');
	});
	
	$("#show_storageunit_warehouse_" + warehouse.id).click(function(){
		window.open(baseUrl + "/warehouse.html?id=" + warehouse.id, '_blank').focus();
	});
	
	for(var storageUnit of warehouse.storageUnits){
		$("#show_storageunit_" + storageUnit.id + "_warehouse_" + warehouse.id).attr("su_id", storageUnit.id);
		$("#show_storageunit_" + storageUnit.id + "_warehouse_" + warehouse.id).click(function(){
			window.open(baseUrl + "/storageunit.html?id=" + $(this).attr("su_id"), '_blank').focus();
		});
		$("#move_storageunit_" + storageUnit.id + "_warehouse_" + warehouse.id).attr("su_id", storageUnit.id);
		$("#move_storageunit_" + storageUnit.id + "_warehouse_" + warehouse.id).attr("su_name", storageUnit.name);
		$("#move_storageunit_" + storageUnit.id + "_warehouse_" + warehouse.id).attr("w_id", warehouse.id);
		$("#move_storageunit_" + storageUnit.id + "_warehouse_" + warehouse.id).attr("w_name", warehouse.name);
		$("#move_storageunit_" + storageUnit.id + "_warehouse_" + warehouse.id).click(function(){
			$("#move_storageunit_name_div").text($(this).attr("su_name"));
			$("#move_storageunit_warehouse_div").text($(this).attr("w_name"));
			$("#move_storageunit_id_input").val($(this).attr("su_id"));
			$("#move_storageunit_warehouseid_input").val($(this).attr("w_id"));
			$(".move-su-entry").attr('disabled', false);
			$("#move_storageunit_new_warehouse_select_warehouse" + $(this).attr("w_id")).attr('disabled', true);
			$("#move_storageunit_modal").modal('show');
		});
	}
}

$("#new_warehouse_button").click(function(){
	$("#new_warehouse_modal").modal('show');
});

$("#save_warehouse_button").click(function(){
	if($("#new_warehouse_name_input").val() == ""){
	    $("#new_warehouse_error_div").text("Błąd: nazwa nie może być pusta!");
	    $("#new_warehouse_error_div").show();
   	}else{
		$("#new_warehouse_error_div").hide();
		$("#save_warehouse_button").attr('disabled', true);
		
		var name = $("#new_warehouse_name_input").val();
		var description = null;
		var location = null;
		if($("#new_warehouse_description_input").val() != "") description = $("#new_warehouse_description_input").val();
		if($("#new_warehouse_location_input").val() != "") location = $("#new_warehouse_location_input").val();
		
		$.ajax({
			url: apiUrl + "/warehouse/new",
			method: "POST",
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify({
				name: name,
				description: description,
				location: location
			})
		}).done(function(data, textStatus){
			data.storageUnits = [];
			$("#new_warehouse").before(warehouseBuilder(data));
			warehouseBuilderRegisterEvents(data);
			$("#move_storageunit_new_warehouse_select").append('<option id="move_storageunit_new_warehouse_select_warehouse' + data.id + '" class="move-su-entry" value="' + data.id + '">' + data.name + '</option>');
			
			$("#save_warehouse_button").attr('disabled', false);
			$("#new_warehouse_modal").modal('hide');
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}
});

$("#delete_warehouse_button").click(function(){
	$("#delete_warehouse_button").attr('disabled', true);

	var id = $("#delete_warehouse_id").val();
	
	$.ajax({
		url: apiUrl + "/warehouse/delete/" + id,
		method: "DELETE",
		contentType: "application/json"
	}).done(function(data, textStatus){
		$("#warehouse_" + id).remove();
		$("#su_entry_warehouse" + id).remove();
		$("#move_storageunit_new_warehouse_select_warehouse" + id).remove();
		
		$("#delete_warehouse_button").attr('disabled', false);
		$("#delete_warehouse_modal").modal('hide');
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
});

$("#save_existing_warehouse_button").click(function(){
	if($("#edit_warehouse_name_input").val() == ""){
	    $("#edit_warehouse_error_div").text("Błąd: nazwa nie może być pusta!");
	    $("#edit_warehouse_error_div").show();
   	}else{
		$("#edit_warehouse_error_div").hide();
		$("#save_existing_warehouse_button").attr('disabled', true);
		
		var id = $("#edit_warehouse_id_input").val();
		var name = $("#edit_warehouse_name_input").val();
		var description = $("#edit_warehouse_description_input").val();
		var location = $("#edit_warehouse_location_input").val();
		
		$.ajax({
			url: apiUrl + "/warehouse/edit/" + id,
			method: "PUT",
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify({
				name: name,
				description: description,
				location: location
			})
		}).done(function(data, textStatus){
			$("#warehouse_" + id).replaceWith(warehouseBuilder(data));
			warehouseBuilderRegisterEvents(data);
			
			$("#move_storageunit_new_warehouse_select_warehouse" + id).replaceWith('<option id="move_storageunit_new_warehouse_select_warehouse' + data.id + '" class="move-su-entry" value="' + data.id + '">' + data.name + '</option>');
			
			$("#save_existing_warehouse_button").attr('disabled', false);
			$("#edit_warehouse_modal").modal('hide');
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}
});

$("#move_storageunit_button").click(function(){
	if($("#move_storageunit_new_warehouse_select").val() == $("#move_storageunit_warehouseid_input").val() || $("#move_storageunit_new_warehouse_select").val() == null){
	    $("#move_storageunit_error_div").text("Błąd: nie możesz przenieść elementu do tego samego magazynu!");
	    $("#move_storageunit_error_div").show();
   	}else{
		$("#move_storageunit_error_div").hide();
		$("#move_storageunit_button").attr('disabled', true);
		
		var newWarehouseId = $("#move_storageunit_new_warehouse_select").val();
		var storageUnitId = $("#move_storageunit_id_input").val();
		var oldWarehouseId = $("#move_storageunit_warehouseid_input").val();
		
		$.ajax({
			url: apiUrl + "/storageunit/move/" + storageUnitId + "/" + newWarehouseId,
			method: "PUT",
			contentType: "application/json"
		}).done(function(data, textStatus){
			// reload old warehouse
			$.ajax({
				url: apiUrl + "/warehouse/get/" + oldWarehouseId,
				method: "GET",
				dataType: "json"
			}).done(function(data){
				$("#warehouse_" + oldWarehouseId).replaceWith(warehouseBuilder(data));
				warehouseBuilderRegisterEvents(data);
			}).fail(function(xhr, textStatus){
				alert("Błąd krytyczny - strona zostanie przeładowana");
				location.reload();
			});
			
			//reload new warehouse
			$.ajax({
				url: apiUrl + "/warehouse/get/" + newWarehouseId,
				method: "GET",
				dataType: "json"
			}).done(function(data){
				$("#warehouse_" + newWarehouseId).replaceWith(warehouseBuilder(data));
				warehouseBuilderRegisterEvents(data);
			}).fail(function(xhr, textStatus){
				alert("Błąd krytyczny - strona zostanie przeładowana");
				location.reload();
			});
			
			$("#move_storageunit_button").attr('disabled', false);
			$("#move_storageunit_modal").modal('hide');
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}
});

$("#save_storageunit_button").click(function(){
	if($("#new_storageunit_name_input").val() == ""){
	    $("#new_storageunit_error_div").text("Błąd: nazwa nie może być pusta!");
	    $("#new_storageunit_error_div").show();
   	}else{
		$("#new_storageunit_error_div").hide();
		$("#save_storageunit_button").attr('disabled', true);
		
		var warehouseId = $("#new_storageunit_warehouseid_input").val();
		var name = $("#new_storageunit_name_input").val();
		var description = $("#new_storageunit_description_input").val();
		var location = $("#new_storageunit_location_input").val();
		
		$.ajax({
			url: apiUrl + "/storageunit/new/" + warehouseId,
			method: "POST",
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify({
				name: name,
				description: description,
				location: location
			})
		}).done(function(data, textStatus){
			$.ajax({
				url: apiUrl + "/warehouse/get/" + warehouseId,
				method: "GET",
				dataType: "json"
			}).done(function(data){
				$("#warehouse_" + warehouseId).replaceWith(warehouseBuilder(data));
				warehouseBuilderRegisterEvents(data);
			}).fail(function(xhr, textStatus){
				alert("Błąd krytyczny - strona zostanie przeładowana");
				location.reload();
			});
			
			$("#save_storageunit_button").attr('disabled', false);
			$("#new_storageunit_modal").modal('hide');
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}
});