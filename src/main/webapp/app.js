var PHASE_CNT = 3

function checkShiftTodoButtons(id, phase)
{
	$("#shift-todo-left-" + id).prop("disabled", !((phase > 0) && (phase < PHASE_CNT)));
	$("#shift-todo-right-" + id).prop("disabled", !((phase >= 0) && (phase < PHASE_CNT-1)));
}

var tplCard = ({id, name, description, phase, dateModified}) => `
<div class="card mb-2">
	<div class="card-body">
		<h5 class="card-title">${name}</h5>
		<p class="card-text">${description}</p>
		<button type="button" class="btn btn-primary btn-sm text-end" onclick="prepareEditModal(${id}, '${name}', '${description}', '${phase}')"
					data-bs-toggle="modal" data-bs-target="#editTodoModal">
			<i class="bi bi-pencil"></i>
		</button>
		<button type="button" class="btn btn-danger btn-sm text-end" onclick="removeTodo(${id})"><i class="bi bi-trash"></i></button>
		<div class="btn-group" role="group">
			<button type="button" class="btn btn-primary btn-sm text-end"
				id = "shift-todo-left-${id}"
				onclick="shiftTodo(${id}, '${name}', '${description}', ${phase - 1})"><i class="bi bi-arrow-left"></i></button>
			<button type="button" class="btn btn-primary btn-sm text-end"
				id = "shift-todo-right-${id}"
				onclick="shiftTodo(${id}, '${name}', '${description}', ${phase + 1})"><i class="bi bi-arrow-right"></i></button>
		</div>
	</div>
	<div class="card-footer text-muted">Updated ${moment(dateModified).fromNow()}</p>
</div>
`

var currentlyEditedTodoId;

var modalDescriptionMaxCharCount=1024;

function setTodoModalCharCount(modalPrefix)
{
	var textAreaLength = $('#' + modalPrefix + '-todo-description').val().length;
	var charCountStr = textAreaLength + ' / ' + modalDescriptionMaxCharCount;

	$('#' + modalPrefix + '-todo-description-char-count').html(charCountStr);
}

function prepareAddModal()
{
	setTodoModalCharCount('add');
}

function prepareEditModal(id, name, description, phase) {
	currentlyEditedTodoId = id;

	$('#edit-todo-title').html('Edit Todo: ' + name);
	$('#edit-todo-name').val(name);
	$('#edit-todo-description').val(description);
	$('#edit-todo-phase' + phase).prop("checked", true);

	setTodoModalCharCount('edit');
}

var baseurl = window.location.origin;

var fetchTodos = function () {
	$.ajax({
		url: baseurl + "/todo",
		success: function(result){
			for (var i = 0; i < PHASE_CNT; i++) {
				$('#todo-container-phase' + i).empty();
			}
			for (var i = 0; i < result.length; i++) {
				var temp_id=result[i].id;
				var temp_phase=result[i].phase;

				$('#todo-container-phase' + result[i].phase).append(
					[{id: temp_id, name: result[i].name, description: result[i].description, phase: temp_phase, dateModified: result[i].dateModified}].map(tplCard)
				);

				checkShiftTodoButtons(temp_id, temp_phase);
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
			description: $('#add-todo-description').val(),
			phase: $('input[name=add-todo-phase]:checked').val()
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
	});
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
			description: $('#edit-todo-description').val(),
			phase: $('input[name=edit-todo-phase]:checked').val()
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

function shiftTodo(id2, name2, description2, phase_new) {
	if ((phase_new >= 0) && (phase_new < PHASE_CNT))
	{
		$.ajax({
			url: baseurl + "/todo/" + id2,
			method: 'PATCH',
			data: JSON.stringify({
				name: name2,
				description: description2,
				phase: phase_new
			}),
			contentType: "application/json; charset=utf-8",
			success: function(){
				$.growl.notice({message: 'Todo shifted successfully!'});
				fetchTodos();
			},
			error: function() {
				$.growl.error({message: 'Failed to shift Todo!'});
			}
		})
	}
	else
		$.growl.warning({message: 'Todo cannot be shifted this way!'});
}
