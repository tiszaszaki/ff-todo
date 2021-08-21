app.controller('TodoAddController', function($scope, $http, $location, GlobalService, TodoGlobalService)
{
	$scope.phase_labels = GlobalService.phase_labels;
	$scope.descriptionMaxLength = GlobalService.descriptionMaxLength;
	$scope.submitAction = function() {
		TodoGlobalService.addTodo($scope.name, $scope.description, $('input[name=add-todo-phase]:checked').val())
				.then(function(response) {
					$.growl.notice({message: 'Todo (' + $scope.name + ') added successfully!'});
					$location.path("/");
				}, function(response) {
					$.growl.error({message: 'Failed to add Todo (' + $scope.name + ')!'});
				});
	}
	$scope.dismissAction = function() {
		$location.path("/");
	}
});
