package com.example.todolist.data.repository

import androidx.lifecycle.MutableLiveData
import com.example.todolist.data.Todo
import com.example.todolist.utils.Constants.LIMIT
import com.example.todolist.utils.Constants.TODO_COLLECTION
import com.example.todolist.utils.Constants.TODO_TITLE_PROPERTY
import com.example.todolist.data.TodoListLiveData
import com.example.todolist.interfaces.OnLastTodoReachedCallback
import com.example.todolist.interfaces.OnLastVisibleTodoCallback
import com.example.todolist.interfaces.TodoListRepository
import com.example.todolist.utils.Constants.TODO_DATE_PROPERTY
import com.example.todolist.utils.Constants.TODO_DESCRIPTION_PROPERTY
import com.example.todolist.utils.Constants.TODO_ICON_URL_PROPERTY
import com.example.todolist.utils.Constants.TODO_ID_PROPERTY
import com.example.todolist.utils.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query.Direction.ASCENDING
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreTodoListRepository : TodoListRepository, OnLastVisibleTodoCallback,
    OnLastTodoReachedCallback {

    private val firestore = Firebase.firestore

    private var isLastTodoReached = false
    private var lastVisibleTodo: DocumentSnapshot? = null

    override fun getTodoListLiveData(): TodoListLiveData? {

        val collectionReference = firestore.collection(TODO_COLLECTION)
        var query = collectionReference.orderBy(
            TODO_TITLE_PROPERTY,
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
    ): MutableLiveData<Resource<Todo>> {
        val id = firestore.collection(TODO_COLLECTION).document().id
        val todo = Todo(id = id, title = title, description = description, iconUrl = iconUrl)
        val mutableRresource = MutableLiveData<Resource<Todo>>()

        val todoMap = hashMapOf(
            TODO_ID_PROPERTY to todo.id,
            TODO_TITLE_PROPERTY to todo.title,
            TODO_DESCRIPTION_PROPERTY to todo.description,
            TODO_DATE_PROPERTY to todo.timestamp,
            TODO_ICON_URL_PROPERTY to todo.iconUrl
        )

        var resource: Resource<Todo> = Resource.Loading()
        mutableRresource.value = resource

        firestore.collection(TODO_COLLECTION).document(id).set(todoMap)
            .addOnSuccessListener {
                resource = Resource.Success(todo)
                mutableRresource.value = resource
            }
            .addOnFailureListener { e ->
                resource = Resource.Error(e.message.toString())
                mutableRresource.value = resource
            }

        return mutableRresource
    }

    override fun updateTodo(todo: Todo): MutableLiveData<Resource<Todo>> {
        val todo = Todo(
            id = todo.id,
            title = todo.title,
            description = todo.description,
            iconUrl = todo.iconUrl
        )
        val mutableResource = MutableLiveData<Resource<Todo>>()

        val todoMap = mapOf(
            TODO_ID_PROPERTY to todo.id,
            TODO_TITLE_PROPERTY to todo.title,
            TODO_DESCRIPTION_PROPERTY to todo.description,
            TODO_DATE_PROPERTY to todo.timestamp,
            TODO_ICON_URL_PROPERTY to todo.iconUrl
        )

        var resource: Resource<Todo> = Resource.Loading()
        mutableResource.value = resource

        firestore.collection(TODO_COLLECTION).document(todo.id!!).update(todoMap)
            .addOnSuccessListener {
                resource = Resource.Success(todo)
                mutableResource.value = resource
            }
            .addOnFailureListener { e ->
                resource = Resource.Error(e.message.toString())
                mutableResource.value = resource
            }

        return mutableResource
    }

    override fun deleteTodo(todo: Todo): MutableLiveData<Resource<Todo>> {
        val mutableResource = MutableLiveData<Resource<Todo>>()

        var resource: Resource<Todo> = Resource.Loading()
        mutableResource.value = resource

        firestore.collection(TODO_COLLECTION).document(todo.id!!).delete()
            .addOnSuccessListener {
                resource = Resource.Success(todo)
                mutableResource.value = resource
            }
            .addOnFailureListener { e ->
                resource = Resource.Error(e.message.toString())
                mutableResource.value = resource
            }

        return mutableResource
    }


    override fun setLastVisibleTodo(lastVisibleTodo: DocumentSnapshot) {
        this.lastVisibleTodo = lastVisibleTodo
    }

    override fun setLastTodoReached(isLastTodoReached: Boolean) {
        this.isLastTodoReached = isLastTodoReached
    }
}