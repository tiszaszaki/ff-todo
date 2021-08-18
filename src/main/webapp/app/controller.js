app.controller('todoCtrl', function($scope, $rootScope, $http, TodoGlobalService)
{
	$scope.phaseNum = 3;
	$scope.todo_phases = ["Todo", "In progress", "Done"];

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
				var temp_value=value;
				var phase=value.phase;

				temp_value.dateModified = moment(temp_value.dateModified).fromNow();
				temp_value.dateCreated = moment(temp_value.dateCreated).fromNow();

				$scope.todo_list[phase].push(temp_value);
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
