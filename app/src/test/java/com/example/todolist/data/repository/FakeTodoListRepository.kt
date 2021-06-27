package com.example.todolist.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todolist.data.Operation
import com.example.todolist.data.Todo
import com.example.todolist.data.TodoListLiveData
import com.example.todolist.interfaces.TodoListRepository
import com.example.todolist.utils.Response
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentChange.Type.ADDED

class FakeTodoListRepository : TodoListRepository {

    private val mutableList = mutableListOf(
        Operation(Todo("0", "Title0", "Description0", "IconUrl0", "0"), ADDED),
        Operation(Todo("1", "Title1", "Description1", "IconUrl1", "1"), ADDED),
        Operation(Todo("2", "Title2", "Description2", "IconUrl2", "2"), ADDED),
        Operation(Todo("3", "Title3", "Description3", "IconUrl3", "3"), ADDED),
        Operation(Todo("4", "Title4", "Description4", "IconUrl4", "4"), ADDED),
    )

    private val _mutableOperations = MutableLiveData<Operation>()
    private val mutableOperations: LiveData<Operation> get() = _mutableOperations

    init {
        mutableList.forEach { operation ->
            _mutableOperations.value = operation
        }
    }

    override fun getTodoListLiveData(): TodoListLiveData? {
        TODO("Not yet implemented")
    }

    override fun addTodo(
        title: String,
        description: String,
        iconUrl: String
    ): LiveData<Response<Todo>> {
        TODO("Not yet implemented")
    }

    override fun updateTodo(
        id: String,
        title: String,
        description: String,
        iconUrl: String,
        timestamp: String
    ): LiveData<Response<Todo>> {
        TODO("Not yet implemented")
    }

    override fun deleteTodo(todo: Todo): LiveData<Response<Todo>> {
        TODO("Not yet implemented")
    }
}