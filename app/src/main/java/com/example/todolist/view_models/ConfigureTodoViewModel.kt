package com.example.todolist.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.data.Todo
import com.example.todolist.data.repository.FirestoreTodoListRepository
import com.example.todolist.utils.ConfigureAction
import com.example.todolist.utils.Resource

class ConfigureTodoViewModel(private val firestoreTodoListRepository: FirestoreTodoListRepository) :
    ViewModel() {

    lateinit var todoForUpdate: Todo

    private val _resource = MutableLiveData<Resource<Todo>>()
    val resource: LiveData<Resource<Todo>> get() = _resource

    private val _configureAction = MutableLiveData<ConfigureAction>()
    val configureAction: LiveData<ConfigureAction> get() = _configureAction

    fun addTodo(title: String, description: String, iconUrl: String): LiveData<Resource<Todo>> {
        _resource.value = firestoreTodoListRepository.addTodo(title, description, iconUrl).value
        return resource
    }

    fun updateTodo(
        title: String,
        description: String,
        iconUrl: String
    ): LiveData<Resource<Todo>> {
        _resource.value = firestoreTodoListRepository.updateTodo(
            todoForUpdate.id!!,
            title,
            description,
            iconUrl,
            todoForUpdate.timestamp!!
        ).value
        return resource
    }

    fun setConfigureAction(configureAction: ConfigureAction) {
        _configureAction.value = configureAction
    }

    fun insertTodoForUpdate(todo: Todo) {
        todoForUpdate = todo
    }
}