app.controller('todoCtrl', function($scope, $rootScope, $http, TodoGlobalService)
{
	$rootScope.phaseNum = $scope.phaseNum = 3;

    $rootScope.todo_common_options = {
        showDescriptionLength: true,
        showDateCreated: true,
        showTaskCount: true
    };

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
		TodoGlobalService.addTodo();
	};

	$scope.removeAllTodos = function() {
		TodoGlobalService.removeAllTodos();
	};
});
