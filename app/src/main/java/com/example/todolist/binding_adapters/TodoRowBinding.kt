package com.example.todolist.binding_adapters

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.example.todolist.R
import com.example.todolist.adapters.TodoRowListener
import com.example.todolist.data.Todo
import com.example.todolist.utils.TodoRowAction

class TodoRowBinding {
    companion object {

        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun ImageView.loadImageFromUrl(url: String?) {
            url?.let {
                this.load(it) {
                    crossfade(500)
                    error(R.drawable.ic_placeholder_error)
                }
            }
        }

        @BindingAdapter(value = ["todoRowListener", "todo"], requireAll = true)
        @JvmStatic
        fun View.touchListener(todoRowListener: TodoRowListener?, todo: Todo?) {
            this.setOnLongClickListener {
                if (todoRowListener != null && todo != null) {
                    todoRowListener.onTouch(todo, TodoRowAction.LONG_CLICK)
                }
                return@setOnLongClickListener true

            }
            this.setOnClickListener {
                if (todoRowListener != null && todo != null) {
                    todoRowListener.onTouch(todo, TodoRowAction.CLICK)
                }
            }
        }
    }
}