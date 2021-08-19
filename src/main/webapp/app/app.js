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
			/*
			.when('/todo', {
				templateUrl: '/todo-details-tpl.html',
				controller: ''
			})
			.when('/task/add', {
				templateUrl: '/task-add-tpl.html',
				controller: ''
			})
			.when('/task', {
				templateUrl: '/task-details-tpl.html',
				controller: ''
			})
			*/
			.otherwise({
				redirectTo: '/'
			});
});
