let App = {
	PHASE_CNT: 3,
	modalDescriptionMaxCharCount: 1024,
	baseurl: window.location.origin,
	currentlyPickedTodoId: undefined,
	currentlyPickedTodoName: undefined,
	editTodoModalTitlePrefix: undefined,

	tplTask: function({task}) {
		return `
		<li class="list-group-item d-flex justify-content-between">
			${task.name}
			<input id="task-done-checkbox-${task.id}" class="form-check-input" type="checkbox">
		</li>`
	},
	tplCard: function({id, name, description, phase, dateModified}) {
		return `
		<div class="card mb-2">
			<div class="card-body">
				<h5 class="card-title">${name}</h5>
				<p class="card-text">${description}</p>
				<ul id="task-list-container-${id}" class="list-group p-2">
				</ul>
				<div class="d-flex justify-content-between">
				<div class="btn-group" role="group">
				<button type="button" class="btn btn-primary btn-sm text-end" onclick="App.prepareEditModal(${id}, '${name}', '${description}', '${phase}')"
							data-bs-toggle="modal" data-bs-target="#editTodoModal" data-toggle="tooltip" data-placement="bottom" title="Edit Todo">
					<i class="bi bi-pencil"></i>
				</button>
				<button type="button" class="btn btn-danger btn-sm text-end" onclick="App.prepareRemoveConfirmModal(${id}, '${name}')"
							data-bs-toggle="modal" data-bs-target="#removeConfirmModal" data-toggle="tooltip" data-placement="bottom" title="Remove Todo">
					<i class="bi bi-trash"></i>
				</button>
				</div>
				<div class="btn-group" role="group">
					<button type="button" class="btn btn-primary btn-sm text-end" id = "shift-todo-left-${id}"
						onclick="App.shiftTodo(${id}, '${name}', '${description}', ${phase - 1})"
						data-toggle="tooltip" data-placement="bottom" title="Shift Todo"><i class="bi bi-arrow-left"></i></button>
					<button type="button" class="btn btn-primary btn-sm text-end" id = "shift-todo-right-${id}"
						onclick="App.shiftTodo(${id}, '${name}', '${description}', ${phase + 1})"
						data-toggle="tooltip" data-placement="bottom" title="Shift Todo"><i class="bi bi-arrow-right"></i></button>
				</div>
				</div>
			</div>
			<div class="card-footer text-muted">Updated ${moment(dateModified).fromNow()}</p>
		</div>
		`
	},
	tplTodoModal: function({modalPrefix, modalTitle, placeholderName, placeholderDescription, submitButtonCaption, dismissButtonCaption})
	{
		return `
		<div class="modal fade" id="${modalPrefix}TodoModal" tabindex="-1">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="${modalPrefix}-todo-title">${modalTitle}</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
					</div>
					<div class="modal-body">
						<form novalidate>
							<div id="${modalPrefix}-todo-name-form-group" class="mb-3">
								<label for="${modalPrefix}-todo-name" class="col-form-label">Name:</label>
								<input type="text" class="form-control" id="${modalPrefix}-todo-name"
									   placeholder="${placeholderName}"
									   oninput="App.validateFormGroup('${modalPrefix}-todo-name-form-group')" required>
								<div id="${modalPrefix}-todo-name-validator" class="invalid-feedback">
									Please provide a name.
								</div>
							</div>
							<div class="mb-3">
								<label for="${modalPrefix}-todo-description" class="col-form-label">Description:</label>
								<textarea class="form-control" id="${modalPrefix}-todo-description" onkeyup="App.setTodoModalCharCount('${modalPrefix}')"
										  placeholder="${placeholderDescription}"
										  maxlength="${App.modalDescriptionMaxCharCount}"></textarea>
								<span class="pull-right label label-default" id="${modalPrefix}-todo-description-char-count"></span>
							</div>
							<div>
							<label class="col-form-label">Phase:</label>
							</div>
							<div class="btn-group" role="group">
								<input type="radio" class="btn-check" name="${modalPrefix}-todo-phase" id="${modalPrefix}-todo-phase0" value="0" checked>
								<label class="btn btn-outline-primary" for="${modalPrefix}-todo-phase0">Todo</label>

								<input type="radio" class="btn-check" name="${modalPrefix}-todo-phase" id="${modalPrefix}-todo-phase1" value="1">
								<label class="btn btn-outline-primary" for="${modalPrefix}-todo-phase1">In progress</label>

								<input type="radio" class="btn-check" name="${modalPrefix}-todo-phase" id="${modalPrefix}-todo-phase2" value="2">
								<label class="btn btn-outline-primary" for="${modalPrefix}-todo-phase2">Done</label>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" id="${modalPrefix}-todo-submit-button"
							onclick="App.${modalPrefix}Todo()" data-bs-dismiss="modal">${submitButtonCaption}</button>
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">${dismissButtonCaption}</button>
					</div>
				</div>
			</div>
		</div>
		`
	},
	tplConfirmModal: function({modalPrefix, modalTitle, modalMessage, submitButtonCaption, dismissButtonCaption})
	{
		var modalName = modalPrefix + 'ConfirmModal';
		return `
		<div class="modal fade" id="${modalName}" tabindex="-1">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title">${modalTitle}</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
					</div>
					<div class="modal-body">
						<p><div id="${modalPrefix}-confirm-modal-message">${modalMessage}</div></p>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-danger" id="${modalPrefix}-confirm-modal-submit-button">${submitButtonCaption}</button>
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">${dismissButtonCaption}</button>
					</div>
				</div>			</div>
		</div>
		`
	},
	checkShiftTodoButtons: function(id, phase) {
		$("#shift-todo-left-" + id).prop("disabled", !((phase > 0) && (phase < App.PHASE_CNT)));
		$("#shift-todo-right-" + id).prop("disabled", !((phase >= 0) && (phase < App.PHASE_CNT-1)));
	},
	setTodoModalCharCount: function(modalPrefix) {
		var textAreaLength = $('#' + modalPrefix + '-todo-description').val().length;
		var charCountStr = textAreaLength + ' / ' + App.modalDescriptionMaxCharCount;

		$('#' + modalPrefix + '-todo-description-char-count').html(charCountStr);
	},
	prepareAddModal: function() {
		App.setTodoModalCharCount('add');
	},
	prepareEditModal: function(id, name, description, phase) {
		App.currentlyPickedTodoId = id;

		$('#edit-todo-title').html(editTodoModalTitlePrefix + name);
		$('#edit-todo-name').val(name);
		$('#edit-todo-description').val(description);
		$('#edit-todo-phase' + phase).prop("checked", true);

		App.setTodoModalCharCount('edit');
	},
	fetchTodos: function () {
		$.ajax({
			url: App.baseurl + "/todo",
			success: function(result){
				for (var i = 0; i < App.PHASE_CNT; i++) {
					$('#todo-container-phase' + i).empty();
				}
				for (var i = 0; i < result.length; i++) {
					var temp_id=result[i].id;
					var temp_phase=result[i].phase;
					var temp_tasks = result[i].tasks;

					$('#todo-container-phase' + result[i].phase).append(
						[{id: temp_id, name: result[i].name, description: result[i].description, phase: temp_phase, dateModified: result[i].dateModified}].map(App.tplCard)
					);

					for (var j = 0; j < temp_tasks.length; j++) {
						$('#task-list-container-' + temp_id).append(
							[{task: temp_tasks[j]}].map(App.tplTask)
						);
						$('#task-done-checkbox-' + temp_tasks[j].id).prop('checked', temp_tasks[j].done);
					}

					App.checkShiftTodoButtons(temp_id, temp_phase);
				}
			}
		});
	},
	addTodo: function() {
		var todo_name = $('#add-todo-name').val();
		$.ajax({
			url: App.baseurl + "/todo",
			method: 'PUT',
			data: JSON.stringify({
				name: todo_name,
				description: $('#add-todo-description').val(),
				phase: $('input[name=add-todo-phase]:checked').val()
			}),
			contentType: "application/json; charset=utf-8",
			dataType: "json",
			success: function(){
				$.growl.notice({message: 'Todo (' + todo_name + ') added successfully!'});
				$('#add-todo-name').val('');
				$('#add-todo-description').val('');
				$('#add-todo-phase0').prop("checked", true);
				App.fetchTodos();
			},
			error: function() {
				$.growl.error({message: 'Failed to add Todo (' + todo_name + ')!'});
			}
		});
	},
	removeTodo: function(id, name, successCallback) {
		$.ajax({
			url: App.baseurl + "/todo/" + id,
			method: 'DELETE',
			success: function(){
				successCallback();
				$.growl.notice({message: 'Todo (' + name + ') removed successfully!'});
				App.fetchTodos();
			},
			error: function() {
				$.growl.error({message: 'Failed to remove Todo (' + name + ')!'});
			}
		})
	},
	editTodo: function() {
		var todo_name = $('#edit-todo-name').val();
		$.ajax({
			url: App.baseurl + "/todo/" + App.currentlyPickedTodoId,
			method: 'PATCH',
			data: JSON.stringify({
				name: todo_name,
				description: $('#edit-todo-description').val(),
				phase: $('input[name=edit-todo-phase]:checked').val()
			}),
			contentType: "application/json; charset=utf-8",
			success: function(){
				$.growl.notice({message: 'Todo (' + todo_name + ') saved successfully!'});
				App.fetchTodos();
			},
			error: function() {
				$.growl.error({message: 'Failed to save Todo (' + todo_name + ')!'});
			}
		})
	},
	shiftTodo: function(id2, name2, description2, phase_new) {
		if ((phase_new >= 0) && (phase_new < App.PHASE_CNT)) {
			$.ajax({
				url: App.baseurl + "/todo/" + id2,
				method: 'PATCH',
				data: JSON.stringify({
					name: name2,
					description: description2,
					phase: phase_new
				}),
				contentType: "application/json; charset=utf-8",
				success: function(){
					$.growl.notice({message: 'Todo (' + name2 + ') shifted successfully!'});
					App.fetchTodos();
				},
				error: function() {
					$.growl.error({message: 'Failed to shift Todo (' + name2 + ')!'});
				}
			})
		} else {
			$.growl.warning({message: 'Todo (' + name2 + ') cannot be shifted this way!'});
		}
	},
	validateFormGroup: function(formGroupId) {
		var fromGroup = $('#' + formGroupId);
		if(!fromGroup.hasClass("was-validated")){
			fromGroup.addClass("was-validated");
		}
	},
	submitTodoModal: function(e, modalPrefix)
	{
		if (e.which == 13) {
			$('#' + modalPrefix + '-todo-submit-button').click();
			e.preventDefault();
		}
	},
	prepareModals: function() {
		$('#modal-container').append(
			[{modalPrefix: 'add', modalTitle: 'Add Todo',
				placeholderName: 'Enter name for new Todo...', placeholderDescription: 'Enter description for new Todo (optional)...',
				submitButtonCaption: 'Add', dismissButtonCaption: 'Close'}].map(App.tplTodoModal)
		);

		$('#add-todo-name').keypress(function(e) { App.submitTodoModal(e, 'add'); });

		editTodoModalTitlePrefix = 'Edit todo: ',

		$('#modal-container').append(
			[{modalPrefix: 'edit', modalTitle: '',
				placeholderName: 'Change name for this Todo...', placeholderDescription: 'Change description for this Todo...',
				submitButtonCaption: 'Save', dismissButtonCaption: 'Cancel'}].map(App.tplTodoModal)
		);

		$('#edit-todo-name').keypress(function(e) { App.submitTodoModal(e, 'edit'); });

		$('#modal-container').append(
			[{modalPrefix: 'remove', modalTitle: 'Confirm removing a Todo', modalMessage: '',
				submitButtonCaption: 'Remove', dismissButtonCaption: 'Cancel'}].map(App.tplConfirmModal)
		);

		$('#remove-confirm-modal-submit-button').click(function(e) { App.submitConfirmRemoveModal(); });
	},
	dismissModal: function(modalPrefix)
	{
		$('#' + modalPrefix + 'Modal').modal('hide');
	},
	submitConfirmRemoveModal: function()
	{
		App.removeTodo(App.currentlyPickedTodoId, App.currentlyPickedTodoName, function()
		{
			App.dismissModal('removeConfirm');
		})
	},
	prepareRemoveConfirmModal: function(id, name)
	{
		App.currentlyPickedTodoId = id;
		App.currentlyPickedTodoName = name;
		$('#remove-confirm-modal-message').html('Are you sure to remove this Todo (' + name + ')?');
	}
}

$(document).ready(function () {
	App.prepareModals();
	App.fetchTodos();
});
