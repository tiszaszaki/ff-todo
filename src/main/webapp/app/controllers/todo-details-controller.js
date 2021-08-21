app.controller('TodoDetailController', function($scope, $http, $location, $routeParams, GlobalService, TodoCardService)
{
	var id = Number.parseInt($routeParams.id.substr(1));

	$scope.phase_labels = GlobalService.phase_labels;
	$scope.descriptionMaxLength = GlobalService.descriptionMaxLength;

	$scope.name = $routeParams.name.substr(1);
	$scope.description = $routeParams.description.substr(1);
	$scope.phase = Number.parseInt($routeParams.phase.substr(1));

	$scope.submitAction = function() {
		TodoCardService.editTodo(id, $scope.name, $scope.description, $scope.phase)
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
});
