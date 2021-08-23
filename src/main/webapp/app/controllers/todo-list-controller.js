app.controller('TodoListController', function($scope, $location, orderByFilter, GlobalService, TodoGlobalService)
{
	var todo_records = [];

	$scope.todo_expand_status = new Map([]);

	$scope.todoSortingFields = {'name': 'Todo name', 'description': 'Todo description',
			'descriptionLength': 'Todo description length', 'taskCount': 'Task count in Todo',
			'dateCreated': 'Date of Todo created', 'dateModified': 'Date of Todo updated'},

	$scope.readonlyTodo = GlobalService.todo_common_options.readonlyTodo;
	$scope.phase_labels = GlobalService.phase_labels;

	$scope.todo_sorting_field = [];
	$scope.todo_sorting_direction = [];

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
			var id = value.id;

			$scope.todo_list[value.phase].push(value);
			$scope.todo_expand_status[id] = false;

			$("#task-list-" + id + "-collapse").on("show.bs.collapse", function() {
				console.log($scope.todo_expand_status[id]);
				$scope.todo_expand_status[id] = true;
			});
			$("#task-list-" + id + "-collapse").on("hide.bs.collapse", function() {
				console.log($scope.todo_expand_status[id]);
				$scope.todo_expand_status[id] = false;
			});
		});
	});

	$scope.addTodo = function() {
		$location.path('/todo/add');
	};

	$scope.removeAllTodos = function() {
		$location.path('/todo/remove-all');
	};
});
