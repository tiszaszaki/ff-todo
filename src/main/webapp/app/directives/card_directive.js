app.directive("tszCardInvalid", function()
{
    return {
        restrict: 'E',
        templateUrl: "app/directives/card_invalid_tpl.html"
    };
});

app.directive("tszCard", function($location, GlobalService, TodoCardService)
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
            var phasenum = GlobalService.phaseNum;

			$scope.id = todo.id;
			$scope.name = todo.name;
			$scope.description = todo.description;
			$scope.phase = todo.phase;
			$scope.datemodified = moment(todo.dateModified).fromNow();
			$scope.datecreated = moment(todo.dateCreated).fromNow();
			$scope.tasks = todo.tasks;

			$scope.id2 = Number.parseInt($scope.id) + 1;

            $scope.phaseLeftExists = (($scope.phase-1) >= 0);
            $scope.phaseRightExists = (($scope.phase+1) < phasenum);

            $scope.isCardValid = (true
                && ($scope.name != ""));

			if ($scope.description)
				$scope.descriptionLength = $scope.description.length;
			else
				$scope.descriptionLength = 0;

			$scope.taskCount = $scope.tasks.length;

            if (options)
            {
				$scope.showDateCreated = (options.showDateCreated && $scope.datecreated);
				$scope.showDescriptionLength = (options.showDescriptionLength && ($scope.descriptionLength > 0));
				$scope.showTaskCount = (options.showTaskCount && ($scope.taskCount > 0));
            }
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
                $location.path('/todo/edit/:' + todo.id + '/:' + todo.name + '/:' + todo.description + '/:' + todo.phase);
            }

            $scope.prepareRemoveTodoConfirmModal = function()
            {
                $location.path('/todo/remove/:' + todo.id + '/:' + todo.name);
            }

            $scope.prepareRemoveAllTasksConfirmModal = function()
            {
                TodoCardService.removeAllTasksFromTodo(todo.id, todo.name);
            }

            $scope.shiftTodoLeft = function()
            {
                TodoCardService.shiftTodoToTheLeft(todo.id, todo.name, todo.phase)
                	.then(function (response) {
                		$.growl.notice({message: 'Todo (' + todo.name + ') shifted to the left successfully!'});
                		$location.path('/');
                	});
            }

            $scope.shiftTodoRight = function()
            {
                TodoCardService.shiftTodoToTheRight(todo.id, todo.name, todo.phase)
                	.then(function (response) {
                		$.growl.notice({message: 'Todo (' + todo.name + ') shifted to the right successfully!'});
                		$location.path('/');
                	});
            }
        },
        templateUrl: "app/directives/card_tpl.html"
    };
});
