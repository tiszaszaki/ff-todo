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

			$scope.readonlyTodo = options.readonlyTodo;
			$scope.readonlyTask = options.readonlyTask;

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
                $location.path('/task/add/:' + todo.id);
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
                $location.path('/task/remove-all/:' + todo.id + '/:' + todo.name);
            }

            $scope.shiftTodoLeft = function()
            {
                TodoCardService.shiftTodoToTheLeft(todo.id, todo.name, todo.phase)
                	.then(function (response) {
                		$.growl.notice({message: 'Todo (' + todo.name + ') shifted to the left successfully!'});
                		$location.path('/');
                	}, function (response) {
                		$.growl.error({message: 'Failed to shift Todo (' + todo.name + ') to the left!'});
                	});
            }

            $scope.shiftTodoRight = function()
            {
                TodoCardService.shiftTodoToTheRight(todo.id, todo.name, todo.phase)
                	.then(function (response) {
                		$.growl.notice({message: 'Todo (' + todo.name + ') shifted to the right successfully!'});
                		$location.path('/');
                	}, function (response) {
                		$.growl.error({message: 'Failed to shift Todo (' + todo.name + ') to the right!'});
                	});
            }

			$scope.checkTask = function(task)
			{
				TodoCardService.checkTask(task.id)
					.then(function (response) {
						$.growl.notice({message: 'Task (' + task.name + ') checked successfully!'});
						$location.path('/');
                	}, function (response) {
                		$.growl.error({message: 'Failed to check Task (' + todo.name + ')!'});
                	});
			}

			$scope.removeTask = function(task)
			{
				TodoCardService.removeTask(task.id)
					.then(function (response) {
						$.growl.notice({message: 'Task (' + task.name + ') removed successfully!'});
						$location.path('/');
                	}, function (response) {
                		$.growl.error({message: 'Failed to remove Task (' + todo.name + ')!'});
                	});
			}
        },
        templateUrl: "app/directives/card_tpl.html"
    };
});
