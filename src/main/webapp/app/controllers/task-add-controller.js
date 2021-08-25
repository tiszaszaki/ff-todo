app.controller('TaskAddController', function($scope, $location, $routeParams, GlobalService, TodoCardService)
{
	var id = Number.parseInt($routeParams.id);

	$scope.validateFormGroup = GlobalService.validateFormGroup;

	$scope.submitAction = function() {
		return TodoCardService.addTaskForTodo(id, $scope.name)
				.then(function(response) {
					$.growl.notice({message: 'Task (' + $scope.name + ') added successfully!'});
					$location.path("/");
				}, function(response) {
					$.growl.error({message: 'Failed to add Task (' + $scope.name + ')!'});
				});
	}
	$scope.dismissAction = function() {
		$location.path("/");
	}

	$("#add-task-name").focus();
});
