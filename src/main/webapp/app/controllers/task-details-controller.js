app.controller('TaskDetailsController', function($scope, $location, $routeParams, GlobalService, TodoGlobalService)
{
	var id = Number.parseInt($routeParams.id);

	$scope.validateFormGroup = GlobalService.validateFormGroup;

	$scope.revertAction = function(doNotify) {
		var _done=$routeParams.done.toLowerCase();

		$scope.name = $routeParams.name;
		if ((_done == "false") || (_done == "true"))
		{
			$scope.done = JSON.parse(_done);
		}

		if (doNotify)
		{
			console.log('[INFO] Changes reverted for Todo (' + $scope.name + ')!');
		}
	}

	$scope.revertAction(false);

	$scope.dismissAction = function() {
		$location.path("/");
	}
});
