package com.example.todolist.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todolist.data.BaseTodoListLiveData
import com.example.todolist.data.FakeTodoListLiveData
import com.example.todolist.data.Todo
import com.example.todolist.utils.Response
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class FakeFirestoreTodoListRepository(private val firestore: FirebaseFirestore) :
    BaseFirestoreTodoListRepository(firestore) {

    override fun getTodoListLiveData(): BaseTodoListLiveData? {
        return FakeTodoListLiveData()
    }

    private val _responseLiveData = MutableLiveData<Response<Todo>>()
    private val responseLiveData: LiveData<Response<Todo>> get() = _responseLiveData

    override fun addTodo(
        title: String,
        description: String,
        iconUrl: String
    ): LiveData<Response<Todo>> {
        val todo = Todo(title = title, description = description, iconUrl = iconUrl)
        _responseLiveData.value = Response.Success(todo)
        return responseLiveData
    }

    override fun updateTodo(
        id: String,
        title: String,
        description: String,
        iconUrl: String,
        timestamp: String
    ): LiveData<Response<Todo>> {
        val todo = Todo(
            id = "0",
            title = title,
            description = description,
            iconUrl = iconUrl,
            timestamp = "0"
        )
        _responseLiveData.value = Response.Success(todo)
        return responseLiveData
    }

    override fun deleteTodo(todo: Todo): LiveData<Response<Todo>> {
        _responseLiveData.value = Response.Success(todo)
        return responseLiveData
    }

    override fun setLastVisibleTodo(lastVisibleTodo: DocumentSnapshot) {}

    override fun setLastTodoReached(isLastTodoReached: Boolean) {}
}