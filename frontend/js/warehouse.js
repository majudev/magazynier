const urlParams = new URLSearchParams(window.location.search);
const warehouseId = urlParams.get('id');

$(document).ready(function(){
	$.ajax({
		url: apiUrl + "/warehouse/get/" + warehouseId,
		method: "GET",
		dataType: "json"
	}).done(function(data){
		warehouseMetaReload(data);
		
		for(var storageUnit of data.storageUnits){
			$.ajax({
				url: apiUrl + "/storageunit/get/" + storageUnit.id,
				method: "GET",
				dataType: "json"
			}).done(function(meta){
				$.ajax({
					url: apiUrl + "/storageunit/items/grouped/" + meta.id,
					method: "GET",
					dataType: "json"
				}).done(function(items){
					$("#new_storageunit").before(storageUnitBuilder(meta, items));
					storageUnitBuilderRegisterEvents(meta, items);
				}).fail(function(xhr, textStatus){
					alert("Błąd krytyczny - strona zostanie przeładowana");
					location.reload();
				});
			}).fail(function(xhr, textStatus){
				alert("Błąd krytyczny - strona zostanie przeładowana");
				location.reload();
			});
		}
		/*for(var storageUnit of data.storageUnits){
			$("#new_warehouse").before(warehouseBuilder(warehouse));
			warehouseBuilderRegisterEvents(warehouse);
			
			$("#move_storageunit_new_warehouse_select").append('<option id="move_storageunit_new_warehouse_select_warehouse' + warehouse.id + '" class="move-su-entry" value="' + warehouse.id + '">' + warehouse.name + '</option>');
		}*/
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
});

function warehouseMetaReload(warehouse){
	$("#warehouse_name").html("Nazwa: <b>" + warehouse.name + "</b>");
	if(warehouse.description != null) $("#warehouse_description").text("Opis: " + warehouse.description);
	if(warehouse.location != null) $("#warehouse_location").text("Lokalizacja: " + warehouse.location);

	$("#edit_warehouse_name_input").val(warehouse.name);
	$("#edit_warehouse_description_input").val(warehouse.description);
	$("#edit_warehouse_location_input").val(warehouse.location);
	
	$("#delete_warehouse_div").text(warehouse.name);
	
	if(warehouse.storageUnits.length == 0) $("#delete_this_warehouse_button").show();
	else $("#delete_this_warehouse_button").hide();
}

function storageUnitBuilder(meta, items){
	var builder = '';
	builder += '<div id="su' + meta.id + '" class="col-lg-4 col-sm-12">';
	builder += '<div class="p-2">';
	builder += '<ul class="list-group">';
	builder += '<li class="list-group-item list-group-item-info d-flex justify-content-center bg-dark text-center text-white">';
	builder += '<h4 class="mb-1 mt-1">' + meta.name + '</h4>';
	builder += '</li>';
	if(meta.description != null || meta.location != null){
		builder += '<li class="list-group-item d-flex justify-content-around align-items-center">';
		if(meta.description != null) builder += '<div>Opis: ' + meta.description + '</div>';
		if(meta.location != null) builder += '<div>Lokalizacja: ' + meta.location + '</div>';
		builder += '</li>';
	}
	
	for(var item of items){
		builder += '<li class="list-group-item d-flex align-items-center">';
		builder += '<div class="flex-fill">';
		var mark = '';
		if(item.itemGroupMark != null) mark = '<i>' + item.itemGroupMark + '</i>';
		builder += '<b>' + item.itemGroupName + '</b> ' + mark + ' (ilość: ' + item.numberOfItems + ')';
		if(item.numberOfIndividualItems > 0 && item.numberOfIndividualItems == item.individualItems.length){
			builder += '<br>W tym:';
			builder += '<ul>';
			for(var individual of item.individualItems){
				var individualmark = '(brak oznaczenia)';
				if(individual.mark != null) individualmark = individual.mark;
				var individualnote = '';
				if(individual.note != null) individualnote = ' - ' + individual.note;
				builder += '<li>' + individualmark + individualnote + '</li>';
			}
			builder += '</ul>';
		}
		builder += '</div>';
		/*builder += '<div>';
		builder += '<button id="delete_itemgroup' + item.itemGroupId + '_su' + meta.id + '" type="button" class="btn btn-danger">Usuń</button> ';
		builder += '<button id="move_itemgroup' + item.itemGroupId + '_su' + meta.id + '" type="button" class="btn btn-dark">Przenieś</button> ';
		builder += '</div>';*/
		builder += '</li>';
	}
	
	builder += '<li class="list-group-item d-flex justify-content-end align-items-center">';
	builder += '<div>';
	if(items.length == 0) builder += '<button id="delete_su' + meta.id + '" type="button" class="btn btn-danger">Usuń</button> ';
	builder += '<button id="newitem_su' + meta.id + '" type="button" class="btn btn-dark">Dodaj przedmiot</button> ';
	builder += '<button id="edit_su' + meta.id + '" type="button" class="btn btn-dark">Edytuj</button> ';
	builder += '<button id="show_su' + meta.id + '" type="button" class="btn btn-dark">Pokaż</button> ';
	builder += '</div>';
	builder += '</li>';
	
	builder += '</ul>';
	builder += '</div>';
	builder += '</div>';
	
	return builder;
}

function storageUnitBuilderRegisterEvents(meta, items){
	$("#newitem_su" + meta.id).click(function(){
		$("#new_item_modal").modal('show');
	});
	
	$("#delete_su" + meta.id).click(function(){
		$("#delete_storageunit_name_div").text(meta.name);
		$("#delete_storageunit_id_input").val(meta.id);
		
		$("#delete_storageunit_modal").modal('show');
	});
	
	$("#show_su" + meta.id).click(function(){
		window.open(baseUrl + "/storageunit.html?id=" + meta.id, '_blank').focus();
	});
	
	$("#edit_su" + meta.id).click(function(){
		$("#edit_storageunit_id_input").val(meta.id);
		$("#edit_storageunit_name_input").val(meta.name);
		$("#edit_storageunit_description_input").val(meta.description);
		$("#edit_storageunit_location_input").val(meta.location);
		
		$("#edit_storageunit_modal").modal('show');
	});
}

$("#delete_this_warehouse_button").click(function(){
	$("#delete_warehouse_modal").modal('show');
});

$("#delete_warehouse_button").click(function(){
	$("#delete_warehouse_button").attr('disabled', true);
	
	$.ajax({
		url: apiUrl + "/warehouse/delete/" + warehouseId,
		method: "DELETE",
		contentType: "application/json"
	}).done(function(data, textStatus){
		window.close();
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
});

$("#new_storageunit_button").click(function(){
	$("#new_storageunit_modal").modal('show');
});

$("#save_storageunit_button").click(function(){
	if($("#new_storageunit_name_input").val() == ""){
	    $("#new_storageunit_error_div").text("Błąd: nazwa nie może być pusta!");
	    $("#new_storageunit_error_div").show();
   	}else{
		$("#new_storageunit_error_div").hide();
		$("#save_storageunit_button").attr('disabled', true);
		
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
				url: apiUrl + "/storageunit/items/grouped/" + data.id,
				method: "GET",
				dataType: "json"
			}).done(function(items){
				$("#new_storageunit").before(storageUnitBuilder(data, items));
				storageUnitBuilderRegisterEvents(data, items);
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

$("#edit_warehouse_button").click(function(){
	$("#edit_warehouse_modal").modal('show');
});

$("#save_warehouse_button").click(function(){
	if($("#edit_warehouse_name_input").val() == ""){
	    $("#edit_warehouse_error_div").text("Błąd: nazwa nie może być pusta!");
	    $("#edit_warehouse_error_div").show();
   	}else{
		$("#edit_warehouse_error_div").hide();
		$("#save_existing_warehouse_button").attr('disabled', true);
		
		var name = $("#edit_warehouse_name_input").val();
		var description = $("#edit_warehouse_description_input").val();
		var location = $("#edit_warehouse_location_input").val();
		
		$.ajax({
			url: apiUrl + "/warehouse/edit/" + warehouseId,
			method: "PUT",
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify({
				name: name,
				description: description,
				location: location
			})
		}).done(function(data, textStatus){
			warehouseMetaReload(data);
			
			$("#save_existing_warehouse_button").attr('disabled', false);
			$("#edit_warehouse_modal").modal('hide');
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}
});




$("#delete_storageunit_button").click(function(){
	$("#delete_storageunit_button").attr('disabled', true);

	var id = $("#delete_storageunit_id_input").val();
	
	$.ajax({
		url: apiUrl + "/storageunit/delete/" + id,
		method: "DELETE",
		contentType: "application/json"
	}).done(function(data, textStatus){
		$("#su" + id).remove();
		
		$("#delete_storageunit_modal").modal('hide');
		$("#delete_storageunit_button").attr('disabled', false);
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
});

$("#save_existing_storageunit_button").click(function(){
	if($("#edit_storageunit_name_input").val() == ""){
	    $("#edit_storageunit_error_div").text("Błąd: nazwa nie może być pusta!");
	    $("#edit_storageunit_error_div").show();
   	}else{
		$("#edit_storageunit_error_div").hide();
		$("#save_existing_storageunit_button").attr('disabled', true);
		
		var id = $("#edit_storageunit_id_input").val();
		var name = $("#edit_storageunit_name_input").val();
		var description = $("#edit_storageunit_description_input").val();
		var location = $("#edit_storageunit_location_input").val();
		
		$.ajax({
			url: apiUrl + "/storageunit/edit/" + id,
			method: "PUT",
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify({
				name: name,
				description: description,
				location: location
			})
		}).done(function(data, textStatus){
			$.ajax({
				url: apiUrl + "/storageunit/items/grouped/" + data.id,
				method: "GET",
				dataType: "json"
			}).done(function(items){
				$("#su" + data.id).replaceWith(storageUnitBuilder(data, items));
				storageUnitBuilderRegisterEvents(data, items);
			}).fail(function(xhr, textStatus){
				alert("Błąd krytyczny - strona zostanie przeładowana");
				location.reload();
			});
			
			$("#edit_storageunit_modal").modal('hide');
			$("#save_existing_storageunit_button").attr('disabled', false);
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}
});