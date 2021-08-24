app.factory('GlobalService', function() {
	var _phase_labels = ['Todo', 'In progress', 'Done'];
	var _phaseNum = _phase_labels.length;

	var _validateFormGroup = function(formGroupId) {
		var fromGroup = $('#' + formGroupId);
		console.log("Validating form group with ID " + formGroupId + "...");
		if(!fromGroup.hasClass("was-validated")){
			fromGroup.addClass("was-validated");
		}
	};

	return {
		phase_labels: _phase_labels,
		phaseNum: _phaseNum,
		todo_common_options: {
			showDescriptionLength: false,
			showDateCreated: false,
			showTaskCount: false,
			validateTodo: true,
			testInvalidTodo: false,
			readonlyTodo: false,
			readonlyTask: false
		},
		descriptionMaxLength: 1024,

		validateFormGroup: _validateFormGroup,
    }
});
