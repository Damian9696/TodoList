package com.example.todolist.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Todo(
    val id: String? = "",
    val title: String? = "",
    val description: String? = "",
    val iconUrl: String? = "",
    val timestamp: String? = ""
) : Parcelable