app.controller('TodoRemoveController', function($scope, $location, $routeParams, TodoCardService)
{
	var id = Number.parseInt($routeParams.id.substr(1));
	$scope.name = $routeParams.name.substr(1);

	$scope.submitAction = function() {
		TodoCardService.removeTodo(id)
				.then(function(response) {
					$.growl.notice({message: 'Todo (' + $scope.name + ') removed successfully!'});
					$location.path("/");
				}, function(response) {
					$.growl.error({message: 'Failed to remove Todo (' + $scope.name + ')!'});
				});
	}
	$scope.dismissAction = function() {
		$location.path("/");
	}
});
