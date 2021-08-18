app.directive("tszTodoListPerPhase", function()
{
    return {
        restrict: 'E',
        scope:
        {
            content: '@',
        },
        controller: function($scope)
        {
            $scope.todoSubList = JSON.parse($scope.content);
            $scope.todoSubListNotEmpty = $scope.content.length > 0;
        },
        templateUrl: "app/directives/todo_phase_tpl.html"
    };
});
