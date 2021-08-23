app.controller('TodoDetailController', function($scope, $location, $routeParams, GlobalService, TodoCardService)
{
	var id = Number.parseInt($routeParams.id);

	$scope.phase_labels = GlobalService.phase_labels;
	$scope.descriptionMaxLength = GlobalService.descriptionMaxLength;
	$scope.validateFormGroup = GlobalService.validateFormGroup;

	$scope.revertAction = function(doNotify) {
		$scope.name = $routeParams.name;
		$scope.description = $routeParams.description;
		$scope.phasePrepared = Number.parseInt($routeParams.phase);

		if (doNotify)
		{
			$.growl.warning({message: 'Changes reverted for Todo (' + $scope.name + ')!'});
		}
	}

	$scope.revertAction(false);

	$scope.submitAction = function() {
		console.log($scope.phaseSelected);
		TodoCardService.editTodo(id, $scope.name, $scope.description, $scope.phaseSelected)
				.then(function(response) {
					$.growl.notice({message: 'Todo (' + $scope.name + ') saved successfully!'});
					$location.path("/");
				}, function(response) {
					$.growl.error({message: 'Failed to save Todo (' + $scope.name + ')!'});
				});
	}
	$scope.dismissAction = function() {
		$location.path("/");
	}

	$("#edit-todo-name").focus();
});
