app.factory('TodoGlobalService', function($http) {
    return {
    	fetchTodos: function()
    	{
    		return $http.get("/todo");
    	},
        addTodo: function(name, description, phase)
        {
            return $http.put("/todo", JSON.stringify( {
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
			return $http.delete("/todo/clear");
        }
    };
});
app.factory('TodoCardService', function($http) {
    return {
        addTaskForTodo: function(id, name)
        {
            return $http.put("/todo/" + id + "/task", JSON.stringify( {
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
        	console.log(patchBody);
            return $http.patch("/todo/" + id, JSON.stringify(patchBody), {
				contentType: "application/json; charset=utf-8",
				dataType: "json",
            });
        },

        removeTodo: function(id)
        {
        	return $http.delete("/todo/" + id);
        },
        removeAllTasksFromTodo: function(id)
        {
        	return $http.delete("/todo/" + id + "/task/clear");
        },

        shiftTodoToTheLeft: function(id, name, phase)
        {
        	return $http.patch("/todo/" + id + "/shift/left");
        },
        shiftTodoToTheRight: function(id, name, phase)
        {
        	return $http.patch("/todo/" + id + "/shift/right");
        },

        checkTask: function(id)
        {
        	return $http.patch("/task/" + id + "/check");
        },

        removeTask: function(id)
        {
        	return $http.delete("/task/" + id);
        }
    };
});
