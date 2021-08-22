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
			.when('/todo/edit/:id/:name/:description/:phase', {
				templateUrl: '/app/views/todo-details-tpl.html',
				controller: 'TodoDetailController'
			})
			.when('/todo/remove/:id/:name', {
				templateUrl: '/app/views/todo-remove-tpl.html',
				controller: 'TodoRemoveController'
			})
			.when('/todo/remove-all', {
				templateUrl: '/app/views/todo-removeall-tpl.html',
				controller: 'TodoRemoveAllController'
			})
			.when('/task/add/:id', {
				templateUrl: '/app/views/task-add-tpl.html',
				controller: 'TaskAddController'
			})
			.when('/task/remove-all/:id/:name', {
				templateUrl: '/app/views/task-removeall-tpl.html',
				controller: 'TaskRemoveAllController'
			})
			.when('/task', {
				templateUrl: '/app/views/task-details-tpl.html',
				controller: 'TaskDetailController'
			})
			.otherwise({
				redirectTo: '/'
			});
});
