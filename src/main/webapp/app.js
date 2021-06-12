var tplCard = ({id, name, description}) => `
<div class="card mb-2">
	<div class="card-body">
		<h5 class="card-title">${name}</h5>
		<p class="card-text">${description}</p>
		<button type="button" class="btn btn-primary btn-sm text-end" onclick="prepareEditModal(${id}, '${name}', '${description}')"
					data-bs-toggle="modal" data-bs-target="#editTodoModal">
			<i class="bi bi-pencil"></i>
		</button>
		<button type="button" class="btn btn-danger btn-sm text-end" onclick="removeTodo(${id})"><i class="bi bi-trash"></i></button>
	</div>
</div>
`

var currentlyEditedTodoId;

function prepareEditModal(id, name, description) {
	currentlyEditedTodoId = id;

	$('#edit-todo-title').html('Edit Todo: ' + name);
	$('#edit-todo-name').val(name);
	$('#edit-todo-description').val(description);
}

var baseurl = window.location.origin;

var fetchTodos = function () {
	$.ajax({
		url: baseurl + "/todo",
		success: function(result){
			$('#todo-container').empty();
			for (var i = 0; i < result.length; i++) {
				$('#todo-container').append([{id: result[i].id, name: result[i].name, description: result[i].description}].map(tplCard));
			}
		}
	});
}

fetchTodos();

function validateFormGroup(formGroupId) {
	var fromGroup = $('#' + formGroupId);
	if(!fromGroup.hasClass("was-validated")){
		fromGroup.addClass("was-validated");
	}
}

function addTodo() {
	$.ajax({
		url: baseurl + "/todo",
		method: 'PUT',
		data: JSON.stringify({
			name: $('#add-todo-name').val(),
			description: $('#add-todo-description').val()
		}),
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		success: function(){
			$.growl.notice({message: 'Todo added successfully!'});
			$('#add-todo-name').val('');
			$('#add-todo-description').val('');
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
		success: function(){
			$.growl.notice({message: 'Todo removed successfully!'});
			fetchTodos();
		},
		error: function() {
			$.growl.error({message: 'Failed to remove Todo!'});
		}
	})
}

function editTodo() {
	$.ajax({
		url: baseurl + "/todo/" + currentlyEditedTodoId,
		method: 'PATCH',
		data: JSON.stringify({
			name: $('#edit-todo-name').val(),
			description: $('#edit-todo-description').val()
		}),
		contentType: "application/json; charset=utf-8",
		success: function(){
			$.growl.notice({message: 'Todo saved successfully!'});
			fetchTodos();
		},
		error: function() {
			$.growl.error({message: 'Failed to save Todo!'});
		}
	})
}
