app.factory('TodoGlobalService', function($http, GlobalService) {
	var basePath = GlobalService.basePath;
    return {
    	fetchTodos: function()
    	{
    		return $http.get(basePath + "/todo");
    	},
        addTodo: function(name, description, phase)
        {
            return $http.put(basePath + "/todo", JSON.stringify( {
				name: name,
				description: description,
				phase: phase
            }), {
				contentType: "application/json; charset=utf-8",
				dataType: "json",
            });
        },
        removeAllTodos: function()
        {
			return $http.delete(basePath + "/todo/clear");
        }
    };
});
app.factory('TodoCardService', function($http, GlobalService) {
	var basePath = GlobalService.basePath;
    return {
        addTaskForTodo: function(id, name)
        {
            return $http.put(basePath + "/todo/" + id + "/task", JSON.stringify( {
				name: name,
				done: false
            }), {
				contentType: "application/json; charset=utf-8",
				dataType: "json",
            });
        },
        editTodo: function(id, name, description, phase)
        {
        	var patchBody = {
        		name: name,
        		description: description,
        		phase: 0
        	};
        	if (phase !== undefined)
        	{
	        	patchBody.phase = phase;
        	}
            return $http.patch(basePath + "/todo/" + id, JSON.stringify(patchBody), {
				contentType: "application/json; charset=utf-8",
				dataType: "json",
            });
        },

        removeTodo: function(id)
        {
        	return $http.delete(basePath + "/todo/" + id);
        },
        removeAllTasksFromTodo: function(id)
        {
        	return $http.delete(basePath + "/todo/" + id + "/task/clear");
        },

        shiftTodoToTheLeft: function(id, name, phase)
        {
        	return $http.patch(basePath + "/todo/" + id + "/shift/left");
        },
        shiftTodoToTheRight: function(id, name, phase)
        {
        	return $http.patch(basePath + "/todo/" + id + "/shift/right");
        },

        checkTask: function(id)
        {
        	return $http.patch(basePath + "/task/" + id + "/check");
        },

        removeTask: function(id)
        {
        	return $http.delete(basePath + "/task/" + id);
        }
    };
});
