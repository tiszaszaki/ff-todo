app.controller('TodoListController', function($scope, $location, GlobalService, TodoGlobalService)
{
	var todo_records = [];

	$scope.todoSortingFields = {'name': 'Todo name', 'description': 'Todo description',
			'descriptionLength': 'Todo description length', 'taskCount': 'Task count in Todo',
			'dateCreated': 'Date of Todo created', 'dateModified': 'Date of Todo updated'};

	$scope.taskSortingFields = {'name': 'Task name', 'done': 'Task checked'};

	$scope.readonlyTodo = GlobalService.todo_common_options.readonlyTodo;
	$scope.phase_labels = GlobalService.phase_labels;

	$scope.todo_sorting_field = [];
	$scope.todo_sorting_direction = [];

	$scope.task_sorting_field = [];
	$scope.task_sorting_direction = [];

	TodoGlobalService.fetchTodos().then(function (response)
	{
		todo_records = response.data;

		$scope.todo_list = [];

		$scope.todo_count = todo_records.length;
		$scope.task_count = [];

		$scope.phase_labels.forEach(function() {
			$scope.todo_list.push([]);
			$scope.task_count.push(0);
		});

		todo_records.forEach(function(value)
		{
			var id = value.id;

			$scope.todo_list[value.phase].push(value);
			$scope.task_count[value.phase] += value.tasks.length;
		});
	});

	$scope.addTodo = function() {
		$location.path('/todo/add');
	};

	$scope.removeAllTodos = function() {
		$location.path('/todo/remove-all');
	};

	$scope.updateSortingRelatedOptions = function(idx)
	{
		var fieldName = $scope.todo_sorting_field[idx];

		GlobalService.todo_common_options.showDescriptionLength = (fieldName == 'descriptionLength');
		GlobalService.todo_common_options.showDateCreated = (fieldName == 'dateCreated');
		GlobalService.todo_common_options.showTaskCount = (fieldName == 'taskCount');
	}

	$scope.resetTodoSorting = function(idx) {
		$scope.todo_sorting_field[idx] = '';
		$scope.todo_sorting_direction[idx] = false;
	};
	$scope.resetTaskSorting = function(idx) {
		$scope.task_sorting_field[idx] = '';
		$scope.task_sorting_direction[idx] = false;
	};
});
