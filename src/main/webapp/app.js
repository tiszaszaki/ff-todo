var baseurl = window.location.origin;

var fetchTodos = function () {
	$.ajax({
		url: baseurl + "/todo",
		success: function(result){
			$('#todo-container').empty();
			for (var i = 0; i < result.length; i++) {
				var cardHtml = "";
				cardHtml += '<div class="card mb-2"><div class="card-body"><h5 class="card-title">';
				cardHtml += result[i].name;
				cardHtml += '</h5><p class="card-text">';
				cardHtml += result[i].description;
				cardHtml += '</p><button type="button" class="btn btn-primary btn-sm text-end"><i class="bi bi-pencil"></i></button>';
				cardHtml += '<button type="button" class="btn btn-danger btn-sm text-end" onclick="removeTodo(' + result[i].id + ')"><i class="bi bi-trash"></i></button>';
				cardHtml += '</div></div>';
				$('#todo-container').append(cardHtml);
			}
		}
	});
}

fetchTodos();

function addTodo() {
	$.ajax({
		url: baseurl + "/todo",
		method: 'PUT',
		data: JSON.stringify({
			name: $('#todo-name').val(),
			description: $('#todo-description').val()
		}),
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function(result){
			$.growl.notice({message: 'Todo added successfully!'});
			fetchTodos();
		},
		error: function() {
			$.growl.error({message: 'Failed to add Todo!'});
		}
	})
}

function removeTodo(id) {
	$.ajax({
		url: baseurl + "/todo/" + id,
		method: 'DELETE',
		success: function(result){
			$.growl.notice({message: 'Todo removed successfully!'});
			fetchTodos();
		},
		error: function() {
			$.growl.error({message: 'Failed to remove Todo!'});
		}
	})
}
