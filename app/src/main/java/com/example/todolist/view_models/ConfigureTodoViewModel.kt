package com.example.todolist.view_models

import androidx.lifecycle.*
import com.example.todolist.data.Todo
import com.example.todolist.data.repository.FirestoreTodoListRepository
import com.example.todolist.utils.ConfigureAction
import com.example.todolist.utils.Response

class ConfigureTodoViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val firestoreTodoListRepository: FirestoreTodoListRepository,
) : ViewModel() {

    private val todoForUpdateArgs: Todo
        get() = savedStateHandle.get<Todo>(TODO_KEY)!!

    private val configureActionArgs: ConfigureAction
        get() = savedStateHandle.get<ConfigureAction>(CONFIGURE_ACTION_KEY)!!

    private val _configureAction = MutableLiveData(configureActionArgs)
    val configureAction: LiveData<ConfigureAction> get() = _configureAction

    private val _todoForUpdate = MutableLiveData(todoForUpdateArgs)

    val todoForUpdateIfConfigureActionIsUpdate = Transformations.map(_todoForUpdate) {
        if (configureActionArgs == ConfigureAction.UPDATE) {
            return@map _todoForUpdate.value
        } else {
            return@map null
        }
    }

    fun addTodo(title: String, description: String, iconUrl: String): LiveData<Response<Todo>> {
        return firestoreTodoListRepository.addTodo(title, description, iconUrl)
    }

    fun updateTodo(
        title: String,
        description: String,
        iconUrl: String
    ): LiveData<Response<Todo>> {
        return firestoreTodoListRepository.updateTodo(
            todoForUpdateArgs.id!!,
            title,
            description,
            iconUrl,
            todoForUpdateArgs.timestamp!!
        )
    }

    companion object {
        const val CONFIGURE_ACTION_KEY = "configureAction"
        const val TODO_KEY = "todo"
    }
}