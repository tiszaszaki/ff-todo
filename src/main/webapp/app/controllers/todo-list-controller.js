app.controller('TodoListController', function($scope, $location, orderByFilter, GlobalService, TodoGlobalService)
{
	var todo_records = [];
	$scope.todoSortingFields = {'name': 'Todo name', 'description': 'Todo description',
			'descriptionLength': 'Todo description length', 'taskCount': 'Task count in Todo',
			'dateCreated': 'Date of Todo created', 'dateModified': 'Date of Todo updated'},

	$scope.readonlyTodo = GlobalService.todo_common_options.readonlyTodo;
	$scope.phase_labels = GlobalService.phase_labels;

	TodoGlobalService.fetchTodos().then(function (response)
	{
		todo_records = response.data;

		$scope.todo_list = [];

		$scope.todo_count = todo_records.length;

		$scope.phase_labels.forEach(function() {
			$scope.todo_list.push([]);
		});

		todo_records.forEach(function(value)
		{
			$scope.todo_list[value.phase].push(value);
		});
	});

	$scope.addTodo = function() {
		$location.path('/todo/add');
	};

	$scope.removeAllTodos = function() {
		$location.path('/todo/remove-all');
	};

	$scope.sortTodos = function(fieldName2, fieldDisp2) {
		var sorting_direction=$("#todo_sorting_direction").prop("checked");
		var sorting_dirStr="";

		if (sorting_direction)
			sorting_dirStr = "descending";
		else
			sorting_dirStr = "ascending";

		console.log("Trying to sort Todos " + sorting_dirStr + " by " + fieldName2 + " ('" + fieldDisp2 + "')...");
		console.log(todo_records);
		orderByFilter(todo_records, fieldName2, sorting_direction);
		console.log(todo_records);
		$location.path('/');
	}
});
