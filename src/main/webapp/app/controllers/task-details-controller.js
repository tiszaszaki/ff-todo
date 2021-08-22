app.controller('TaskDetailsController', function($scope, $location, $routeParams, GlobalService, TodoGlobalService)
{
	var id = Number.parseInt($routeParams.id.substr(1));

	$scope.validateFormGroup = GlobalService.validateFormGroup;

	$scope.revertAction = function(doNotify) {
		var _done=$routeParams.done.substr(1).toLowerCase();

		$scope.name = $routeParams.name.substr(1);
		if ((_done == "false") || (_done == "true"))
		{
			$scope.done = JSON.parse(_done);
		}

		if (doNotify)
		{
			$.growl.warning({message: 'Changes reverted for Todo (' + $scope.name + ')!'});
		}
	}

	$scope.revertAction(false);

	$scope.dismissAction = function() {
		$location.path("/");
	}
});
