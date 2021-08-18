app.directive("tszCardInvalid", function()
{
    return {
        restrict: 'E',
        templateUrl: "app/directives/card_invalid_tpl.html"
    };
});

app.directive("tszCard", function()
{
    return {
        restrict: 'E',
        scope: {
        	id: '@',
        	name: '@',
        	description: '@',
        	phase: '@',
        	datemodified: '@',
        	datecreated: '@',
        	tasks: '@',
            phasenum: '@',
        },
        controller: function ($scope, $rootScope)
        {
            var options = $rootScope.todo_common_options;

			$scope.id2 = Number.parseInt($scope.id) + 1;
			$scope.tasks2 = JSON.parse($scope.tasks);

            $scope.isCardValid = (true
                && ($scope.name != ""));

			$scope.taskCount = $scope.tasks2.length;

            if (options)
            {
				$scope.showDateCreated = (options.showDateCreated && $scope.datecreated);

                if (options.showDescriptionLength && $scope.description)
                {
                    $scope.descriptionLength = $scope.description.length;
                }

				$scope.showTaskCount = true || (options.showTaskCount && $scope.taskCount);
            }
        },
        templateUrl: "app/directives/card_tpl.html"
    };
});

app.directive("tszCardToolbar", function($rootScope, TodoCardService)
{
    return {
        restrict: 'E',
        scope:
        {
            id: '@',
            name: '@',
            phase: '@',
            phasenum: '@'
        },
        controller: function($scope, $rootScope)
        {
            var _phase = Number.parseInt($scope.phase);
            var _phasenum = Number.parseInt($scope.phasenum);

            var phase_left=_phase-1;
            var phase_right=_phase+1;

            $scope.phaseLeftExists = (phase_left >= 0);
            $scope.phaseRightExists = (phase_right < _phasenum);
        },
        link: function($scope)
        {
            var _phase = Number.parseInt($scope.phase);

            $scope.prepareAddTaskModal = function()
            {
                TodoCardService.addTaskForTodo($scope.id, $scope.name);
            }

            $scope.prepareEditTodoModal = function()
            {
                TodoCardService.editTodo($scope.id, $scope.name);
            }

            $scope.prepareRemoveTodoConfirmModal = function()
            {
                TodoCardService.removeTodo($scope.id, $scope.name);
            }

            $scope.prepareRemoveAllTasksConfirmModal = function()
            {
                TodoCardService.removeAllTasksFromTodo($scope.id, $scope.name);
            }

            $scope.shiftTodoLeft = function()
            {
                TodoCardService.shiftTodoToTheLeft($scope.id, $scope.name, _phase)
                	.then(function (response) {
                		$.growl.notice({message: 'Todo (' + $scope.name + ') shifted to the left successfully!'});
                		$rootScope.todoRefresh();
                	});
            }

            $scope.shiftTodoRight = function()
            {
                TodoCardService.shiftTodoToTheRight($scope.id, $scope.name, _phase)
                	.then(function (response) {
                		$.growl.notice({message: 'Todo (' + $scope.name + ') shifted to the right successfully!'});
                		$rootScope.todoRefresh();
                	});
            }
        },
        templateUrl: "app/directives/card_toolbar_tpl.html"
    };
});
