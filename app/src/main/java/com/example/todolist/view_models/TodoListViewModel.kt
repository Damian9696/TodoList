package com.example.todolist.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.data.Todo
import com.example.todolist.data.TodoListLiveData
import com.example.todolist.data.repository.FirestoreTodoListRepository
import com.example.todolist.utils.Resource

class TodoListViewModel(private val firestoreTodoListRepository: FirestoreTodoListRepository) :
    ViewModel() {
    private val _resource = MutableLiveData<Resource<Todo>>()
    val resource: LiveData<Resource<Todo>> get() = _resource

    fun getTodoListLiveData(): TodoListLiveData? {
        return firestoreTodoListRepository.getTodoListLiveData()
    }

    fun deleteTodo(todo: Todo): LiveData<Resource<Todo>> {
        _resource.value = firestoreTodoListRepository.deleteTodo(todo).value
        return resource
    }
}