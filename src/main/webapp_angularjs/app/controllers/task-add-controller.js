app.controller('TaskAddController', function($scope, $location, $routeParams, GlobalService, TodoCardService)
{
	var id = Number.parseInt($routeParams.id);

	$scope.validateFormGroup = GlobalService.validateFormGroup;

	$scope.submitAction = function() {
		return TodoCardService.addTaskForTodo(id, $scope.name)
				.then(function(response) {
					console.log('[INFO] Task (' + $scope.name + ') added successfully!');
					$location.path("/");
				}, function(response) {
					console.log('[ERROR] Failed to add Task (' + $scope.name + ')!');
				});
	}
	$scope.dismissAction = function() {
		$location.path("/");
	}

	//$("#add-task-name").focus(); // TODO: replace jQuery usage
});
