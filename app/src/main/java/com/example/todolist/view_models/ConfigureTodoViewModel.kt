package com.example.todolist.view_models

import androidx.lifecycle.*
import com.example.todolist.data.Todo
import com.example.todolist.data.repository.BaseFirestoreTodoListRepository
import com.example.todolist.data.repository.FirestoreTodoListRepository
import com.example.todolist.utils.enums.ConfigureAction
import com.example.todolist.utils.Response
import com.example.todolist.utils.enums.ValidateAction
import com.example.todolist.utils.enums.ValidateAction.SUCCESS
import com.example.todolist.utils.zipLiveData

class ConfigureTodoViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val baseFirestoreTodoListRepository: BaseFirestoreTodoListRepository,
) : ViewModel() {

    private val todoForUpdateArgs: Todo
        get() = savedStateHandle.get<Todo>(TODO_KEY)!!

    private val configureActionArgs: ConfigureAction
        get() = savedStateHandle.get<ConfigureAction>(CONFIGURE_ACTION_KEY)!!

    private val _configureAction = MutableLiveData(configureActionArgs)
    val configureAction: LiveData<ConfigureAction> get() = _configureAction

    private val _todoForUpdate = MutableLiveData(todoForUpdateArgs)

    val updateFieldsIfConfigureActionIsUpdate = Transformations.map(_todoForUpdate) {
        if (configureActionArgs == ConfigureAction.UPDATE) {
            return@map _todoForUpdate.value
        } else {
            return@map null
        }
    }

    private val _validateTitle = MutableLiveData<ValidateAction>()
    val validateTitle: LiveData<ValidateAction> = _validateTitle

    private val _validateDescription = MutableLiveData<ValidateAction>()
    val validateDescription: LiveData<ValidateAction> = _validateDescription

    private val zipValidationResult = zipLiveData(_validateTitle, _validateDescription)

    val validationPass = Transformations.map(zipValidationResult) {
        val pass =
            zipValidationResult.value?.first == SUCCESS && zipValidationResult.value?.second == SUCCESS
        ValidationWithConfigureAction(pass, configureActionArgs)
    }

    fun addTodo(title: String, description: String, iconUrl: String): LiveData<Response<Todo>> {
        return baseFirestoreTodoListRepository.addTodo(title, description, iconUrl)
    }

    fun updateTodo(
        title: String,
        description: String,
        iconUrl: String
    ): LiveData<Response<Todo>> {
        return baseFirestoreTodoListRepository.updateTodo(
            todoForUpdateArgs.id!!,
            title,
            description,
            iconUrl,
            todoForUpdateArgs.timestamp!!
        )
    }

    fun validateTitle(title: String) {
        when {
            title.isEmpty() -> {
                _validateTitle.value = ValidateAction.TEXT_EMPTY
            }
            title.length > 30 -> {
                _validateTitle.value = ValidateAction.TEXT_TOO_LONG
            }
            else -> {
                _validateTitle.value = SUCCESS
            }
        }
    }

    fun validateDescription(description: String) {
        when {
            description.isEmpty() -> {
                _validateDescription.value = ValidateAction.TEXT_EMPTY
            }
            description.length > 300 -> {
                _validateDescription.value = ValidateAction.TEXT_TOO_LONG
            }
            else -> {
                _validateDescription.value = SUCCESS
            }
        }
    }

    companion object {
        const val CONFIGURE_ACTION_KEY = "configureAction"
        const val TODO_KEY = "todo"
    }
}

data class ValidationWithConfigureAction(val pass: Boolean, val configureAction: ConfigureAction)