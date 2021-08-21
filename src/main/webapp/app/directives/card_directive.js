app.directive("tszCardInvalid", function()
{
    return {
        restrict: 'E',
        templateUrl: "app/directives/card_invalid_tpl.html"
    };
});

app.directive("tszCard", function(GlobalService)
{
    return {
        restrict: 'E',
        scope: {
        	content: '@',
        },
        controller: function ($scope)
        {
            var options = GlobalService.todo_common_options;
            var todo = JSON.parse($scope.content);

			$scope.id = todo.id;
			$scope.name = todo.name;
			$scope.description = todo.description;
			$scope.phase = todo.phase;
			$scope.datemodified = moment(todo.dateModified).fromNow();
			$scope.datecreated = moment(todo.dateCreated).fromNow();
			$scope.tasks = todo.tasks;

			$scope.id2 = Number.parseInt($scope.id) + 1;

            $scope.isCardValid = (true
                && ($scope.name != ""));

			$scope.descriptionLength = $scope.description.length;
			$scope.taskCount = $scope.tasks.length;

            if (options)
            {
				$scope.showDateCreated = (options.showDateCreated && $scope.datecreated);
				$scope.showDescriptionLength = (options.showDescriptionLength && ($scope.descriptionLength > 0));
				$scope.showTaskCount = (options.showTaskCount && ($scope.taskCount > 0));
            }
        },
        templateUrl: "app/directives/card_tpl.html"
    };
});

app.directive("tszCardToolbar", function($location, GlobalService, TodoCardService)
{
    return {
        restrict: 'E',
        scope:
        {
        	content: '@'
        },
        controller: function($scope)
        {
        	/*
            var todo = JSON.parse($scope.content);
            var phase = todo.phase;
            var phasenum = GlobalService.phaseNum;

            var phase_left=phase-1;
            var phase_right=phase+1;

            $scope.phaseLeftExists = (phase_left >= 0);
            $scope.phaseRightExists = (phase_right < phasenum);
            */

            $scope.phaseLeftExists = true;
            $scope.phaseRightExists = true;
        },
        link: function($scope)
        {
            var todo = JSON.parse($scope.content);

            $scope.prepareAddTaskModal = function()
            {
                TodoCardService.addTaskForTodo(todo.id, todo.name);
            }

            $scope.prepareEditTodoModal = function()
            {
                TodoCardService.editTodo(todo.id, todo.name, todo.description, todo.phase);
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
                TodoCardService.shiftTodoToTheLeft($scope.id, $scope.name, phase)
                	.then(function (response) {
                		$.growl.notice({message: 'Todo (' + $scope.name + ') shifted to the left successfully!'});
                		$location.path('/');
                	});
            }

            $scope.shiftTodoRight = function()
            {
                TodoCardService.shiftTodoToTheRight($scope.id, $scope.name, phase)
                	.then(function (response) {
                		$.growl.notice({message: 'Todo (' + $scope.name + ') shifted to the right successfully!'});
                		$location.path('/');
                	});
            }
        },
        templateUrl: "app/directives/card_toolbar_tpl.html"
    };
});
