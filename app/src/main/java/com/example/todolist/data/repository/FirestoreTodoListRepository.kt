package com.example.todolist.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.todolist.data.Operation
import com.example.todolist.data.Todo
import com.example.todolist.utils.Constants.LIMIT
import com.example.todolist.utils.Constants.TODO_COLLECTION
import com.example.todolist.utils.Constants.TODO_TITLE_PROPERTY
import com.example.todolist.data.TodoListLiveData
import com.example.todolist.interfaces.OnLastTodoReachedCallback
import com.example.todolist.interfaces.OnLastVisibleTodoCallback
import com.example.todolist.interfaces.TodoListRepository
import com.example.todolist.utils.Constants.TODO_TIMESTAMP_PROPERTY
import com.example.todolist.utils.Constants.TODO_DESCRIPTION_PROPERTY
import com.example.todolist.utils.Constants.TODO_ICON_URL_PROPERTY
import com.example.todolist.utils.Constants.TODO_ID_PROPERTY
import com.example.todolist.utils.Response
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.ASCENDING

class FirestoreTodoListRepository(private val firestore: FirebaseFirestore) :
    BaseFirestoreTodoListRepository(firestore) {

    private var isLastTodoReached = false
    private var lastVisibleTodo: DocumentSnapshot? = null

    private val _response = MutableLiveData<Response<Todo>>()
    private val response: LiveData<Response<Todo>> get() = _response

    override fun getTodoListLiveData(): TodoListLiveData? {

        val collectionReference = firestore.collection(TODO_COLLECTION)
        var query = collectionReference.orderBy(
            TODO_TIMESTAMP_PROPERTY,
            ASCENDING
        ).limit(LIMIT.toLong())

        if (isLastTodoReached) {
            return null
        }
        if (lastVisibleTodo != null) {
            query = query.startAfter(lastVisibleTodo)
        }

        return TodoListLiveData(query, this, this)
    }

    override fun addTodo(
        title: String,
        description: String,
        iconUrl: String
    ): LiveData<Response<Todo>> {
        val id = firestore.collection(TODO_COLLECTION).document().id
        val timestamp = System.currentTimeMillis().toString()
        val todo = Todo(
            id = id,
            title = title,
            description = description,
            iconUrl = iconUrl,
            timestamp = timestamp
        )


        val todoMap = hashMapOf(
            TODO_ID_PROPERTY to todo.id,
            TODO_TITLE_PROPERTY to todo.title,
            TODO_DESCRIPTION_PROPERTY to todo.description,
            TODO_TIMESTAMP_PROPERTY to todo.timestamp,
            TODO_ICON_URL_PROPERTY to todo.iconUrl
        )

        var response: Response<Todo> = Response.Loading(todo)
        _response.value = response

        firestore.collection(TODO_COLLECTION).document(id).set(todoMap)
            .addOnSuccessListener {
                response = Response.Success(todo)
                _response.value = response
            }
            .addOnFailureListener { e ->
                response = Response.Error(e.message.toString())
                _response.value = response
            }

        return this.response
    }

    override fun updateTodo(
        id: String,
        title: String,
        description: String,
        iconUrl: String,
        timestamp: String
    ): LiveData<Response<Todo>> {
        val todo = Todo(
            id = id,
            title = title,
            description = description,
            iconUrl = iconUrl,
            timestamp = timestamp
        )

        val todoMap = mapOf(
            TODO_ID_PROPERTY to todo.id,
            TODO_TITLE_PROPERTY to todo.title,
            TODO_DESCRIPTION_PROPERTY to todo.description,
            TODO_TIMESTAMP_PROPERTY to todo.timestamp,
            TODO_ICON_URL_PROPERTY to todo.iconUrl
        )

        var response: Response<Todo> = Response.Loading(todo)
        _response.value = response

        firestore.collection(TODO_COLLECTION).document(todo.id!!).update(todoMap)
            .addOnSuccessListener {
                response = Response.Success(todo)
                _response.value = response
            }
            .addOnFailureListener { e ->
                response = Response.Error(e.message.toString())
                _response.value = response
            }

        return this.response
    }

    override fun deleteTodo(todo: Todo): LiveData<Response<Todo>> {

        var response: Response<Todo> = Response.Loading(todo)
        _response.value = response

        firestore.collection(TODO_COLLECTION).document(todo.id!!).delete()
            .addOnSuccessListener {
                response = Response.Success(todo)
                _response.value = response
            }
            .addOnFailureListener { e ->
                response = Response.Error(e.message.toString())
                _response.value = response
            }

        return this.response
    }


    override fun setLastVisibleTodo(lastVisibleTodo: DocumentSnapshot) {
        this.lastVisibleTodo = lastVisibleTodo
    }

    override fun setLastTodoReached(isLastTodoReached: Boolean) {
        this.isLastTodoReached = isLastTodoReached
    }
}

abstract class BaseFirestoreTodoListRepository(private val firestore: FirebaseFirestore) :
    TodoListRepository, OnLastVisibleTodoCallback,
    OnLastTodoReachedCallback {}