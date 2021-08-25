app.controller('TodoDetailController', function($scope, $location, $routeParams, GlobalService, TodoCardService)
{
	var id = Number.parseInt($routeParams.id);

	$scope.phase_labels = GlobalService.phase_labels;
	$scope.descriptionMaxLength = GlobalService.descriptionMaxLength;

	$scope.validateFormGroup = GlobalService.validateFormGroup;

	$scope.revertAction = function(doNotify) {
		$scope.name = $routeParams.name;
		$scope.description = $routeParams.description.trim();
		$scope.phasePrepared = Number.parseInt($routeParams.phase);
		$scope.phaseSelected = $scope.phasePrepared;

		if (doNotify)
		{
			console.log('[WARN] Changes reverted for Todo (' + $scope.name + ')!');
		}
	}

	$scope.revertAction(false);

	$scope.submitAction = function() {
		return TodoCardService.editTodo(id, $scope.name, $scope.description.trim(), $scope.phaseSelected)
				.then(function(response) {
					console.log('[INFO] Todo (' + $scope.name + ') saved successfully!');
					$location.path("/");
				}, function(response) {
					console.log('[ERROR] Failed to save Todo (' + $scope.name + ')!');
				});
	}
	$scope.dismissAction = function() {
		$location.path("/");
	}

	//$("#edit-todo-name").focus(); // TODO: replace jQuery usage
});
