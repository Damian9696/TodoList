package com.example.todolist.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.todolist.adapters.TodoAdapter
import com.example.todolist.adapters.TodoRowListener
import com.example.todolist.data.Todo
import com.example.todolist.databinding.TodoListFragmentBinding
import com.example.todolist.utils.ConfigureAction
import com.example.todolist.utils.Resource
import com.example.todolist.view_models.TodoListViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentChange.Type.*
import org.koin.android.ext.android.inject

class TodoList : Fragment() {

    private var _binding: TodoListFragmentBinding? = null
    private val binding get() = _binding!!

    private var _todoAdapter: TodoAdapter? = null
    private val todoAdapter get() = _todoAdapter!!

    private val todoListViewModel by inject<TodoListViewModel>()
    private val listOfTodo = mutableListOf<Todo>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TodoListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initFab()
        initAdapter()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        getListOfTodo()
        observeResource()
    }

    private fun observeResource() {
        todoListViewModel.resource.observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        Snackbar.make(requireView(), "Loading", Snackbar.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        Snackbar.make(
                            requireView(),
                            "Error ${resource.message}",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is Resource.Success -> {
                        Snackbar.make(
                            requireView(),
                            "Success ${resource.data!!.title} removed!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
    }

    private fun getListOfTodo() {
        val todoListLiveData = todoListViewModel.getTodoListLiveData()
        todoListLiveData?.let {
            it.observe(viewLifecycleOwner) { operation ->
                operation?.let {
                    val todo = operation.todo
                    when (operation.type) {
                        ADDED -> {
                            addTodo(todo)
                        }
                        MODIFIED -> {
                            modifyTodo(todo)
                        }
                        REMOVED -> {
                            removeTodo(todo)
                        }
                    }
                }
                todoAdapter.setData(listOfTodo)
            }
        }
    }

    private fun removeTodo(removedTodo: Todo) {
        listOfTodo.remove(removedTodo)
    }

    private fun modifyTodo(modifiedTodo: Todo) {
        listOfTodo.forEachIndexed { index, todo ->
            if (todo.id!! == modifiedTodo.id) {
                listOfTodo[index] = modifiedTodo
            }
        }
    }

    private fun addTodo(addedTodo: Todo) {
        listOfTodo.add(addedTodo)
    }

    private fun initAdapter() {
        _todoAdapter = TodoAdapter(TodoRowListener { todoId, rowAction ->
            Snackbar.make(requireView(), "${todoId} ${rowAction}", Snackbar.LENGTH_SHORT).show()
        })
        binding.listOfTodoRecyclerView.adapter = todoAdapter
    }

    private fun initFab() {
        binding.addTodoFab.setOnClickListener {
            findNavController().navigate(TodoListDirections.actionTodoListToAddTodo(ConfigureAction.ADD))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}