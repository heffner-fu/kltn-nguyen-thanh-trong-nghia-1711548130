dropdownBrands = $("#brand");
dropdownCategories = $("#category");

$(document).ready(function() {
	
	$("#shortDescription").richText();
	
	$("#fullDescription").richText();
	
	dropdownBrands.change(function() {
		dropdownCategories.empty();
		getCategories()
	});
	getCategoriesForNewForm();
	
});

function getCategoriesForNewForm() {
	catIdField = $("#categoryId");
	editmode = false;
	if (catIdField.length) {
		editmode = true;
	}
	if (!editmode) getCategories();
}

function getCategories() {
	brandId = dropdownBrands.val();
	url = brandModuleURL + "/" + brandId + "/categories";
	$.get(url, function(responseJson) {
		$.each(responseJson, function(index, category) {
			$("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
		});
	});
}

function checkUnique(form) {
	productId = $("#id").val();
	productName = $("#name").val();
	csrfValue = $("input[name='_csrf']").val();
	params = {id: productId, name: productName, _csrf: csrfValue};
	$.post(checkUniqueUrl, params, function(response) {
		if(response == "OK") {
			form.submit();
		} else if(response == "Duplicate") {
			showWarningDialog("There is another product having same name: '" + productName + "''" );
		} else {
			showErrorDialog("Unknown response from server");
		}
	}).fail(function() {
		showErrorDialog("Could not connect to server");
	});
	return false;
}