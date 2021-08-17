app.directive("tszTodoModal", function()
{
    return {
        restrict: 'E',
        scope: {
            modalprefix: '@?',
            modaltitle: '@?',
            placeholdername: '@?',
            placeholderdescription: '@?',
            submitbuttoncaption: '@?',
            dismissbuttoncaption: '@?',
            modaldescriptionmaxcharcount: '@?',
            icontemplate: '@?'
        },
        templateUrl: "app/directives/todo_modal_tpl.html"
    };
});