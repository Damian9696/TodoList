package com.example.todolist.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentChange.Type.*
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class FakeTodoListLiveData : BaseTodoListLiveData() {

    init {
        value = constOperation
    }

    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {}

    companion object {
        val constOperation = Operation(Todo("0", "Title0", "Description0", "IconUrl0", "0"), ADDED)
    }
}