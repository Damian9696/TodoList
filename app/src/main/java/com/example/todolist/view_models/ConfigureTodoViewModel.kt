package com.example.todolist.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.data.Todo
import com.example.todolist.data.repository.FirestoreTodoListRepository
import com.example.todolist.utils.Resource

class ConfigureTodoViewModel(private val firestoreTodoListRepository: FirestoreTodoListRepository) :
    ViewModel() {
    private val _resource = MutableLiveData<Resource<Todo>>()
    val resource: LiveData<Resource<Todo>> get() = _resource

    fun addTodo(title: String, description: String, iconUrl: String): LiveData<Resource<Todo>> {
        _resource.value = firestoreTodoListRepository.addTodo(title, description, iconUrl).value
        return resource
    }

    fun updateTodo(todo: Todo): LiveData<Resource<Todo>> {
        _resource.value = firestoreTodoListRepository.updateTodo(todo).value
        return resource
    }
}