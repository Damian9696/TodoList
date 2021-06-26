package com.example.todolist.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todolist.data.Todo
import com.example.todolist.databinding.ConfigureTodoFragmentBinding
import com.example.todolist.utils.ConfigureAction
import com.example.todolist.utils.ConfigureAction.ADD
import com.example.todolist.utils.ConfigureAction.UPDATE
import com.example.todolist.utils.Response
import com.example.todolist.view_models.ConfigureTodoViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import timber.log.Timber

class ConfigureTodo : Fragment() {

    private var _binding: ConfigureTodoFragmentBinding? = null
    private val binding get() = _binding!!

    private val args: ConfigureTodoArgs by navArgs()
    private val configureTodoViewModel: ConfigureTodoViewModel by stateViewModel(state = { args.toBundle() })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            moveToListOfTodo()
        }
    }

    private fun moveToListOfTodo() {
        findNavController().navigate(ConfigureTodoDirections.actionConfigureTodoToTodoList())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ConfigureTodoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBackImageView()
        subscribeObservers()
    }

    private fun initBackImageView() {
        binding.backImageView.setOnClickListener {
            moveToListOfTodo()
        }
    }

    private fun initConfigureButton(configureAction: ConfigureAction) {
        binding.configureTodoButton.setOnClickListener {

            val title = binding.titleTextInputEditText.text.toString()
            val description = binding.descriptionTextInputEditText.text.toString()
            val iconUrl = binding.iconUrlTextInputEditText.text.toString()

            when (configureAction) {
                ADD -> {
                    configureTodoViewModel.addTodo(title, description, iconUrl)
                        .observe(viewLifecycleOwner) {
                            it?.let { response ->
                                handleResponse(response)
                            }
                        }
                }
                UPDATE -> {
                    configureTodoViewModel.updateTodo(title, description, iconUrl)
                        .observe(viewLifecycleOwner) {
                            it?.let { response ->
                                handleResponse(response)
                            }
                        }
                }
            }

        }
    }

    private fun subscribeObservers() {
        configureTodoViewModel.configureAction.observe(viewLifecycleOwner) {
            it?.let {
                initConfigureButton(it)
                configureAppearance(it)
            }
        }

        configureTodoViewModel.todoForUpdateIfConfigureActionIsUpdate.observe(viewLifecycleOwner) {
            it?.let { todo ->
                binding.titleTextInputEditText.setText(todo.title.orEmpty())
                binding.descriptionTextInputEditText.setText(todo.description.orEmpty())
                binding.iconUrlTextInputEditText.setText(todo.iconUrl.orEmpty())
            }
        }
    }

    private fun handleResponse(todoResponse: Response<Todo>) {
        when (todoResponse) {
            is Response.Loading -> {
                binding.progressBar.isVisible = true
            }
            is Response.Success -> {
                binding.progressBar.isVisible = false
                Snackbar.make(requireView(), "Todo added!", Snackbar.LENGTH_LONG)
                    .show()
            }
            is Response.Error -> {
                binding.progressBar.isVisible = false
                Snackbar.make(requireView(), "Todo not added! ${todoResponse.message}", Snackbar.LENGTH_LONG)
                    .show()
            }
        }

    }

    private fun configureAppearance(it: ConfigureAction) {
        when (it) {
            ADD -> {
                binding.titleTextView.text = "Create new todo"
                binding.configureTodoButton.text = "Add"
            }
            UPDATE -> {
                binding.titleTextView.text = "Update todo"
                binding.configureTodoButton.text = "Update"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}