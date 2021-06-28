package com.example.todolist.binding_adapters

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.todolist.utils.TimeUtil.convertLongToTime

class TodoListBinding {
    companion object {

        @BindingAdapter("timestampToDate")
        @JvmStatic
        fun TextView.timestampToDate(timestamp: String?) {
            timestamp?.let {
                this.text = convertLongToTime(timestamp.toLong())
            }
        }

    }
}