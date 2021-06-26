package com.example.todolist.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.data.Todo
import com.example.todolist.data.TodoListLiveData
import com.example.todolist.data.repository.FirestoreTodoListRepository
import com.example.todolist.utils.Response

class TodoListViewModel(private val firestoreTodoListRepository: FirestoreTodoListRepository) :
    ViewModel() {
    private val _resource = MutableLiveData<Response<Todo>>()
    val response: LiveData<Response<Todo>> get() = _resource

    fun getTodoListLiveData(): TodoListLiveData? {
        return firestoreTodoListRepository.getTodoListLiveData()
    }

    fun deleteTodo(todo: Todo): LiveData<Response<Todo>> {
        return firestoreTodoListRepository.deleteTodo(todo)
    }
}