app.controller('todoCtrl', function($scope, $rootScope, $http, TodoGlobalService)
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

	$scope.showModal = function(modalprefix) {
		var options = {
			closeButtonText: 'Cancel',
			actionButtonText: 'Yes',
			headerText: 'Modal Header ?',
			bodyText: 'Are you sure you want to delete?'
		};

		return TodoModalService.showModal({}, options);
	};

	$scope.addTodo = function() {
		$scope.showModal("addTodo").then(function () {
			TodoGlobalService.addTodo();
		});
	};

	$scope.removeAllTodos = function() {
		TodoGlobalService.removeAllTodos();
	};
});
