package com.example.todolist.data


import androidx.lifecycle.LiveData
import com.example.todolist.utils.Constants.LIMIT
import com.example.todolist.interfaces.OnLastTodoReachedCallback
import com.example.todolist.interfaces.OnLastVisibleTodoCallback
import com.google.firebase.firestore.*

class TodoListLiveData(
    private val query: Query,
    private val onLastVisibleTodoCallback: OnLastVisibleTodoCallback,
    private val onLastTodoReachedCallback: OnLastTodoReachedCallback
) : LiveData<Operation>(), EventListener<QuerySnapshot> {

    private lateinit var listenerRegistration: ListenerRegistration


    override fun onActive() {
        listenerRegistration = query.addSnapshotListener(this)
    }

    override fun onInactive() {
        listenerRegistration.remove()
    }

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        if (error != null) return

        value?.let { notNullValue ->
            for (documentChange in notNullValue.documentChanges) {
                val todo = documentChange.document.toObject(Todo::class.java)
                val operation = Operation(todo, documentChange.type)
                setValue(operation)
            }

            val querySnapshotSize = notNullValue.size()
            if (querySnapshotSize < LIMIT) {
                onLastTodoReachedCallback.setLastTodoReached(true)
            } else {
                val lastVisibleTodo = notNullValue.documents[querySnapshotSize - 1]
                onLastVisibleTodoCallback.setLastVisibleTodo(lastVisibleTodo)
            }
        }

    }
}