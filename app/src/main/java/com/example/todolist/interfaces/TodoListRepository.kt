package com.example.todolist.interfaces

import androidx.lifecycle.LiveData
import com.example.todolist.data.BaseTodoListLiveData
import com.example.todolist.data.Todo
import com.example.todolist.data.TodoListLiveData
import com.example.todolist.utils.Response
import com.hadilq.liveevent.LiveEvent

interface TodoListRepository {
    fun getTodoListLiveData(): BaseTodoListLiveData?
    fun addTodo(title: String, description: String, iconUrl: String): LiveData<Response<Todo>>
    fun updateTodo(id:String, title: String, description: String, iconUrl: String, timestamp: String): LiveData<Response<Todo>>
    fun deleteTodo(todo: Todo): LiveData<Response<Todo>>
}