var storageunit_queue = [];

var storageUnitMapping = {};

$(document).ready(function(){
	$.ajax({
		url: apiUrl + "/itemgroup/get/grouped",
		method: "GET",
		dataType: "json"
	}).done(function(data){
		for(var itemgroup of data){
			$("#new_itemgroup").before(itemGroupBuilder(itemgroup));
			itemGroupBuilderRegisterEvents(itemgroup);
			
			//$("#move_storageunit_new_warehouse_select").append('<option id="move_storageunit_new_warehouse_select_warehouse' + warehouse.id + '" class="move-su-entry" value="' + warehouse.id + '">' + warehouse.name + '</option>');
		}
		
		propagateStorageUnitNames();
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
});

function itemGroupBuilder(itemgroup){
	var builder = '';
	//object open
	builder += '<div id="itemgroup_' + itemgroup.itemGroupId + '" class="col-lg-4 col-sm-12">';
	builder += '<div class="p-2">';
	builder += '<ul class="list-group">';
	
	//header
	builder += '<li class="list-group-item list-group-item-info d-flex justify-content-center bg-dark text-center text-white">'
	builder += '<h4 class="mb-1 mt-1">' + itemgroup.itemGroupName + '</h4>';
	builder += '</li>';
	
	//mark
	if(itemgroup.itemGroupMark != null){
		builder += '<li id="descfield_itemgroup_' + itemgroup.itemGroupId + '" class="list-group-item d-flex justify-content-around align-items-center align-text-center p-3">';
		builder += '<div id="mark_itemgroup_' + itemgroup.itemGroupId + '" style="display: inline">Oznaczenie: ' + itemgroup.itemGroupMark + '</div>';
		builder += '</li>';
	}
	
	builder += '<li class="list-group-item d-flex justify-content-around align-items-center align-text-center p-3">';
	builder += '<div style="display: inline">Ilość: ' + itemgroup.numberOfItems + '</div>';
	builder += '</li>';
	
	if(itemgroup.numberOfIndividualItems > 0){
		builder += '<li class="list-group-item d-flex flex-column justify-content-around align-items-center align-text-center">';
		/*builder += '<ul>';
		for(var individualItem of itemgroup.individualItems){
			builder += '<li class="mb-1"><div class="d-flex align-items-center"><div class="flex-fill">' + individualItem.mark + '</div><button id="move_itemgroup' + itemgroup.itemGroupId + '_i' + individualItem.id + '" type="button" class="btn btn-dark">Przenieś</button></div></li>';
		}
		builder += '<li><div class="d-flex align-items-center"><div class="flex-fill"><i>' + itemgroup.numberOfCountedItems + '</i> luzem</div>';
		if(itemgroup.numberOfCountedItems > 0){
			builder += '<button id="move_itemgroup' + itemgroup.itemGroupId + '_counted" type="button" class="btn btn-dark">Przenieś</button>';
		}
		builder += '</div></li>';
		builder += '</ul>';*/
		builder += '<div style="display: inline"><b>Z oznaczeniami</b></div>';
		builder += '<table class="table">';
		builder += '<thead>';
		builder += '<td>Oznaczenie</td><td>Notatka</td><td>Jednostka</td><td></td>';
		builder += '</thead>';
		builder += '<tbody>';
		for(var individualItem of itemgroup.individualItems){
			//builder += '<li class="mb-1"><div class="d-flex align-items-center"><div class="flex-fill">' + individualItem.mark + '</div><button id="move_itemgroup' + itemgroup.itemGroupId + '_i' + individualItem.id + '" type="button" class="btn btn-dark">Przenieś</button></div></li>';
			var note = '<i>brak</i>';
			if(individualItem.notes != null) note = individualItem.notes;
			builder += '<tr><td>' + individualItem.mark + '</td><td>' + note + '</td><td class="su' + individualItem.storageUnitId + '_name">...</td><td style="width: 1px; white-space: nowrap">';
			builder += '<button id="delete_item' + individualItem.id + '" type="button" class="btn btn-sm btn-danger">Usuń</button> ';
			builder += '<button id="edit_item' + individualItem.id + '" type="button" class="btn btn-sm btn-dark">Edytuj</button> ';
			builder += '<button id="move_item' + individualItem.id + '" type="button" class="btn btn-sm btn-dark">Przenieś</button> ';
			builder += '</td></tr>';
		}
		/*builder += '<li><div class="d-flex align-items-center"><div class="flex-fill"><i>' + itemgroup.numberOfCountedItems + '</i> luzem</div>';
		if(itemgroup.numberOfCountedItems > 0){
			builder += '<button id="move_itemgroup' + itemgroup.itemGroupId + '_counted" type="button" class="btn btn-dark">Przenieś</button>';
		}*/
		builder += '</tbody>';
		builder += '</table>';
		builder += '</li>';
	}
	
	if(itemgroup.numberOfCountedItems > 0){
		builder += '<li class="list-group-item d-flex flex-column justify-content-around align-items-center align-text-center">';
		builder += '<div style="display: inline"><b>Luzem</b></div>';
		builder += '<table class="table">';
		builder += '<thead>';
		builder += '<td>Ilość</td><td>Paleta</td><td></td>';
		builder += '</thead>';
		builder += '<tbody>';
		
		//builder += '<td>' + itemgroup.numberOfCountedItems + '</td><td id="item' + individualItem.id + '_su_name">...</td>';
		for(var storageUnit in itemgroup.countedItemsPerStorageUnit){
			builder += '<tr><td>' + itemgroup.countedItemsPerStorageUnit[storageUnit] + '</td><td class="su' + storageUnit + '_name">...</td><td style="width: 1px; white-space: nowrap">';
			builder += '<button id="delete_item_counted_itemgroup' + itemgroup.itemGroupId + '_su' + storageUnit + '" type="button" class="btn btn-sm btn-danger">Usuń</button> ';
			builder += '<button id="move_item_counted_itemgroup' + itemgroup.itemGroupId + '_su' + storageUnit + '" type="button" class="btn btn-sm btn-dark">Przenieś</button> ';
			builder += '</td></tr>';
		}
		
		/*builder += '<li><div class="d-flex align-items-center"><div class="flex-fill"><i>' + itemgroup.numberOfCountedItems + '</i> luzem</div>';
		if(itemgroup.numberOfCountedItems > 0){
			builder += '<button id="move_itemgroup' + itemgroup.itemGroupId + '_counted" type="button" class="btn btn-dark">Przenieś</button>';
		}*/
		builder += '</tbody>';
		builder += '</table>';
		builder += '</li>';
	}
	
	/*for(var storageUnit of warehouse.storageUnits){
		builder += '<li class="list-group-item d-flex justify-content-between align-items-center">';
		builder += '<div>';
		builder += '<b>' + storageUnit.name + '</b> ';
		if(storageUnit.description != null) builder += '- ' + storageUnit.description;
		builder += '</div>';
		builder += '<div>';
		builder += '<button id="move_storageunit_' + storageUnit.id + '_warehouse_' + warehouse.id + '" type="button" class="btn btn-dark btn-sm">Przenieś</button> ';
		builder += '<button id="show_storageunit_' + storageUnit.id + '_warehouse_' + warehouse.id + '" type="button" class="btn btn-dark btn-sm">Pokaż</button> ';
		builder += '</div>';
		builder += '</li>';
	}*/
	
	//edit & add new
	builder += '<li class="list-group-item d-flex justify-content-between align-items-center">';
    builder += '<div></div>';
    builder += '<div>';
    if(itemgroup.numberOfItems == 0) builder += '<button id="delete_itemgroup_' + itemgroup.itemGroupId + '" type="button" class="btn btn-danger btn-sm">Usuń grupę</button> ';
    builder += '<button id="edit_itemgroup_' + itemgroup.itemGroupId + '" type="button" class="btn btn-dark btn-sm">Edytuj grupę</button> ';
    builder += '<button id="new_item_itemgroup_' + itemgroup.itemGroupId + '" type="button" class="btn btn-dark btn-sm">Nowy przedmiot</button> ';
    builder += '</div>';
    builder += '</li>';
	
	//object close
	builder += '</ul>';
	builder += '</div>';
	builder += '</div>';
	
	return builder;
}

function itemGroupBuilderRegisterEvents(itemgroup){
	//register events
	$("#edit_itemgroup_" + itemgroup.itemGroupId).click(function(){
		$("#edit_itemgroup_id_input").val(itemgroup.itemGroupId);
		$("#edit_itemgroup_name_input").val(itemgroup.itemGroupName);
		$("#edit_itemgroup_mark_input").val(itemgroup.itemGroupMark);
		$("#edit_itemgroup_modal").modal('show');
	});
	$("#delete_itemgroup_" + itemgroup.itemGroupId).click(function(){
		$("#delete_itemgroup_name_div").text(itemgroup.itemGroupName);
		$("#delete_itemgroup_id_input").val(itemgroup.itemGroupId);
		$("#delete_itemgroup_modal").modal('show');
	});
	
	$("#new_item_itemgroup_" + itemgroup.itemGroupId).click(function(){
		refreshStorageUnits();
		$("#new_item_itemgroup_id_input").val(itemgroup.itemGroupId);
		$("#new_item_itemgroup_name_div").text(itemgroup.itemGroupName);
		$("#new_item_itemgroup_mark_div").text(itemgroup.itemGroupMark);
		$("#new_item_type_select").change();
		$("#new_item_modal").modal('show');
	});
	
	for(var individualItem of itemgroup.individualItems){
		if(!storageunit_queue.includes(individualItem.storageUnitId)){
			storageunit_queue.push(individualItem.storageUnitId);
		}
		
		$("#edit_item" + individualItem.id).attr("ig_name", itemgroup.itemGroupName);
		$("#edit_item" + individualItem.id).attr("ig_mark", itemgroup.itemGroupMark);
		$("#edit_item" + individualItem.id).attr("i_id", individualItem.id);
		$("#edit_item" + individualItem.id).attr("i_mark", individualItem.mark);
		$("#edit_item" + individualItem.id).attr("i_notes", individualItem.notes);
		$("#edit_item" + individualItem.id).click(function(){
			$("#edit_item_itemgroup_name_div").text($(this).attr("ig_name"));
			$("#edit_item_itemgroup_mark_div").text($(this).attr("ig_mark"));
			$("#edit_item_id_input").val($(this).attr("i_id"));
			$("#edit_item_mark_input").val($(this).attr("i_mark"));
			$("#edit_item_notes_input").val($(this).attr("i_notes"));
			$("#edit_item_modal").modal('show');
		});
		
		$("#delete_item" + individualItem.id).attr("i_id", individualItem.id);
		$("#delete_item" + individualItem.id).attr("i_mark", individualItem.mark);
		$("#delete_item" + individualItem.id).attr("ig_id", itemgroup.itemGroupId);
		$("#delete_item" + individualItem.id).click(function(){
			$("#delete_item_id_input").val($(this).attr("i_id"));
			$("#delete_item_itemgroup_id_input").val($(this).attr("ig_id"));
			$("#delete_item_name_div").text($(this).attr("i_mark"));
			$("#delete_item_count_div").hide();
			$("#delete_item_modal").modal('show');
		});
		
		$("#move_item" + individualItem.id).attr("i_id", individualItem.id);
		$("#move_item" + individualItem.id).attr("i_mark", individualItem.mark);
		$("#move_item" + individualItem.id).click(function(){
			refreshStorageUnits();
			$("#move_item_storageunit_id_input").val('');
			$("#move_item_id_input").val($(this).attr("i_id"));
			$("#move_item_name_div").text($(this).attr("i_mark"));
			$("#move_item_count_div").hide();
			$("#move_item_modal").modal('show');
		});
	}
	
	for(var storageUnit in itemgroup.countedItemsPerStorageUnit){
		if(!storageunit_queue.includes(storageUnit)){
			storageunit_queue.push(storageUnit);
		}
		
		$("#delete_item_counted_itemgroup" + itemgroup.itemGroupId + "_su" + storageUnit).attr("ig_id", itemgroup.itemGroupId);
		$("#delete_item_counted_itemgroup" + itemgroup.itemGroupId + "_su" + storageUnit).attr("ig_name", itemgroup.itemGroupName);
		$("#delete_item_counted_itemgroup" + itemgroup.itemGroupId + "_su" + storageUnit).attr("su_id", storageUnit);
		$("#delete_item_counted_itemgroup" + itemgroup.itemGroupId + "_su" + storageUnit).click(function(){
			$("#delete_item_name_div").html($(this).attr("ig_name") + ' z <i class="su' + $(this).attr("su_id") + '_name">...</i>');
			storageunit_queue.push($(this).attr("su_id"));
			propagateStorageUnitNames();
			$("#delete_item_itemgroup_id_input").val($(this).attr("ig_id"));
			$("#delete_item_storageunit_id_input").val($(this).attr("su_id"));
			$("#delete_item_count_div").show();
			$("#delete_item_modal").modal('show');
		});
		
		$("#move_item_counted_itemgroup" + itemgroup.itemGroupId + "_su" + storageUnit).attr("ig_id", itemgroup.itemGroupId);
		$("#move_item_counted_itemgroup" + itemgroup.itemGroupId + "_su" + storageUnit).attr("ig_name", itemgroup.itemGroupName);
		$("#move_item_counted_itemgroup" + itemgroup.itemGroupId + "_su" + storageUnit).attr("i_count", itemgroup.countedItemsPerStorageUnit[storageUnit]);
		$("#move_item_counted_itemgroup" + itemgroup.itemGroupId + "_su" + storageUnit).attr("su_id", storageUnit);
		$("#move_item_counted_itemgroup" + itemgroup.itemGroupId + "_su" + storageUnit).click(function(){
			refreshStorageUnits();
			$("#move_item_name_div").html($(this).attr("ig_name") + ' z <i class="su' + $(this).attr("su_id") + '_name">...</i>');
			storageunit_queue.push($(this).attr("su_id"));
			propagateStorageUnitNames();
			$("#move_item_id_input").val($(this).attr("ig_id"));
			$("#move_item_storageunit_id_input").val($(this).attr("su_id"));
			for(var i = 1; i <= $(this).attr("i_count"); ++i){
				$("#move_item_count_select").append('<option value="' + i + '">' + i + '</option>');
			}
			$("#move_item_count_div").show();
			$("#move_item_modal").modal('show');
		});
	}
}

function propagateStorageUnitNames(){
	for(var storageUnit of storageunit_queue){
		$.ajax({
			url: apiUrl + "/storageunit/meta/" + storageUnit,
			method: "GET",
			dataType: "json"
		}).done(function(data){
			$('.su' + data.id + '_name').text(data.name);
		});
	}
	storageunit_queue = [];
}

function refreshStorageUnits(){
	$.ajax({
		url: apiUrl + "/warehouse/get",
		method: "GET",
		dataType: "json"
	}).done(function(data){
		storageUnitMapping = {};
		$(".warehouse_select").empty();
		
		for(var warehouse of data){
			storageUnitMapping[warehouse.id] = warehouse.storageUnits;
			
			$(".warehouse_select").append('<option value="' + warehouse.id + '">' + warehouse.name + '</option>');
		}
		
		$(".warehouse_select").change();
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
}

$(".warehouse_select").on('change', function(e){
	$(".storageunit_select").empty();
	var storageUnits = storageUnitMapping[$(this).val()];
	for(var storageUnit of storageUnits){
		$(".storageunit_select").append('<option value="' + storageUnit.id + '">' + storageUnit.name + '</option>');
	}
});

$("#new_item_type_select").on('change', function(){
	if($("#new_item_type_select").val() == 'marked'){
		$("#new_item_marked_div").show();
		$("#new_item_counted_div").hide();
	}else{
		$("#new_item_marked_div").hide();
		$("#new_item_counted_div").show();
	}
});

$("#new_itemgroup_button").click(function(){
	$("#new_itemgroup_modal").modal('show');
});

$("#move_item_button").click(function(){
	var storageUnitId = $("#move_item_storageunit_id_input").val();
	if($("#move_item_storageunit_select").val() == storageUnitId){
	    $("#move_item_error_div").text("Błąd: przedmiot znajduje się już na tej jednostce!");
	    $("#move_item_error_div").show();
   	}else if($("#move_item_count_div").is(":visible") && $("#move_item_count_select").val() == null){
	    $("#move_item_error_div").text("Błąd: wybierz ile przedmiotów chcesz przenieść!");
	    $("#move_item_error_div").show();
   	}else{
		$("#move_item_error_div").hide();
		$("#move_item_button").attr('disabled', true);
		
		var newStorageUnit = $("#move_item_storageunit_select").val();
		var itemId = $("#move_item_id_input").val();
		var count = 0;
		if($("#move_item_count_div").is(":visible")){
		   count = $("#move_item_count_select").val();
	    }
		
		var url = apiUrl + "/item";
		if(count > 0){
			url += "/move/" + count + "/unmarked/" + itemId + "/from/" + storageUnitId + "/to/" + newStorageUnit;
		}else{
			url += "/move/" + itemId + "/to/" + newStorageUnit;
		}
		$.ajax({
			url: url,
			method: "PUT",
			contentType: "application/json"
		}).done(function(data, textStatus){
			window.location.reload();
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}
});

/*$("#save_warehouse_button").click(function(){
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
});*/

$("#save_existing_item_button").click(function(){
	if($("#edit_item_name_input").val() == ""){
	    $("#edit_item_error_div").text("Błąd: nazwa nie może być pusta!");
	    $("#edit_item_error_div").show();
   	}else{
		$("#edit_item_error_div").hide();
		$("#save_existing_item_button").attr('disabled', true);
		
		var id = $("#edit_item_id_input").val();
		var mark = $("#edit_item_mark_input").val();
		var notes = $("#edit_item_notes_input").val();
		
		$.ajax({
			url: apiUrl + "/item/edit/" + id,
			method: "PUT",
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify({
				mark: mark,
				notes: notes
			})
		}).done(function(data, textStatus){
			refreshItemGroup(data.itemGroupId);
			
			$("#save_existing_item_button").attr('disabled', false);
			$("#edit_item_modal").modal('hide');
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}
});

$("#save_item_button").click(function(){
	if($("#new_item_type_select").val() == "counted" && $("#new_item_count_input").val() <= 0){
	    $("#new_item_error_div").text("Błąd: musisz dodać przynajmniej jeden przedmiot");
	    $("#new_item_error_div").show();
   	}else if($("#new_item_type_select").val() == "marked" && $("#new_item_mark_input").val() == ""){
	    $("#new_item_error_div").text("Błąd: musisz wpisać oznaczenie przedmiotu!");
	    $("#new_item_error_div").show();
   	}else{
		$("#new_item_error_div").hide();
		$("#new_item_button").attr('disabled', true);
		
		var itemGroup = $("#new_item_itemgroup_id_input").val();
		var storageUnit = $("#new_item_storageunit_select").val();
		var mark = $("#new_item_mark_input").val();
		var notes = $("#new_item_notes_input").val();
		var count = 0;
		if($("#new_item_type_select").val() == "counted"){
		   count = $("#new_item_count_input").val();
	    }
		
		var data = undefined;
		var url = apiUrl + "/item";
		if(count > 0){
			url += "/new/" + itemGroup + "/on/" + storageUnit + "/unmarked/" + count;
		}else{
			url += "/new/" + itemGroup + "/on/" + storageUnit;
			data = JSON.stringify({
				mark: mark,
				notes: notes
			});
		}
		$.ajax({
			url: url,
			method: "POST",
			contentType: "application/json",
			data: data
		}).done(function(data, textStatus){
			refreshItemGroup(itemGroup);
			
			$("#new_item_button").attr('disabled', false);
			$("#new_item_modal").modal('hide');
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}
});

$("#delete_item_button").click(function(){
	var url = apiUrl + "/item";
	
	var itemGroup = $("#delete_item_itemgroup_id_input").val();
	
	if($("#delete_item_count_div").is(":visible")){
		var storageUnit = $("#delete_item_storageunit_id_input").val();
		var count = $("#delete_item_count_input").val();
		
		url += "/delete/" + itemGroup + "/unmarked/" + count + "/from/" + storageUnit;
	}else{
		var id = $("#delete_item_id_input").val();
		url += "/delete/" + id;
	}
	
	$.ajax({
		url: url,
		method: "DELETE",
		contentType: "application/json"
	}).done(function(data, textStatus){
		refreshItemGroup(itemGroup);

		$("#delete_item_button").attr('disabled', false);
		$("#delete_item_modal").modal('hide');
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
});

$("#save_itemgroup_button").click(function(){
	if($("#new_itemgroup_name_input").val() == ""){
	    $("#new_itemgroup_error_div").text("Błąd: nazwa nie może być pusta!");
	    $("#new_itemgroup_error_div").show();
   	}else if($("#new_itemgroup_mark_input").val() == ""){
	    $("#new_itemgroup_error_div").text("Błąd: oznaczenie nie może być puste!");
	    $("#new_itemgroup_error_div").show();
   	}else{
		$("#new_itemgroup_error_div").hide();
		$("#save_itemgroup_button").attr('disabled', true);
		
		var name = $("#new_itemgroup_name_input").val();
		var mark = $("#new_itemgroup_mark_input").val();
		
		$.ajax({
			url: apiUrl + "/itemgroup/new",
			method: "POST",
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify({
				name: name,
				mark: mark
			})
		}).done(function(data, textStatus){
			$.ajax({
				url: apiUrl + "/itemgroup/get/" + data.id + "/grouped",
				method: "GET",
				contentType: "application/json"
			}).done(function(itemgroup, textStatus){
				$("#new_itemgroup").before(itemGroupBuilder(itemgroup));
				itemGroupBuilderRegisterEvents(itemgroup);
			}).fail(function(xhr, textStatus){
				alert("Błąd krytyczny - strona zostanie przeładowana");
				location.reload();
			});
			
			$("#save_itemgroup_button").attr('disabled', false);
			$("#new_itemgroup_modal").modal('hide');
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}
});

$("#save_existing_itemgroup_button").click(function(){
	if($("#edit_itemgroup_name_input").val() == ""){
	    $("#edit_itemgroup_error_div").text("Błąd: nazwa nie może być pusta!");
	    $("#edit_itemgroup_error_div").show();
   	}else if($("#edit_itemgroup_mark_input").val() == ""){
	    $("#edit_itemgroup_error_div").text("Błąd: oznaczenie nie może być puste!");
	    $("#edit_itemgroup_error_div").show();
   	}else{
		$("#edit_itemgroup_error_div").hide();
		$("#save_existing_itemgroup_button").attr('disabled', true);
		
		var id = $("#edit_itemgroup_id_input").val();
		var name = $("#edit_itemgroup_name_input").val();
		var mark = $("#edit_itemgroup_mark_input").val();
		
		$.ajax({
			url: apiUrl + "/itemgroup/edit/" + id,
			method: "PUT",
			contentType: "application/json",
			dataType: "json",
			data: JSON.stringify({
				name: name,
				mark: mark
			})
		}).done(function(data, textStatus){
			refreshItemGroup(data.id);
			
			$("#save_existing_itemgroup_button").attr('disabled', false);
			$("#edit_itemgroup_modal").modal('hide');
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}
});

$("#delete_itemgroup_button").click(function(){
	var id = $("#delete_itemgroup_id_input").val();
	
	$.ajax({
		url: apiUrl + "/itemgroup/delete/" + id,
		method: "DELETE",
		contentType: "application/json"
	}).done(function(data, textStatus){
		$("#itemgroup_" + id).remove();
		$("#delete_itemgroup_modal").modal('hide');
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
});

/*$("#move_storageunit_button").click(function(){
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
});*/

function refreshItemGroup(itemGroupId){
	$.ajax({
		url: apiUrl + "/itemgroup/get/" + itemGroupId + "/grouped",
		method: "GET",
		contentType: "application/json"
	}).done(function(itemgroup, textStatus){
		$("#itemgroup_" + itemGroupId).replaceWith(itemGroupBuilder(itemgroup));
		itemGroupBuilderRegisterEvents(itemgroup);

		propagateStorageUnitNames();
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
}