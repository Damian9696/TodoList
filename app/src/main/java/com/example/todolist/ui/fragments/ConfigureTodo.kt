package com.example.todolist.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todolist.data.Todo
import com.example.todolist.databinding.ConfigureTodoFragmentBinding
import com.example.todolist.utils.ConfigureAction
import com.example.todolist.utils.ConfigureAction.ADD
import com.example.todolist.utils.ConfigureAction.UPDATE
import com.example.todolist.utils.Constants
import com.example.todolist.view_models.ConfigureTodoViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import timber.log.Timber

class ConfigureTodo : Fragment() {

    private var _binding: ConfigureTodoFragmentBinding? = null
    private val binding get() = _binding!!

    private val configureTodoViewModel by inject<ConfigureTodoViewModel>()
    private val args: ConfigureTodoArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        passArgs()
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            moveToListOfTodo()
        }
    }

    private fun passArgs() {
        configureTodoViewModel.setConfigureAction(args.configureAction)
        configureTodoViewModel.insertTodoForUpdate(args.todo)
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
                }
                UPDATE -> {
                    configureTodoViewModel.updateTodo(title, description, iconUrl)
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