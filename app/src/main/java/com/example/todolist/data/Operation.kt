package com.example.todolist.data
import com.google.firebase.firestore.DocumentChange.Type

data class Operation(val todo: Todo, val type: Type)