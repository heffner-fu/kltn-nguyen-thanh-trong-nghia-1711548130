$(document).ready(function() {
		$("#logoutLink").on("click", function(e) {
			e.preventDefault();
			document.logoutForm.submit();
		});
		
});

function customizeTabs() {
	var url = document.location.toString();
	if (url.match('#')) {
		$('.nav-tab a[href="#' + url.split('#')[1] +'"]"').tab('show');
	}
	
	$('.nav-tabs a').on('show.bs.tab', function (e) {
		window.location.hash = e.target.hash;
	})
}

