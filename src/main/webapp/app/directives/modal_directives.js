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
            icontemplate: '@?',
            submittrigger: '&',
            dismisstrigger: '&'
        },
        controller: function($scope, $element) {
        	$element.on('shown.bs.modal', function(e) {
				$('#' + modalprefix + "-todo-name").trigger('focus');
        	});
        },
        templateUrl: "app/directives/todo_modal_tpl.html"
    };
});
