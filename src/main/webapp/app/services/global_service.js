app.factory('GlobalService', function() {
	var _phase_labels = ['Todo', 'In progress', 'Done'];
	var _phaseNum = _phase_labels.length;

	return {
		phase_labels: _phase_labels,
		phaseNum: _phaseNum,
		todo_common_options: {
			showDescriptionLength: true,
			showDateCreated: true,
			showTaskCount: true
		},
		descriptionMaxLength: 1024
    }
});
