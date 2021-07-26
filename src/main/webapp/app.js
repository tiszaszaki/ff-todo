let App = {
	PHASE_CNT: 3,
	modalDescriptionMaxCharCount: 1024,
	baseurl: window.location.origin,
	currentlyPickedTodoId: undefined,
	currentlyPickedTodoName: undefined,
	addTaskModalTitlePrefix: undefined,
	editTodoModalTitlePrefix: undefined,

	tplTask: function({task, todo}) {
		return `
		<li class="list-group-item d-flex justify-content-between">
			<div>
				<button type="button" class="btn btn-danger btn-sm text-end" onclick="App.removeTask(${task.id}, '${task.name}', '${todo.name}')"
							data-toggle="tooltip" data-placement="bottom" title="Remove Task">
					<i class="bi bi-trash"></i>
				</button>
				${task.name}
			</div>
			<input id="task-done-checkbox-${task.id}" class="form-check-input" type="checkbox"
				data-toggle="tooltip" data-placement="bottom" title="Check Task"
				onclick="App.checkTask(${task.id}, '${task.name}', ${task.done})">
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
						<button type="button" class="btn btn-primary btn-sm text-end" onclick="App.prepareAddTaskModal(${id}, '${name}')"
									data-bs-toggle="modal" data-bs-target="#addTaskModal" data-toggle="tooltip" data-placement="bottom" title="Add new Task">
							<i class="fas fa-plus"></i>
						</button>
						<button type="button" class="btn btn-primary btn-sm text-end" onclick="App.prepareEditTodoModal(${id}, '${name}', '${description}', '${phase}')"
									data-bs-toggle="modal" data-bs-target="#editTodoModal" data-toggle="tooltip" data-placement="bottom" title="Edit Todo">
							<i class="fas fa-pencil-alt"></i>
						</button>
						<button type="button" class="btn btn-danger btn-sm text-end" onclick="App.prepareRemoveTodoConfirmModal(${id}, '${name}')"
									data-bs-toggle="modal" data-bs-target="#remove-todo-confirm-modal" data-toggle="tooltip" data-placement="bottom" title="Remove Todo">
							<i class="fas fa-trash-alt"></i>
						</button>
						<button type="button" class="btn btn-danger btn-sm text-end" id="remove-all-tasks-for-todo-${id}-button"
									onclick="App.prepareRemoveAllTasksConfirmModal(${id}, '${name}')"
									data-bs-toggle="modal" data-bs-target="#remove-all-tasks-confirm-modal" data-toggle="tooltip" data-placement="bottom" title="Remove All Tasks">
							<i class="fas fa-broom"></i>
						</button>
					</div>
					<div class="btn-group" role="group">
						<button type="button" class="btn btn-primary btn-sm text-end" id = "shift-todo-left-${id}"
							onclick="App.shiftTodo(${id}, '${name}', '${description}', ${phase}, -1)"
							data-toggle="tooltip" data-placement="bottom" title="Shift Todo">
								<i class="fas fa-arrow-left"></i></i>
						</button>
						<button type="button" class="btn btn-primary btn-sm text-end" id = "shift-todo-right-${id}"
							onclick="App.shiftTodo(${id}, '${name}', '${description}', ${phase}, 1)"
							data-toggle="tooltip" data-placement="bottom" title="Shift Todo">
								<i class="fas fa-arrow-right"></i></i>
						</button>
					</div>
				</div>
			</div>
			<div class="card-footer text-muted">Updated ${moment(dateModified).fromNow()}</p>
		</div>
		`
	},
	tplTaskModal: function({modalPrefix, modalTitle, placeholderName, submitButtonCaption, dismissButtonCaption})
	{
		return `
		<div class="modal fade" id="${modalPrefix}TaskModal" tabindex="-1">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="${modalPrefix}-task-title">${modalTitle}</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
					</div>
					<div class="modal-body">
						<form novalidate>
							<div id="${modalPrefix}-task-name-form-group" class="mb-3">
								<label for="${modalPrefix}-task-name" class="col-form-label">Name:</label>
								<input type="text" class="form-control" id="${modalPrefix}-task-name"
									   placeholder="${placeholderName}"
									   oninput="App.validateFormGroup('${modalPrefix}-task-name-form-group')" required>
								<div id="${modalPrefix}-task-name-validator" class="invalid-feedback">
									Please provide a name.
								</div>
							</div>
						</form>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-primary" id="${modalPrefix}-task-submit-button"
							onclick="App.${modalPrefix}Task()" data-bs-dismiss="modal">${submitButtonCaption}</button>
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">${dismissButtonCaption}</button>
					</div>
				</div>
			</div>
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
		var modalName = modalPrefix + '-confirm-modal';
		return `
		<div class="modal fade" id="${modalName}" tabindex="-1">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title">${modalTitle}</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
					</div>
					<div class="modal-body">
						<p><div id="${modalName}-message">${modalMessage}</div></p>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-danger" id="${modalName}-submit-button">${submitButtonCaption}</button>
						<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">${dismissButtonCaption}</button>
					</div>
				</div>
			</div>
		</div>
		`
	},
	checkShiftTodoButtons: function(id, phase) {
		$("#shift-todo-left-" + id).prop("disabled", !((phase > 0) && (phase < App.PHASE_CNT)));
		$("#shift-todo-right-" + id).prop("disabled", !((phase >= 0) && (phase < App.PHASE_CNT-1)));
	},
	checkRemoveButton: function(buttonPrefix, count) {
		$("#remove-" + buttonPrefix + "-button").prop("disabled", !(count > 0));
	},
	setTodoModalCharCount: function(modalPrefix) {
		var textAreaLength = $('#' + modalPrefix + '-todo-description').val().length;
		var charCountStr = textAreaLength + ' / ' + App.modalDescriptionMaxCharCount;

		$('#' + modalPrefix + '-todo-description-char-count').html(charCountStr);
	},
	prepareAddTodoModal: function() {
		App.setTodoModalCharCount('add');
	},
	prepareEditTodoModal: function(id, name, description, phase) {
		App.currentlyPickedTodoId = id;

		$('#edit-todo-title').html(editTodoModalTitlePrefix + name);
		$('#edit-todo-name').val(name);
		$('#edit-todo-description').val(description);
		$('#edit-todo-phase' + phase).prop("checked", true);

		App.setTodoModalCharCount('edit');
	},
	prepareAddTaskModal: function(id, name) {
		App.currentlyPickedTodoId = id;
		App.currentlyPickedTodoName = name;

		$('#add-task-title').html(addTaskModalTitlePrefix + name);
	},
	fetchTodos: function () {
		$.ajax({
			url: App.baseurl + "/todo",
			success: function(result){
				var todo_count=result.length;

				for (var i = 0; i < App.PHASE_CNT; i++) {
					$('#todo-container-phase' + i).empty();
				}

				App.checkRemoveButton('all-todos', todo_count);

				for (var i = 0; i < todo_count; i++) {
					var temp_id = result[i].id;
					var temp_phase = result[i].phase;
					var temp_tasks = result[i].tasks;
					var task_count = temp_tasks.length;

					$('#todo-container-phase' + result[i].phase).append(
						[{id: temp_id, name: result[i].name, description: result[i].description, phase: temp_phase, dateModified: result[i].dateModified}].map(App.tplCard)
					);

					App.checkRemoveButton('all-tasks-for-todo-' + temp_id, task_count);

					for (var j = 0; j < task_count; j++) {
						$('#task-list-container-' + temp_id).append(
							[{task: temp_tasks[j], todo: result[i]}].map(App.tplTask)
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
	removeAllTodos: function(successCallback) {
		$.ajax({
			url: App.baseurl + "/todo/clear",
			method: 'DELETE',
			success: function(result) {
				successCallback();
				if (result > 0)
					$.growl.notice({message: 'All Todos were removed successfully!'});
				else
					$.growl.warning({message: 'No Todos were removed.'});
				App.fetchTodos();
			},
			error: function(result) {
				$.growl.error({message: 'Failed to remove all Todos!'});
			}
		})
	},
	patchTodoRequest: function(id, name, description, phase, successMessageVerb, errorMessageVerb)
	{
		$.ajax({
			url: App.baseurl + "/todo/" + id,
			method: 'PATCH',
			data: JSON.stringify({
				name: name,
				description: description,
				phase: phase
			}),
			contentType: "application/json; charset=utf-8",
			success: function(){
				$.growl.notice({message: 'Todo (' + name + ') ' + successMessageVerb + ' successfully!'});
				App.fetchTodos();
			},
			error: function() {
				$.growl.error({message: 'Failed to ' + errorMessageVerb + ' Todo (' + name + ')!'});
			}
		})
	},
	editTodo: function() {
		App.patchTodoRequest(App.currentlyPickedTodoId, $('#edit-todo-name').val(),
			$('#edit-todo-description').val(), $('input[name=edit-todo-phase]:checked').val(), 'saved', 'save');
	},
	shiftTodo: function(id, name, description, phase, phase_diff) {
		if ((phase_diff == -1) || (phase_diff == 1))
		{
			var phase_new = phase + phase_diff;
			if ((phase_new >= 0) && (phase_new < App.PHASE_CNT)) {
				App.patchTodoRequest(id, name, description, phase_new, 'shifted', 'shift');
			} else {
				$.growl.warning({message: 'Todo (' + name + ') cannot be shifted this way!'});
			}
		} else {
			$.growl.error({message: "Invalid value (" + phase_diff + ") for parameter 'phase_diff' of function 'shiftTodo'!"});
		}
	},
	addTask: function() {
		var todo_id = App.currentlyPickedTodoId;
		var todo_name = App.currentlyPickedTodoName;
		var task_name = $('#add-task-name').val();

		$.ajax({
			url: App.baseurl + "/todo/" + todo_id + "/task",
			method: 'PUT',
			data: JSON.stringify({
				name: task_name,
				done: false,
			}),
			contentType: "application/json; charset=utf-8",
			success: function(){
				$.growl.notice({message: 'Task (' + task_name + ') added successfully for Todo (' + todo_name + ')!'});
				$('#add-task-name').val('');
				App.fetchTodos();
			},
			error: function() {
				$.growl.error({message: 'Failed to add Task (' + task_name + ')!'});
			}
		});
	},
	removeTask: function(id, name, todo_name) {
		$.ajax({
			url: App.baseurl + "/task/" + id,
			method: 'DELETE',
			success: function(){
				$.growl.notice({message: 'Task (' + name + ') removed successfully from Todo (' + todo_name + ')!'});
				App.fetchTodos();
			},
			error: function() {
				$.growl.error({message: 'Failed to remove Task (' + name + ') from Todo (' + todo_name + ')!'});
			}
		})
	},
	checkTask: function(id, name, done)
	{
		$.ajax({
			url: App.baseurl + "/task/" + id,
			method: 'PATCH',
			data: JSON.stringify({
				name: name,
				done: !done,
			}),
			contentType: "application/json; charset=utf-8",
			success: function(){
				$.growl.notice({message: 'Task (' + id + ',' + name + ') checked successfully!'});
				App.fetchTodos();
			},
			error: function() {
				$.growl.error({message: 'Failed to check Task (' + id + ',' + name + ')!'});
			}
		})
	},
	removeAllTasks: function(id, name, successCallback) {
		$.ajax({
			url: App.baseurl + "/todo/" + id + "/task/clear",
			method: 'DELETE',
			success: function(result) {
				successCallback();
				if (result > 0)
					$.growl.notice({message: 'All Tasks were removed successfully from Todo (' + name + ')!'});
				else
					$.growl.warning({message: 'No Tasks were removed from Todo (' + name + ').'});
				App.fetchTodos();
			},
			error: function(result) {
				$.growl.error({message: 'Failed to remove any Task from Todo (' + name + ')!'});
			}
		})
	},
	validateFormGroup: function(formGroupId) {
		var fromGroup = $('#' + formGroupId);
		if(!fromGroup.hasClass("was-validated")){
			fromGroup.addClass("was-validated");
		}
	},
	submitModal: function(e, modalPrefix)
	{
		if (e.which == 13) {
			$('#' + modalPrefix + '-submit-button').click();
			e.preventDefault();
		}
	},
	prepareModals: function() {
		$('#modal-container').append(
			[{modalPrefix: 'add', modalTitle: 'Add Todo',
				placeholderName: 'Enter name for new Todo...', placeholderDescription: 'Enter description for new Todo (optional)...',
				submitButtonCaption: 'Add', dismissButtonCaption: 'Close'}].map(App.tplTodoModal)
		);

		$('#add-todo-name').keypress(function(e) { App.submitModal(e, 'add-todo'); });

		editTodoModalTitlePrefix = 'Edit todo: ',

		$('#modal-container').append(
			[{modalPrefix: 'edit', modalTitle: '',
				placeholderName: 'Change name for this Todo...', placeholderDescription: 'Change description for this Todo...',
				submitButtonCaption: 'Save', dismissButtonCaption: 'Cancel'}].map(App.tplTodoModal)
		);

		$('#edit-todo-name').keypress(function(e) { App.submitModal(e, 'edit-todo'); });

		addTaskModalTitlePrefix = 'Add task for this Todo: ',

		$('#modal-container').append(
			[{modalPrefix: 'add', modalTitle: '',
				placeholderName: 'Enter name for new Task...',
				submitButtonCaption: 'Add', dismissButtonCaption: 'Close'}].map(App.tplTaskModal)
		);

		$('#add-task-name').keypress(function(e) { App.submitModal(e, 'add-task'); });

		$('#modal-container').append(
			[{modalPrefix: 'remove-todo', modalTitle: 'Confirm removing a Todo', modalMessage: '',
				submitButtonCaption: 'Remove', dismissButtonCaption: 'Cancel'}].map(App.tplConfirmModal)
		);

		$('#remove-todo-confirm-modal-submit-button').click(function(e) { App.submitRemoveTodoConfirmModal(); });

		$('#modal-container').append(
			[{modalPrefix: 'remove-all-todos', modalTitle: 'Confirm removing all Todos',
				modalMessage: 'Are you sure to remove all Todos?',
				submitButtonCaption: 'Remove All', dismissButtonCaption: 'Cancel'}].map(App.tplConfirmModal)
		);

		$('#remove-all-todos-confirm-modal-submit-button').click(function(e) { App.submitRemoveAllTodosConfirmModal(); });

		$('#modal-container').append(
			[{modalPrefix: 'remove-all-tasks', modalTitle: 'Confirm removing all Tasks', modalMessage: '',
				submitButtonCaption: 'Remove All', dismissButtonCaption: 'Cancel'}].map(App.tplConfirmModal)
		);

		$('#remove-all-tasks-confirm-modal-submit-button').click(function(e) { App.submitRemoveAllTasksConfirmModal(); });
	},
	dismissModal: function(modalPrefix)
	{
		$('#' + modalPrefix + '-modal').modal('hide');
	},
	submitRemoveTodoConfirmModal: function()
	{
		App.removeTodo(App.currentlyPickedTodoId, App.currentlyPickedTodoName, function()
		{
			App.dismissModal('remove-todo-confirm');
		})
	},
	submitRemoveAllTodosConfirmModal: function()
	{
		App.removeAllTodos(function()
		{
			App.dismissModal('remove-all-todos-confirm');
		})
	},
	submitRemoveAllTasksConfirmModal: function()
	{
		App.removeAllTasks(App.currentlyPickedTodoId, App.currentlyPickedTodoName, function()
		{
			App.dismissModal('remove-all-tasks-confirm');
		})
	},
	prepareRemoveTodoConfirmModal: function(id, name)
	{
		App.currentlyPickedTodoId = id;
		App.currentlyPickedTodoName = name;

		$('#remove-todo-confirm-modal-message').html('Are you sure to remove this Todo (' + name + ')?');
	},
	prepareRemoveAllTasksConfirmModal: function(id, name)
	{
		App.currentlyPickedTodoId = id;
		App.currentlyPickedTodoName = name;

		$('#remove-all-tasks-confirm-modal-message').html('Are you sure to remove all Tasks from this Todo (' + name + ')?');
	}
}

$(document).ready(function () {
	App.prepareModals();
	App.fetchTodos();
});
