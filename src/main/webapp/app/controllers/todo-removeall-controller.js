app.controller('TodoRemoveAllController', function($scope, $location, TodoGlobalService)
{
	$scope.submitAction = function() {
		TodoGlobalService.removeAllTodos()
				.then(function(response) {
					$.growl.notice({message: 'All Todos were removed successfully from the board!'});
					$location.path("/");
				}, function(response) {
					$.growl.warning({message: 'No Todos were removed from the board.'});
				});
	}
	$scope.dismissAction = function() {
		$location.path("/");
	}
});
