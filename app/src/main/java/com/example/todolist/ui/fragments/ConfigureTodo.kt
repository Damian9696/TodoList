package com.example.todolist.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.example.todolist.data.Todo
import com.example.todolist.databinding.ConfigureTodoFragmentBinding
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

        binding.addTodoButton.setOnClickListener {

            val title = binding.titleTextInputEditText.text.toString()
            val description = binding.descriptionTextInputEditText.text.toString()
            val iconUrl = binding.iconUrlTextInputEditText.text.toString()

            //add toto logic
            //update todo logic
        }

        binding.backImageView.setOnClickListener {
            moveToListOfTodo()
        }

        subscribeObservers()

    }

    private fun subscribeObservers() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}