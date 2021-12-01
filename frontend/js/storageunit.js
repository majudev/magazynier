const urlParams = new URLSearchParams(window.location.search);
const storageUnitId = urlParams.get('id');

var storageUnitMapping = {};

$(document).ready(function(){
	$.ajax({
		url: apiUrl + "/storageunit/meta/" + storageUnitId,
		method: "GET",
		dataType: "json"
	}).done(function(data){
		$.ajax({
			url: apiUrl + "/storageunit/items/grouped/" + storageUnitId,
			method: "GET",
			dataType: "json"
		}).done(function(items){
			storageUnitMetaReload(data, items);
			
			for(var item of items){
				$("#new_item").before(itemBuilder(item));
				itemBuilderRegisterEvents(item);
			}
		}).fail(function(xhr, textStatus){
			alert("Błąd krytyczny - strona zostanie przeładowana");
			location.reload();
		});
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
});

function storageUnitMetaReload(storageUnit, items){
	$("#storageunit_name").html("Nazwa: <b>" + storageUnit.name + "</b>");
	if(storageUnit.description != null) $("#storageunit_description").text("Opis: " + storageUnit.description);
	if(storageUnit.location != null) $("#storageunit_location").text("Lokalizacja: " + storageUnit.location);

	$("#edit_storageunit_name_input").val(storageUnit.name);
	$("#edit_storageunit_description_input").val(storageUnit.description);
	$("#edit_storageunit_location_input").val(storageUnit.location);
	
	$("#delete_storageunit_name_div").text(storageUnit.name);
	
	if(items.length == 0) $("#delete_this_storageunit_button").show();
	else $("#delete_this_storageunit_button").hide();
}

function itemBuilder(item){
	var builder = '';
	builder += '<div id="i' + item.itemGroupId + '" class="col-lg-4 col-sm-12">';
	builder += '<ul class="list-group">';
	builder += '<li class="list-group-item list-group-item-info d-flex justify-content-center bg-dark text-center text-white">';
	builder += '<h4 class="mb-1 mt-1">' + item.itemGroupName + '</h4>';
	builder += '</li>';
	if(item.itemGroupDescription != null){
		builder += '<li class="list-group-item d-flex justify-content-around align-items-center">';
		builder += '<div>Opis: ' + item.itemGroupDescription + '</div>';
		builder += '</li>';
    }
	builder += '<li class="list-group-item d-flex align-items-center">';
	builder += '<div class="flex-fill"><b>Ilość:</b> ' + item.numberOfItems + '</div>';
	if(item.numberOfIndividualItems == 0){
	   builder += '<div><button id="move_itemgroup' + item.itemGroupId + '_counted" type="button" class="btn btn-sm btn-dark">Przenieś</button></div>';
	}
	builder += '</li>';
	if(item.numberOfIndividualItems > 0){
		builder += '<li class="list-group-item">';
		builder += '<b>W tym:</b>';
		builder += '<ul>';
		for(var individualItem of item.individualItems){
			var note = '';
			if(individualItem.notes != null) note = ' - <i>' + individualItem.notes + '</i>';
			builder += '<li class="mb-1"><div class="d-flex align-items-center"><div class="flex-fill">' + individualItem.mark + note + '</div><button id="move_itemgroup' + item.itemGroupId + '_i' + individualItem.id + '" type="button" class="btn btn-sm btn-dark">Przenieś</button></div></li>';
		}
		builder += '<li><div class="d-flex align-items-center"><div class="flex-fill"><i>' + item.numberOfCountedItems + '</i> luzem</div>';
		if(item.numberOfCountedItems > 0){
			builder += '<button id="move_itemgroup' + item.itemGroupId + '_counted" type="button" class="btn btn-sm btn-dark">Przenieś</button>';
		}
		builder += '</div></li>';
		builder += '</ul>';
		builder += '</li>';
	}
	builder += '<li class="list-group-item d-flex justify-content-end align-items-center">';
	builder += '<div>';
	//builder += '<button id="editi_' + item.itemGroupId + '" type="button" class="btn btn-dark">Edytuj</button> ';
	builder += '<button id="show_i' + item.itemGroupId + '" type="button" class="btn btn-sm btn-dark">Pokaż w managerze przedmiotów</button> ';
	builder += '</div>';
	builder += '</li>';
	builder += '</ul>';
	builder += '</div>';
	
	return builder;
}

function itemBuilderRegisterEvents(item){
	$("#show_i" + item.itemGroupId).click(function(){
		window.open(baseUrl + "/itemgroup.html?id=" + item.itemGroupId, '_blank').focus();
	});
	
	$("#move_itemgroup" + item.itemGroupId + "_counted").click(function(){
		refreshStorageUnits();
		$("#move_item_id_input").val(item.itemGroupId);
		$("#move_item_groupname_div").text(item.itemGroupName);
		$("#move_item_count_select").empty();
		for(var i = 1; i <= item.numberOfCountedItems; ++i){
			$("#move_item_count_select").append('<option value="' + i + '">' + i + '</option>');
		}
		$("#move_item_count_div").show();
		$("#move_item_modal").modal('show');
	});
	
	for(var individualItem of item.individualItems){
		$("#move_itemgroup" + item.itemGroupId + "_i" + individualItem.id).attr("i_id", individualItem.id);
		$("#move_itemgroup" + item.itemGroupId + "_i" + individualItem.id).attr("i_mark", individualItem.mark);
		$("#move_itemgroup" + item.itemGroupId + "_i" + individualItem.id).attr("ig_name", item.itemGroupName);
		$("#move_itemgroup" + item.itemGroupId + "_i" + individualItem.id).click(function(){
			refreshStorageUnits();
			$("#move_item_id_input").val($(this).attr("i_id"));
			$("#move_item_groupname_div").text($(this).attr("i_mark") + ' (' + $(this).attr("ig_name") + ')');
			$("#move_item_count_div").hide();
			$("#move_item_modal").modal('show');
		});
	}
}

function refreshStorageUnits(){
	$.ajax({
		url: apiUrl + "/warehouse/get",
		method: "GET",
		dataType: "json"
	}).done(function(data){
		storageUnitMapping = {};
		$("#move_item_warehouse_select").empty();
		
		for(var warehouse of data){
			storageUnitMapping[warehouse.id] = warehouse.storageUnits;
			
			$("#move_item_warehouse_select").append('<option value="' + warehouse.id + '">' + warehouse.name + '</option>');
		}
		
		$("#move_item_warehouse_select").change();
	}).fail(function(xhr, textStatus){
		alert("Błąd krytyczny - strona zostanie przeładowana");
		location.reload();
	});
}

$("#move_item_warehouse_select").on('change', function(e){
	$("#move_item_storageunit_select").empty();
	var storageUnits = storageUnitMapping[$("#move_item_warehouse_select").val()];
	for(var storageUnit of storageUnits){
		$("#move_item_storageunit_select").append('<option value="' + storageUnit.id + '">' + storageUnit.name + '</option>');
	}
});

$("#delete_this_storageunit_button").click(function(){
	$("#delete_storageunit_modal").modal('show');
});

$("#new_item_button").click(function(){
	$("#new_item_modal").modal('show');
});

$("#edit_storageunit_button").click(function(){
	$("#edit_storageunit_modal").modal('show');
});

$("#delete_storageunit_button").click(function(){
	$("#delete_storageunit_button").attr('disabled', true);
	
	$.ajax({
		url: apiUrl + "/storageunit/delete/" + storageUnitId,
		method: "DELETE",
		contentType: "application/json"
	}).done(function(data, textStatus){
		window.close();
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
		
		var name = $("#edit_storageunit_name_input").val();
		var description = $("#edit_storageunit_description_input").val();
		var location = $("#edit_storageunit_location_input").val();
		
		$.ajax({
			url: apiUrl + "/storageunit/edit/" + storageUnitId,
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
				storageUnitMetaReload(data, items);
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

$("#move_item_button").click(function(){
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