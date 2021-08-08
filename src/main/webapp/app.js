let App = {
	PHASE_CNT: 3,
	modalDescriptionMaxCharCount: 1024,
	baseurl: window.location.origin,
	currentlyPickedTodoId: undefined,
	currentlyPickedTodoName: undefined,
	addTaskModalTitlePrefix: undefined,
	editTodoModalTitlePrefix: undefined,

	fetchTodoSortingDirection: undefined,
	fetchTodoSortingProperty: undefined,
	fetchTaskSortingDirection: undefined,
	fetchTaskSortingProperty: undefined,

	task_overall_count: undefined,

	doTodoSorting: false,
	doTaskSorting: false,

	todoModalCharCountRemainingDisplayed: true,
	checkIfRemoveAllButtonsCanBeDisabled: true, // debug purpose only

	todoSortingFields: new Map([['name','Todo name'],['description','Todo description'],
			['descriptionLength','Todo description length'],['taskCount','Task count in Todo'],
			['dateCreated','Date of Todo created'],['dateModified','Date of Todo updated']]),

	taskSortingFields: new Map([['name','Task name'],['done','Task checked']]),

	tplTask: function({task, todo}) {
		return `
		<li class="list-group-item d-flex justify-content-between">
			<div>
				<button type="button" class="btn btn-danger btn-sm text-end" onclick="App.removeTask(${task.id}, '${task.name}', '${todo.name}')"
							data-toggle="tooltip" data-placement="bottom" title="Remove Task">
					<i class="fas fa-trash-alt"></i>
				</button>
				${task.name}
			</div>
			<input id="task-done-checkbox-${task.id}" class="form-check-input" type="checkbox"
				data-toggle="tooltip" data-placement="bottom" title="Check Task"
				onclick="App.checkTask(${task.id}, '${task.name}', ${task.done})">
		</li>`
	},
	tplCard: function({id, name, description, phase, dateModified, dateCreated, descriptionLength}) {
		var cardFooter="";
		var cardFooterAddition="";
		var cardToolbar="";
		var cardTasks="";
		var cardDescription="";
		var cardDescriptionAddition="";

		if (dateCreated !== undefined)
		{
			cardFooterAddition = `
				<div data-toggle="tooltip" data-placement="bottom" title="This field is displayed only when involved by active sorting.">
					<p>Created ${moment(dateCreated).fromNow()}</p>
				</div>
			`
		}

		cardFooter = `
			<p>Updated ${moment(dateModified).fromNow()}</p>
			${cardFooterAddition}
		`

		cardToolbar = `
			<div class="d-flex justify-content-between">
				<div class="btn-group" role="group">
					<button type="button" class="btn btn-primary btn-sm text-end" onclick="App.prepareAddTaskModal(${id}, '${name}', true)"
								data-bs-toggle="modal" data-bs-target="#addTaskModal" data-toggle="tooltip" data-placement="bottom" title="Add new Task">
						<i class="fas fa-plus"></i>
					</button>
					<button type="button" class="btn btn-primary btn-sm text-end" onclick="App.prepareEditTodoModal(${id}, '${name}', '${description}', '${phase}', true)"
								data-bs-toggle="modal" data-bs-target="#editTodoModal" data-toggle="tooltip" data-placement="bottom" title="Edit Todo">
						<i class="fas fa-pencil-alt"></i>
					</button>
				</div>
				<div class="btn-group" role="group">
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
						data-toggle="tooltip" data-placement="bottom" title="Shift Todo to the left">
							<i class="fas fa-arrow-left"></i></i>
					</button>
					<button type="button" class="btn btn-primary btn-sm text-end" id = "shift-todo-right-${id}"
						onclick="App.shiftTodo(${id}, '${name}', '${description}', ${phase}, 1)"
						data-toggle="tooltip" data-placement="bottom" title="Shift Todo to the right">
							<i class="fas fa-arrow-right"></i></i>
					</button>
				</div>
			</div>
		`

		cardTasks = `
			<button class="btn btn-secondary" type="button"
					data-toggle="collapse" data-target="#task-list-container-${id}-collapse"
					data-toggle="tooltip" data-placement="bottom" title="Toggle Todo list">
				<i class="fas fa-folder-plus"></i>
			</button>
			<div id="task-list-container-${id}-collapse" class="collapse show">
				<ul id="task-list-container-${id}" class="list-group border border-primary"></ul>
			</div>
			<div id="task-list-counter-${id}"
				data-toggle="tooltip" data-placement="bottom" title="This field is displayed only when involved by active sorting."></div>
		`

		if (descriptionLength !== undefined)
		{
			cardDescriptionAddition = `
				<div data-toggle="tooltip" data-placement="bottom" title="This field is displayed only when involved by active sorting.">
					Description length: ${descriptionLength}
				</div>
			`
		}

		cardDescription = `
			<div>
				<ul class="list-group list-group-flush">
					<li class="list-group-item rounded border border-secondary"><p class="card-text">${description}</p></li>
					<li class="list-group-item">${cardDescriptionAddition}</li>
				</ul>
			</div>
		`

		return `
		<div class="card mb-2">
			<div class="card-header">
				<h5 class="card-title">
					<span class="text-muted">#${id}</span>
					${name}
				</h5>
			</div>
			<div class="card-body">
				${cardDescription}
			</div>
				<ul class="list-group list-group-flush">
					<li class="list-group-item">${cardTasks}</li>
					<li class="list-group-item">${cardToolbar}</li>
				</ul>
			<div class="card-footer text-muted">
				${cardFooter}
			</div>
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
								<textarea class="form-control" id="${modalPrefix}-todo-description" onkeyup="App.setTodoModalCharCount('${modalPrefix}', ${App.todoModalCharCountRemainingDisplayed})"
										  placeholder="${placeholderDescription}"
										  maxlength="${App.modalDescriptionMaxCharCount}"></textarea>
								<div class="d-flex justify-content-end">
									<span class="border border-secondary label" id="${modalPrefix}-todo-description-char-count"></span>
								</div>
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
	tplSortingModal: function({modalPrefix, modalTitle, submitButtonCaption, dismissButtonCaption})
	{
		var modalName = modalPrefix + '-sorting-modal';
		return `
		<div class="modal fade" id="${modalName}" tabindex="-1">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title">${modalTitle}</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
					</div>
					<div class="modal-body">
						<div>
							<label class="col-form-label">Field:</label>
						</div>
						<div class="btn-group-vertical" role="group" id="${modalPrefix}-sorting-field-group"></div>
						<div>
							<label class="col-form-label">Direction:</label>
						</div>
						<div class="btn-group" role="group" id="${modalPrefix}-sorting-direction-group">
							<input type="radio" class="btn-check" name="${modalPrefix}-sorting-direction" id="${modalPrefix}-sorting-asc" value="asc">
							<label class="btn btn-outline-primary" for="${modalPrefix}-sorting-asc"
									data-toggle="tooltip" data-placement="bottom" title="Ascending">
								<i class="fas fa-sort-amount-up-alt"></i>
							</label>

							<input type="radio" class="btn-check" name="${modalPrefix}-sorting-direction" id="${modalPrefix}-sorting-desc" value="desc">
							<label class="btn btn-outline-primary" for="${modalPrefix}-sorting-desc"
									data-toggle="tooltip" data-placement="bottom" title="Descending">
								<i class="fas fa-sort-amount-down-alt"></i>
							</label>
						</div>
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
	setTodoModalCharCount: function(modalPrefix, displayRemaining) {
		var textAreaLength = $('#' + modalPrefix + '-todo-description').val().length;
		var remainingLength = App.modalDescriptionMaxCharCount - textAreaLength;
		var charCountStr = "";

		if (displayRemaining)
			charCountStr += remainingLength;
		else
			charCountStr += textAreaLength + ' / ' + App.modalDescriptionMaxCharCount;

		$('#' + modalPrefix + '-todo-description-char-count').html(charCountStr);
	},
	prepareAddTodoModal: function(doAutofocus) {
		App.setTodoModalCharCount('add', App.todoModalCharCountRemainingDisplayed);

		if (doAutofocus)
		{
			$('#addTodoModal').on('shown.bs.modal', function () { $('#add-todo-name').trigger('focus'); });
		}
	},
	prepareEditTodoModal: function(id, name, description, phase, doAutofocus) {
		App.currentlyPickedTodoId = id;

		$('#edit-todo-title').html(editTodoModalTitlePrefix + name);
		$('#edit-todo-name').val(name);
		$('#edit-todo-description').val(description);
		$('#edit-todo-phase' + phase).prop("checked", true);

		App.setTodoModalCharCount('edit', App.todoModalCharCountRemainingDisplayed);

		if (doAutofocus)
		{
			$('#editTodoModal').on('shown.bs.modal', function () { $('#edit-todo-name').trigger('focus'); });
		}
	},
	prepareAddTaskModal: function(id, name, doAutofocus) {
		App.currentlyPickedTodoId = id;
		App.currentlyPickedTodoName = name;

		$('#add-task-title').html(addTaskModalTitlePrefix + name);

		if (doAutofocus)
		{
			$('#addTaskModal').on('shown.bs.modal', function () { $('#add-task-name').trigger('focus'); });
		}
	},
	fetchTasks: function (todo2, disableRemoveAllButtons, successCallback)
	{
		var temp_id = todo2.id;
		var url2 = App.baseurl + "/todo/" + temp_id + "/tasks";

		if (App.doTaskSorting)
			url2 += "/sorted/" + App.fetchTaskSortingDirection + "/" + App.fetchTaskSortingProperty;

		$.ajax({
			url: url2,
			success: function(result){
				var task_count = result.length;
				var no_tasks;

				if (successCallback !== undefined)
				{
					successCallback();
				}

				no_tasks = (App.task_overall_count == 0);

				if (no_tasks)
				{
					App.deleteTaskSorting(true);
				}

				$("#task-sorting-toolbar-1").prop("disabled", no_tasks);
				$("#task-sorting-toolbar-2").prop("disabled", no_tasks);

				for (var j = 0; j < task_count; j++) {
					$('#task-list-container-' + temp_id).append(
						[{task: result[j], todo: todo2}].map(App.tplTask)
					);
					$('#task-done-checkbox-' + result[j].id).prop('checked', result[j].done);
				}
			},
		});

	},
	fetchTodos: function (disableRemoveAllButtons, successCallback) {
		var url2=App.baseurl + "/todo";

		if (App.doTodoSorting)
			url2 += "/sorted/" + App.fetchTodoSortingDirection + "/" + App.fetchTodoSortingProperty;

		$.ajax({
			url: url2,
			success: function(result){
				var todo_count=result.length;
				var no_todos=(result.length == 0);

				if (successCallback !== undefined)
				{
					successCallback();
				}

				if (no_todos)
				{
					App.deleteTodoSorting(true);
					App.deleteTaskSorting(true);
				}

				$("#todo-sorting-toolbar-1").prop("disabled", no_todos);
				$("#todo-sorting-toolbar-2").prop("disabled", no_todos);
				$("#task-sorting-toolbar-1").prop("disabled", no_todos);
				$("#task-sorting-toolbar-2").prop("disabled", no_todos);

				for (var i = 0; i < App.PHASE_CNT; i++) {
					$('#todo-container-phase' + i).empty();
				}

				if (disableRemoveAllButtons)
					App.checkRemoveButton('all-todos', todo_count);

				App.task_overall_count = 0;

				for (var i = 0; i < todo_count; i++) {
					var temp_id = result[i].id;
					var temp_phase = result[i].phase;

					var temp_tasks = result[i].tasks;
					var task_count = temp_tasks.length;

					var temp_date_created;
					var temp_description_length;

					App.task_overall_count += task_count;

					if (App.doTodoSorting)
					{
						if (App.fetchTodoSortingProperty.substring(0,4) == "date")
							temp_date_created = result[i].dateCreated;

						if (App.fetchTodoSortingProperty == "descriptionLength")
							temp_description_length = result[i].descriptionLength;
					}

					$('#todo-container-phase' + result[i].phase).append(
						[{id: temp_id, name: result[i].name, description: result[i].description, phase: temp_phase,
							dateModified: result[i].dateModified, dateCreated: temp_date_created,
							descriptionLength: temp_description_length}].map(App.tplCard)
					);

					if (disableRemoveAllButtons)
						App.checkRemoveButton('all-tasks-for-todo-' + temp_id, task_count);

					if (App.doTodoSorting && (App.fetchTodoSortingProperty == "taskCount"))
						$('#task-list-counter-' + temp_id).html("Task count: " + task_count);

					App.fetchTasks(result[i], disableRemoveAllButtons, function() {});

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
				App.fetchTodos(App.checkIfRemoveAllButtonsCanBeDisabled, undefined);
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
				App.fetchTodos(App.checkIfRemoveAllButtonsCanBeDisabled, undefined);
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
				App.fetchTodos(App.checkIfRemoveAllButtonsCanBeDisabled, undefined);
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
				App.fetchTodos(App.checkIfRemoveAllButtonsCanBeDisabled, undefined);
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
				App.fetchTodos(App.checkIfRemoveAllButtonsCanBeDisabled, undefined);
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
				App.fetchTodos(App.checkIfRemoveAllButtonsCanBeDisabled, undefined);
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
				App.fetchTodos(App.checkIfRemoveAllButtonsCanBeDisabled, undefined);
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
				App.fetchTodos(App.checkIfRemoveAllButtonsCanBeDisabled, undefined);
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

		$('#modal-container').append(
			[{modalPrefix: 'todo', modalTitle: 'Sorting Todos',
				submitButtonCaption: 'Sort', dismissButtonCaption: 'Cancel'}].map(App.tplSortingModal)
		);

		App.todoSortingFields.forEach(function(value, key, map) {
			var result=`
				<div class="form-check">
					<input class="form-check-input" type="radio" name="todo-sorting-fields" id="todo-sorting-field-${key}" value="${key}">
					<label class="form-check-label" for="todo-sorting-field-${key}"
							data-toggle="tooltip" data-placement="bottom" title="Sort Todos by '${value}'">
						${value}
					</label>
				</div>
			`
			$('#todo-sorting-field-group').append(result);
		});
		$('#todo-sorting-modal-submit-button').click(function(e) { App.submitTodoSorting(); });
		$('#todo-sorting-field-group').keypress(function(e) { App.submitTodoSorting(); });
		$('#todo-sorting-direction-group').keypress(function(e) { App.submitTodoSorting(); });

		$('#modal-container').append(
			[{modalPrefix: 'task', modalTitle: 'Sorting Tasks',
				submitButtonCaption: 'Sort', dismissButtonCaption: 'Cancel'}].map(App.tplSortingModal)
		);

		App.taskSortingFields.forEach(function(value, key, map) {
			var result=`
				<div class="form-check">
					<input class="form-check-input" type="radio" name="task-sorting-fields" id="task-sorting-field-${key}" value="${key}">
					<label class="form-check-label" for="task-sorting-field-${key}"
							data-toggle="tooltip" data-placement="bottom" title="Sort Tasks by '${value}'">
						${value}
					</label>
				</div>
			`
			$('#task-sorting-field-group').append(result);
		});
		$('#task-sorting-modal-submit-button').click(function(e) { App.submitTaskSorting(); });
		$('#task-sorting-field-group').keypress(function(e) { App.submitTaskSorting(); });
		$('#task-sorting-direction-group').keypress(function(e) { App.submitTaskSorting(); });
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
	displayTodoSorting: function()
	{
			if (App.doTodoSorting)
			{
				$('#display-active-todo-sorting-prop').html("'" + App.todoSortingFields.get(App.fetchTodoSortingProperty) + "'");
				$('#display-active-todo-sorting-dir').html(App.fetchTodoSortingDirection + 'ending');
			}
			else
			{
				$('#display-active-todo-sorting-prop').html("");
				$('#display-active-todo-sorting-dir').html("");
			}
	},
	displayTaskSorting: function()
	{
			if (App.doTaskSorting)
			{
				$('#display-active-task-sorting-prop').html("'" + App.taskSortingFields.get(App.fetchTaskSortingProperty) + "'");
				$('#display-active-task-sorting-dir').html(App.fetchTaskSortingDirection + 'ending');
			}
			else
			{
				$('#display-active-task-sorting-prop').html("");
				$('#display-active-task-sorting-dir').html("");
			}
	},
	submitTodoSorting: function()
	{
		App.fetchTodoSortingDirection = $('input[name=todo-sorting-direction]:checked').val();
		App.fetchTodoSortingProperty = $('input[name=todo-sorting-fields]:checked').val();

		App.doTodoSorting = ((App.fetchTodoSortingDirection !== undefined) && (App.fetchTodoSortingProperty !== undefined));

		App.displayTodoSorting();

		App.fetchTodos(App.checkIfRemoveAllButtonsCanBeDisabled, function() {
			App.dismissModal('todo-sorting');
		});

		if ((App.fetchTodoSortingDirection !== undefined) && (App.fetchTodoSortingProperty !== undefined))
			$.growl.notice({message: 'Todo sorting set up successfully.'});
		else
			$.growl.error({message: 'Failed to set up sorting for Todos!'});
	},
	submitTaskSorting: function()
	{
		App.fetchTaskSortingDirection = $('input[name=task-sorting-direction]:checked').val();
		App.fetchTaskSortingProperty = $('input[name=task-sorting-fields]:checked').val();

		App.doTaskSorting = ((App.fetchTaskSortingDirection !== undefined) && (App.fetchTaskSortingProperty !== undefined));

		App.displayTaskSorting();

		App.fetchTodos(App.checkIfRemoveAllButtonsCanBeDisabled, function() {
			App.dismissModal('task-sorting');
		});

		if ((App.fetchTaskSortingDirection !== undefined) && (App.fetchTaskSortingProperty !== undefined))
			$.growl.notice({message: 'Task sorting set up successfully.'});
		else
			$.growl.error({message: 'Failed to set up sorting for Tasks!'});
	},
	deleteTodoSorting: function(noFeedback)
	{
		App.fetchTodoSortingDirection = undefined;
		App.fetchTodoSortingProperty = undefined;

		App.doTodoSorting = false;

		if (!noFeedback)
		{
			App.fetchTodos(App.checkIfRemoveAllButtonsCanBeDisabled, undefined);

			$.growl.notice({message: 'Todo sorting set back to default.'});
		}

		App.displayTodoSorting();
	},
	deleteTaskSorting: function(noFeedback)
	{
		App.fetchTaskSortingDirection = undefined;
		App.fetchTaskSortingProperty = undefined;

		App.doTaskSorting = false;

		if (!noFeedback)
		{
			App.fetchTodos(App.checkIfRemoveAllButtonsCanBeDisabled, undefined);

			$.growl.notice({message: 'Task sorting set back to default.'});
		}

		App.displayTaskSorting();
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
	},
	prepareTodoSortingModal: function()
	{
		if (App.fetchTodoSortingProperty !== undefined)
		{
			$('#todo-sorting-field-' + App.fetchTodoSortingProperty).prop("checked", true);
		}
		else
		{
			$('#todo-sorting-field-name').prop("checked", true);
		}
		if (App.fetchTodoSortingDirection !== undefined)
		{
			$('#todo-sorting-direction-' + App.fetchTodoSortingDirection).prop("checked", true);
		}
		else
		{
			$('#todo-sorting-asc').prop("checked", true);
		}
	},
	prepareTaskSortingModal: function()
	{
		if (App.fetchTaskSortingProperty !== undefined)
		{
			$('#task-sorting-field-' + App.fetchTaskSortingProperty).prop("checked", true);
		}
		else
		{
			$('#task-sorting-field-name').prop("checked", true);
		}
		if (App.fetchTaskSortingDirection !== undefined)
		{
			$('#task-sorting-direction-' + App.fetchTaskSortingDirection).prop("checked", true);
		}
		else
		{
			$('#task-sorting-asc').prop("checked", true);
		}
	}
}

$(document).ready(function () {
	App.prepareModals();
	App.fetchTodos(App.checkIfRemoveAllButtonsCanBeDisabled, undefined);
});
