var buttonLoad4State;
var dropdownCountry4State;
var dropdownState;
var buttonAdd4State;
var buttonUpdate4State;
var buttonDelete4State;
var labelStateName;
var fieldStateName;

$(document).ready(function() {
	buttonLoad4State = $("#buttonLoadCountriesForState");
	dropdownCountry4State = $("#dropdownCountriesForState");
	dropdownState = $("#dropdownState")
	buttonAdd4State = $("#buttonAddState");
	buttonUpdate4State = $("#buttonUpdateState");
	buttonDelete4State = $("#buttonDeleteState");
	labelStateName = $("#labelStateName");
	fieldStateName = $("#fieldStateName");
	
	buttonLoad4State.click(function() {
		loadCountriesForState();
	});
	
	dropdownState.on("change", function() {
		changeFormStateToSelectedState();
	});
	
	dropdownCountry4State.on("change", function() {
		loadState4Country();
	});
	
	buttonAdd4State.click(function() {
		if (buttonAdd4State.val() == "Add") {
			addState();
		} else {
			changeFormStateToNew();
		}
		
	});
	
	buttonUpdate4State.click(function() {
		updateState();
	})
	
	buttonDelete4State.click(function() {
		deleteState();
	})
});

function deleteState() {
	stateId = dropdownState.val();
	
	url = contextPath + "states/delete/" + stateId;
	$.ajax({
		type: 'DELETE',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}	
	}).done(function() {
		$("#dropdownState option[value='" + stateId + "']").remove();
		changeFormStateToNew();
		showToastMessage("The State has been deleted");
	}).fail(function() {
		showToastMessage("Error: could not connect to server or server encountered an error");
	});
	
}

function updateState() {
	if (!validateFormState()) return;
	
	url = contextPath + "states/save";
	stateId = dropdownState.val();
	stateName = fieldStateName.val();

	selectCountry = $("#dropdownCountriesForState option:selected")
	countryId = selectCountry.val();
	countryName = selectCountry.text();

	jsonData = {id: stateId, name: stateName, country: {id: countryId, name: countryName}};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'	
	}).done(function(stateId) {
		selectNewAddedState(stateId, stateName);
		showToastMessage("Updated State Successfull");
	}).fail(function() {
		showToastMessage("Error: could not connect to server or server encountered an error");
	});
}

function validateFormState() {
	formState = document.getElementById("formState");
	if (!formState.checkValidity()) {
		formState.reportValidity();
		return false;
	}
	return true;
}

function addState() {
	if (!validateFormState()) return;
	
	url = contextPath + "states/save";
	stateName = fieldStateName.val();

	selectCountry = $("#dropdownCountriesForState option:selected")
	countryId = selectCountry.val();
	countryName = selectCountry.text();

	jsonData = {name: stateName, country: {id: countryId, name: countryName}};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'	
	}).done(function(stateId) {
		selectNewAddedState(stateId, stateName);
		showToastMessage("Added Successfull new state");
	}).fail(function() {
		showToastMessage("Error: could not connect to server or server encountered an error");
	});
}

function selectNewAddedState(stateId, stateName) {
	$("<option>").val(stateId).text(stateName).appendTo(dropdownState);
	$("#dropdownState option[value='" + stateId + "']").prop("selected", true);
	fieldStateName.val("").focus();
}

function changeFormStateToSelectedState() {
	buttonAdd4State.prop("value", "New");
	buttonUpdate4State.prop("disabled", false);
	buttonDelete4State.prop("disabled", false);
	
	labelStateName.text("Selected State/Provinces:");
	
	selectedStateName = $("#dropdownState option:selected").text();
	fieldStateName.val(selectedStateName);
}

function changeFormStateToNew() {
	buttonAdd4State.val("Add");
	buttonUpdate4State.prop("disabled", true);
	buttonDelete4State.prop("disabled", true);
	fieldStateName.val("").focus();;
}

function loadState4Country() {
	selectedCountry = $("#dropdownCountriesForState option:selected");
	countryId = selectedCountry.val();
	url = contextPath + "states/list_by_country/" + countryId;
	$.get(url, function(responseJSON) {
		dropdownState.empty();
		$.each(responseJSON, function(index, state) {
			$("<option>").val(state.id).text(state.name).appendTo(dropdownState);
		});
	}).done(function() {
		changeFormStateToNew();
		showToastMessage("All states have been loaded for country " + selectedCountry.text());
	}).fail(function() {
		showToastMessage("Error: could not connect to server or server encountered an error");
	});
}

function loadCountriesForState() {
	url = contextPath + "countries/list";
	$.get(url, function(responseJSON) {
		dropdownCountry4State.empty();
		$.each(responseJSON, function(index, country) {
			$("<option>").val(country.id).text(country.name).appendTo(dropdownCountry4State);
		});
	}).done(function() {
		buttonLoad.val("Refresh Country List");
		showToastMessage("All countries have been loaded");
	}).fail(function() {
		showToastMessage("Error: could not connect to server or server encountered an error");
	});
}

function showToastMessage(message) {
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}