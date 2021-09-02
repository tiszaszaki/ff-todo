app.directive("tszTodoEmptinessIndicator", function()
{
    return {
        restrict: 'E',
        templateUrl: "assets/todo_empty_tpl.html"
    };
});

app.directive("tszTaskEmptinessIndicator", function()
{
    return {
        restrict: 'E',
        templateUrl: "assets/task_empty_tpl.html"
    };
});