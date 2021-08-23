app.controller('TaskRemoveAllController', function($scope, $location, $routeParams, TodoCardService)
{
	var id = Number.parseInt($routeParams.id);
	$scope.name = $routeParams.name;

	$scope.submitAction = function() {
		TodoCardService.removeAllTasksFromTodo(id)
				.then(function(response) {
					$.growl.notice({message: 'All Tasks were removed successfully from Todo (' + $scope.name + ')!'});
					$location.path("/");
				}, function(response) {
					$.growl.warning({message: 'No Tasks were removed from Todo (' + $scope.name + ').'});
				});
	}
	$scope.dismissAction = function() {
		$location.path("/");
	}
});
