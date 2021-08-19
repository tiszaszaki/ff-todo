app.controller('TodoListController', function($scope, $rootScope, $http, $location, TodoGlobalService)
{
	$scope.phase_labels = ['Todo', 'In progress', 'Done'];
	$rootScope.phaseNum = $scope.phaseNum = $scope.phase_labels.length;

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
		$location.path('/todo/add');
	};

	$scope.removeAllTodos = function() {
	};
});
