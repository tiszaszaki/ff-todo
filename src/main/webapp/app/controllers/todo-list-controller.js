app.controller('TodoListController', function($scope, $location, GlobalService, TodoGlobalService)
{
	$scope.phase_labels = GlobalService.phase_labels;

	$scope.todo_list = [[], [], []];

	TodoGlobalService.fetchTodos().then(function (response)
	{
		response.data.forEach(function(value)
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
});
