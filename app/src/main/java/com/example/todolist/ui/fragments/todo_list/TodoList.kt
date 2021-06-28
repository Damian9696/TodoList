package com.example.todolist.ui.fragments.todo_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.adapters.TodoAdapter
import com.example.todolist.adapters.TodoRowListener
import com.example.todolist.data.Todo
import com.example.todolist.databinding.TodoListFragmentBinding
import com.example.todolist.utils.enums.ConfigureAction
import com.example.todolist.utils.Response
import com.example.todolist.utils.enums.TodoRowAction.CLICK
import com.example.todolist.utils.enums.TodoRowAction.LONG_CLICK
import com.example.todolist.view_models.TodoListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentChange.Type.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class TodoList : Fragment() {

    private var _binding: TodoListFragmentBinding? = null
    private val binding get() = _binding!!

    private var _todoAdapter: TodoAdapter? = null
    private val todoAdapter get() = _todoAdapter!!

    private val todoListViewModel by inject<TodoListViewModel>()
    private val listOfTodo = mutableListOf<Todo>()

    private var isScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActionBar()
    }

    private fun initActionBar() {
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as MainActivity).supportActionBar?.title = getString(R.string.todo_list_title)
    }

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
        initRecyclerViewOnScrollListener()
        subscribeObservers()
    }

    private fun initRecyclerViewOnScrollListener() {
        val onScrollListener = object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager =
                    binding.listOfTodoRecyclerView.layoutManager as LinearLayoutManager
                val firstVisibleTodoPosition = layoutManager.findFirstVisibleItemPosition()
                val visibleTodoCount = layoutManager.childCount
                val totalTodoCount = layoutManager.itemCount

                if (isScrolling && (firstVisibleTodoPosition + visibleTodoCount == totalTodoCount)) {
                    isScrolling = false
                    getListOfTodo()
                }
            }
        }
        binding.listOfTodoRecyclerView.addOnScrollListener(onScrollListener)
    }

    private fun subscribeObservers() {
        getListOfTodo()
        observeResource()
    }

    private fun observeResource() {
        todoListViewModel.response.observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource) {
                    is Response.Loading -> binding.progressBar.isVisible = true
                    is Response.Error -> {
                        binding.progressBar.isVisible = false
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(getString(R.string.global_error))
                            .setMessage("${resource.message}")
                            .setPositiveButton(getString(R.string.todo_list_dialog_positive_button)) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    is Response.Success -> binding.progressBar.isVisible = false
                }
            }
        }
    }

    private fun getListOfTodo() {
        val todoListLiveData = todoListViewModel.getTodoListLiveData()
        todoListLiveData?.let {
            it.observe(viewLifecycleOwner) { nullableOperation ->
                nullableOperation?.let { operation ->

                    binding.progressBar.isVisible = true
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

                    val newList = mutableListOf<Todo>().apply { addAll(listOfTodo) }
                    todoAdapter.submitList(newList)
                    binding.progressBar.isVisible = false

                    if (operation.type == ADDED) {
                        lifecycleScope.launch {
                            delay(100)
                            binding.listOfTodoRecyclerView.smoothScrollToPosition(0)
                        }
                    }
                }
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
                return@forEachIndexed
            }
        }
    }

    private fun addTodo(addedTodo: Todo) {
        listOfTodo.add(0, addedTodo)
    }

    private fun initAdapter() {
        _todoAdapter = TodoAdapter(TodoRowListener { todo, rowAction ->
            when (rowAction) {
                CLICK -> {
                    findNavController().navigate(
                        TodoListDirections.actionTodoListToAddTodo(
                            ConfigureAction.UPDATE,
                            todo
                        )
                    )
                }
                LONG_CLICK -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.todo_list_dialog_title_delete))
                        .setMessage(
                            getString(
                                R.string.todo_list_delete_todo_question_title,
                                todo.title
                            )
                        )
                        .setNeutralButton(getString(R.string.todo_list_dialog_cancel_button)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setNegativeButton(getString(R.string.todo_list_dialog_negative_button)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(getString(R.string.todo_list_dialog_positive_button)) { dialog, _ ->
                            todoListViewModel.deleteTodo(todo).observe(viewLifecycleOwner) {
                                it?.let { response ->
                                    handleResponse(response)
                                }
                            }
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        })
        binding.listOfTodoRecyclerView.adapter = todoAdapter
    }

    private fun handleResponse(todoResponse: Response<Todo>) {
        when (todoResponse) {
            is Response.Loading -> {
                binding.progressBar.isVisible = true
            }
            is Response.Success -> {
                binding.progressBar.isVisible = false
                Snackbar.make(
                    requireView(),
                    getString(R.string.todo_list_todo_deleted),
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
            is Response.Error -> {
                binding.progressBar.isVisible = false
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.global_error))
                    .setMessage("${todoResponse.message}")
                    .setPositiveButton(getString(R.string.todo_list_dialog_positive_button)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun initFab() {
        binding.addTodoFab.setOnClickListener {
            findNavController().navigate(
                TodoListDirections.actionTodoListToAddTodo(
                    ConfigureAction.ADD,
                    Todo()
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}