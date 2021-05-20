var baseurl = window.location.host;

$.ajax({
	url: baseurl + "/test",
	success: function(result){
		console.log(result);
	}
})
