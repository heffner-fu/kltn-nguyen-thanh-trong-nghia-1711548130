var buttonLoad;
var dropdownCountry;
var buttonAdd;
var buttonUpdate;
var buttonDelete;
var labelCountryName;
var fieldCountryName;
var fieldCountryCode;

$(document).ready(function() {
	buttonLoad = $("#buttonLoadCountries");
	dropdownCountry = $("#dropdownCountries");
	buttonAdd = $("#buttonAddCountry");
	buttonUpdate = $("#buttonUpdateCountry");
	buttonDelete = $("#buttonDeleteCountry");
	labelCountryName = $("#labelCountryName");
	fieldCountryName = $("#fieldCountryName");
	fieldCountryCode = $("#fieldCountryCode");
	
	buttonLoad.click(function() {
		loadCountries();
	});
	
	dropdownCountry.on("change", function() {
		changeFormStateToSelectedCountry();
	});
	
	buttonAdd.click(function() {
		if (buttonAdd.val() == "Add") {
			addCountry();
		} else {
			changeFormStateToNewCountry();
		}
		
	});
	
	buttonUpdate.click(function() {
		updateCountry();
	})
	
	buttonDelete.click(function() {
		deleteCountry();
	})
});

function deleteCountry() {
	optionValue = dropdownCountry.val();
	countryId = optionValue.split("-")[0];
	url = contextPath + "countries/delete/" + countryId;
	
	$.ajax({
		type: 'DELETE',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function() {
		$("#dropdownCountries option[value='" + optionValue + "']").remove();
		changeFormStateToNewCountry();
		showToastMessage("The Country has been deleted");
	}).fail(function() {
		showToastMessage("Error: could not connect to server or server encountered an error");
	});
	
}

function updateCountry() {
	if (!validateFormCountry()) return;
	
	url = contextPath + "countries/save";
	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();
	countryId = dropdownCountry.val().split("-")[0];
	
	jsonData = {id: countryId,name: countryName, code: countryCode};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'	
	}).done(function(countryId) {
		$("#dropdownCountries option:selected").val(countryId + "-" + countryCode);
		$("#dropdownCountries option:selected").text(countryName);
		showToastMessage("Updated Successfull Country with Id: " + countryId);
		changeFormStateToNewCountry();
	}).fail(function() {
		showToastMessage("Error: could not connect to server or server encountered an error");
	});;
}

function validateFormCountry() {
	formCountry = document.getElementById("formCountry");
	if (!formCountry.checkValidity()) {
		formCountry.reportValidity();
		return false;
	}
	return true;
}

function addCountry() {
	if (!validateFormCountry()) return;
	
	url = contextPath + "countries/save";
	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();
	jsonData = {name: countryName, code: countryCode};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'	
	}).done(function(countryId) {
		selectNewAddedCountry(countryId, countryName, countryCode);
		showToastMessage("Added Successfull New Country with Id: " + countryId);
	}).fail(function() {
		showToastMessage("Error: could not connect to server or server encountered an error");
	});
}

function selectNewAddedCountry(countryId, countryName, countryCode) {
	optionValue = countryId + "-" + countryCode;
	$("<option>").val(optionValue).text(countryName).appendTo(dropdownCountry);
	$("#dropdownCountries option[value='" + optionValue + "']").prop("selected", true);
	fieldCountryName.val("").focus();
	fieldCountryCode.val("");
}

function changeFormStateToNewCountry() {
	buttonAdd.val("Add");
	labelCountryName.text("Country Name:");
	
	buttonUpdate.prop("disabled", true);
	buttonDelete.prop("disabled", true);
	
	fieldCountryName.val("").focus();
	fieldCountryCode.val("");
}

function changeFormStateToSelectedCountry() {
	buttonAdd.prop("value", "New");
	buttonUpdate.prop("disabled", false);
	buttonDelete.prop("disabled", false);
	
	labelCountryName.text("Selected Country:");
	
	selectedCountryName = $("#dropdownCountries option:selected").text();
	fieldCountryName.val(selectedCountryName);
	
	countryCode = dropdownCountry.val().split("-")[1];
	fieldCountryCode.val(countryCode);
}

function loadCountries() {
	url = contextPath + "countries/list";
	$.get(url, function(responseJSON) {
		dropdownCountry.empty();
		$.each(responseJSON, function(index, country) {
			optionValue = country.id + "-" + country.code;
			$("<option>").val(optionValue).text(country.name).appendTo(dropdownCountry);
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