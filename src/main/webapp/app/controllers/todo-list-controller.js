app.controller('TodoListController', function($scope, $rootScope, $http, $location, GlobalService, TodoGlobalService)
{
	$scope.phase_labels = GlobalService.phase_labels;

	$rootScope.todoRefresh = function()
	{
		$scope.todo_list = [[], [], []];

		TodoGlobalService.fetchTodos().then(function (response)
		{
			response.data.forEach(function(value)
			{
				$scope.todo_list[value.phase].push(value);
			});
		});
	}

	$rootScope.todoRefresh();

	$scope.addTodo = function() {
		$location.path('/todo/add');
	};

	$scope.removeAllTodos = function() {
	};
});
