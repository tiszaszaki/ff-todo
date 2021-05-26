var baseurl = window.location.origin;

$.ajax({
	url: baseurl + "/test",
	success: function(result){
		console.log(result);
	}
})
