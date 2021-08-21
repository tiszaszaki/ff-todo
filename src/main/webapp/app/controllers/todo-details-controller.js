app.controller('TodoDetailController', function($scope, $rootScope, $http, TodoCardService)
{
	$scope.phase_labels = GlobalService.phase_labels;
	$scope.descriptionMaxLength = GlobalService.descriptionMaxLength;
	$scope.submitAction = function() {
		TodoCardService.editTodo($scope.name, $scope.description, $('input[name=add-todo-phase]:checked').val())
				.then(function(response) {
					$location.path("/");
				});
	}
	$scope.dismissAction = function() {
		$location.path("/");
	}
});
