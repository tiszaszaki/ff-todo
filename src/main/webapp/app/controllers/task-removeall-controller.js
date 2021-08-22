app.controller('TaskRemoveAllController', function($scope, $http, $location, $routeParams, GlobalService, TodoCardService)
{
	var id = Number.parseInt($routeParams.id.substr(1));
	$scope.name = $routeParams.name.substr(1);

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
