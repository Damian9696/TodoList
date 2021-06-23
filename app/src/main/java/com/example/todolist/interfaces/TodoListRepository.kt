package com.example.todolist.interfaces

import androidx.lifecycle.MutableLiveData
import com.example.todolist.data.Todo
import com.example.todolist.data.TodoListLiveData
import com.example.todolist.utils.Resource

interface TodoListRepository {
    fun getTodoListLiveData(): TodoListLiveData?
    fun addTodo(title: String, description: String, iconUrl: String): MutableLiveData<Resource<Todo>>
    fun updateTodo(id:String, title: String, description: String, iconUrl: String, timestamp: String): MutableLiveData<Resource<Todo>>
    fun deleteTodo(todo: Todo): MutableLiveData<Resource<Todo>>
}