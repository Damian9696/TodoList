package com.example.todolist.interfaces

import com.google.firebase.firestore.DocumentSnapshot

interface OnLastVisibleTodoCallback {
    fun setLastVisibleTodo(lastVisibleTodo: DocumentSnapshot)
}