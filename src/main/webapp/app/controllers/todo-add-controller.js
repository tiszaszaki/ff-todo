app.controller('TodoAddController', function($scope, $routeParams, $http, GlobalService, TodoGlobalService)
{
	$("#add-todo-name").val($routeParams.name);
	$("#add-todo-description").val($routeParams.description);
	$scope.phase_labels = GlobalService.phase_labels;
	console.log($scope.phase_labels);
});
