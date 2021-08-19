var app = angular.module('TodoMainApp', ["ngRoute"]);
app.config(function($routeProvider) {
	$routeProvider
			.when('/', {
				templateUrl: '/app/views/todo-list-tpl.html',
				controller: 'TodoListController'
			})
			.when('/todo/add', {
				templateUrl: '/app/views/todo-add-tpl.html',
				controller: 'TodoAddController'
			})
			.when('/todo', {
				templateUrl: '/app/views/todo-details-tpl.html',
				controller: 'TodoDetailController'
			})
			.when('/task/add', {
				templateUrl: '/app/views/task-add-tpl.html',
				controller: 'TaskAddController'
			})
			.when('/task', {
				templateUrl: '/app/views/task-details-tpl.html',
				controller: 'TaskDetailController'
			})
			.otherwise({
				redirectTo: '/'
			});
});
