package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.data.Todo
import com.example.todolist.databinding.TodoRowLayoutBinding
import com.example.todolist.utils.GenericDiffUtil
import com.example.todolist.utils.TodoRowAction

class TodoAdapter(private val todoRowListener: TodoRowListener) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var listOfTodo = emptyList<Todo>()

    class TodoViewHolder(private val binding: TodoRowLayoutBinding) :
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
        val todo = listOfTodo[position]
        holder.bind(todo, todoRowListener)
    }

    override fun getItemCount(): Int {
        return listOfTodo.size
    }

    fun setData(newListOfTodo: List<Todo>) {
        val todoDiffUtil = GenericDiffUtil(listOfTodo, newListOfTodo)
        val diffResult = DiffUtil.calculateDiff(todoDiffUtil)
        listOfTodo = newListOfTodo
        diffResult.dispatchUpdatesTo(this)
    }
}

class TodoRowListener(val listener: (todoId: String?, rowAction: TodoRowAction) -> Unit) {
    fun onTouch(todo: Todo, rowAction: TodoRowAction) = listener(todo.id, rowAction)
}