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
            console.log("Trying to remove all Todos...");
        }
    };
});
app.factory('TodoCardService', function($http) {
    return {
        addTaskForTodo: function(id, name)
        {
            console.log(
                `Trying to add Task for Todo with name "${name}" and ID ${id}...`
            );
        },
        editTodo: function(id, name, description, phase)
        {
        	console.log(phase);
            return $http.patch("/todo/" + id, JSON.stringify( {
				name: name,
				description: description,
				phase: phase
            }), {
				contentType: "application/json; charset=utf-8",
				dataType: "json",
            });
        },

        removeTodo: function(id)
        {
        	return $http.delete("/todo/" + id);
        },
        removeAllTasksFromTodo: function(id, name)
        {
            console.log(
                `Trying to remove all Tasks from Todo with name "${name}" and ID ${id}...`
            );
        },

        shiftTodoToTheLeft: function(id, name, phase)
        {
        	return $http.patch("/todo/" + id + "/shift/left");
        },
        shiftTodoToTheRight: function(id, name, phase)
        {
        	return $http.patch("/todo/" + id + "/shift/right");
        }
    };
});
