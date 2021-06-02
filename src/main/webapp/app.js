var baseurl = window.location.origin;

$.ajax({
	url: baseurl + "/todo",
	success: function(result){
		for (var i = 0; i < result.length; i++) {
			var cardHtml = "";
			cardHtml += '<div class="card mb-2" style="width: 18rem;"><div class="card-body"><h5 class="card-title">';
			cardHtml += result[i].name;
			cardHtml += '</h5><p class="card-text">';
			cardHtml += result[i].description
			cardHtml += '</p></div></div>';
			$('#todo-container').append(cardHtml);
		}
	}
})
