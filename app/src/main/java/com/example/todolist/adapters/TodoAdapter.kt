package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.data.Todo
import com.example.todolist.databinding.TodoRowLayoutBinding
import com.example.todolist.utils.enums.TodoRowAction

class TodoAdapter(private val todoRowListener: TodoRowListener) :
    ListAdapter<Todo,
            TodoAdapter.TodoViewHolder>(DataItemDiffCallback()) {

    class TodoViewHolder private constructor(private val binding: TodoRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(todo: Todo, todoRowListener: TodoRowListener) {
            binding.todo = todo
            binding.todoRowListener = todoRowListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TodoViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TodoRowLayoutBinding.inflate(layoutInflater, parent, false)
                return TodoViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, todoRowListener)
    }


}

class DataItemDiffCallback : DiffUtil.ItemCallback<Todo>() {
    override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
        return oldItem == newItem
    }
}

class TodoRowListener(val listener: (todoId: Todo, rowAction: TodoRowAction) -> Unit) {
    fun onTouch(todo: Todo, rowAction: TodoRowAction) = listener(todo, rowAction)
}