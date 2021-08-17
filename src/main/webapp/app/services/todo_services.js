app.factory('TodoGlobalService', function($http) {
    return {
    	fetchTodos: function()
    	{
    		return $http.get("/todo");
    	},
        addTodo: function()
        {
            console.log("Trying to add a Todo...");
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
        editTodo: function(id, name)
        {
            console.log(
                `Trying to edit Todo with name "${name}" and ID ${id}...`
            );
        },

        removeTodo: function(id, name)
        {
            console.log(
                `Trying to remove Todo with name "${name}" and ID ${id}...`
            );
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
