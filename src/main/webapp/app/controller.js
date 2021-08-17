app.controller('todoCtrl', function($scope, $rootScope, $http, TodoGlobalService)
{
	$scope.phaseNum = 3;
	$scope.todo_phases = ["Todo", "In progress", "Done"];

    $rootScope.todo_common_options = {
        showDescriptionLength: true,
        showDateCreated: true,
        showTaskCount: true
    };

	$scope.todo_list = [[], [], []];
	$scope.todo_list_count_per_phase = [
			{count: 0, exist: false},
			{count: 0, exist: false},
			{count: 0, exist: false}
	];

	TodoGlobalService.fetchTodos().then(function (response)
	{
		response.data.forEach(function(value)
		{
			var temp_value=value;
			var phase=value.phase;

			temp_value.dateModified = moment(temp_value.dateModified).fromNow();
			temp_value.dateCreated = moment(temp_value.dateCreated).fromNow();

			$scope.todo_list[phase].push(temp_value);

			if ((phase >= 0) && (phase < $scope.phaseNum))
			{
				$scope.todo_list_count_per_phase[phase].count++;
				$scope.todo_list_count_per_phase[phase].exist =
					($scope.todo_list_count_per_phase[phase].count > 0);
			}
		});
	});

	$scope.todo_list_count = 0;
	$scope.todo_list.forEach(function(value) { $scope.todo_list_count += value.length });

	$scope.addTodo = function() {
		TodoGlobalService.addTodo();
	};

	$scope.removeAllTodos = function() {
		TodoGlobalService.removeAllTodos();
	};
});
