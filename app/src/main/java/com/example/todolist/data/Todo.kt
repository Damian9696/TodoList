package com.example.todolist.data

data class Todo(
    val id: String? = "",
    val title: String? = "",
    val description: String? = "",
    val iconUrl: String? = "",
    val timestamp: Long? = System.currentTimeMillis()
)