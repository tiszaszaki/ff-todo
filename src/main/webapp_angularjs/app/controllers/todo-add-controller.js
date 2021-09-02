app.controller('TodoAddController', function($scope, $location, GlobalService, TodoGlobalService)
{
	$scope.phase_labels = GlobalService.phase_labels;
	$scope.descriptionMaxLength = GlobalService.descriptionMaxLength;

	$scope.validateFormGroup = GlobalService.validateFormGroup;

	$scope.name = "";
	$scope.description = "";
	$scope.phaseSelected = 0;

	$scope.submitAction = function() {
		return TodoGlobalService.addTodo($scope.name, $scope.description.trim(), $scope.phaseSelected)
				.then(function(response) {
					console.log('[INFO] Todo (' + $scope.name + ') added successfully!');
					$location.path("/");
				}, function(response) {
					console.log('[ERROR] Failed to add Todo (' + $scope.name + ')!');
				});
	}
	$scope.dismissAction = function() {
		$location.path("/");
	}

	//$("#add-todo-name").focus(); // TODO: replace jQuery usage
});
