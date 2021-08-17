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
            phasenum: '@',
            datemodified: '@',
            datecreated: '@?',
            tasks: '@?'
        },
        controller: function ($scope, $rootScope)
        {
            var options = $rootScope.todo_common_options;
            $scope.isCardValid = (true
                && ($scope.name != ""));

			$scope._id = Number.parseInt($scope.id) + 1;

            if ($scope.tasks !== undefined)
            {
                $scope._tasks = JSON.parse($scope.tasks);
            }

            if (options)
            {
                if (options.showDateCreated !== undefined)
                {
                    $scope._dateCreated = (options.showDateCreated && ($scope.datecreated !== undefined));
                }

                if (options.showDescriptionLength !== undefined)
                {
                    $scope.descriptionLength = $scope.description.length;
                    $scope._descriptionLength = options.showDescriptionLength;
                }

                if (options.showTaskCount !== undefined)
                if (options.showTaskCount)
                {
                    $scope.taskCount = $scope._tasks.length;
                }
            }
        },
        templateUrl: "app/directives/card_tpl.html"
    };
});

app.directive("tszCardToolbar", function(TodoGlobalService, TodoCardService)
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
        controller: function($scope)
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
                		TodoGlobalService.fetchTodos();
                	});
            }

            $scope.shiftTodoRight = function()
            {
                TodoCardService.shiftTodoToTheRight($scope.id, $scope.name, _phase)
                	.then(function (response) {
                		$.growl.notice({message: 'Todo (' + $scope.name + ') shifted to the right successfully!'});
                		TodoGlobalService.fetchTodos();
                	});
            }
        },
        templateUrl: "app/directives/card_toolbar_tpl.html"
    };
});
