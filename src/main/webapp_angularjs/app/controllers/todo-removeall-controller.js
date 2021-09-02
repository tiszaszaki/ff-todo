app.controller('TodoRemoveAllController', function($scope, $location, TodoGlobalService)
{
	$scope.submitAction = function() {
		TodoGlobalService.removeAllTodos()
				.then(function(response) {
					console.log('[INFO] All Todos were removed successfully from the board!');
					$location.path("/");
				}, function(response) {
					console.log('[ERROR] No Todos were removed from the board.');
				});
	}
	$scope.dismissAction = function() {
		$location.path("/");
	}
});
