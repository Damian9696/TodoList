package com.example.todolist.ui.fragments.configure_todo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.data.Todo
import com.example.todolist.databinding.ConfigureTodoFragmentBinding
import com.example.todolist.utils.enums.ConfigureAction
import com.example.todolist.utils.enums.ConfigureAction.ADD
import com.example.todolist.utils.enums.ConfigureAction.UPDATE
import com.example.todolist.utils.Response
import com.example.todolist.utils.enums.ValidateAction.*
import com.example.todolist.view_models.ConfigureTodoViewModel
import com.example.todolist.view_models.ValidationWithConfigureAction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.stateViewModel

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

    private fun initActionBar() {
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
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
        initActionBar()
        initConfigureButton()
        subscribeObservers()
    }

    private fun initConfigureButton() {
        binding.configureTodoButton.setOnClickListener {

            val title = binding.titleTextInputEditText.text.toString()
            val description = binding.descriptionTextInputEditText.text.toString()

            configureTodoViewModel.validateTitle(title)
            configureTodoViewModel.validateDescription(description)
        }
    }

    private fun subscribeObservers() {
        configureTodoViewModel.validateTitle.observe(viewLifecycleOwner) {
            it?.let { validateAction ->
                when (validateAction) {
                    SUCCESS -> {
                        binding.titleTextInput.isErrorEnabled = false
                        binding.titleTextInput.error = ""
                    }
                    TEXT_TOO_LONG -> {
                        binding.titleTextInput.isErrorEnabled = true
                        binding.titleTextInput.error =
                            getString(R.string.validation_text_is_too_long)
                    }
                    TEXT_EMPTY -> {
                        binding.titleTextInput.isErrorEnabled = true
                        binding.titleTextInput.error = getString(R.string.validation_empty_field)
                    }
                }
            }
        }

        configureTodoViewModel.validateDescription.observe(viewLifecycleOwner) {
            it?.let { validateAction ->
                when (validateAction) {
                    SUCCESS -> {
                        binding.descriptionTextInput.isErrorEnabled = false
                        binding.descriptionTextInput.error = ""
                    }
                    TEXT_TOO_LONG -> {
                        binding.descriptionTextInput.isErrorEnabled = true
                        binding.descriptionTextInput.error =
                            getString(R.string.validation_text_is_too_long)
                    }
                    TEXT_EMPTY -> {
                        binding.descriptionTextInput.isErrorEnabled = true
                        binding.descriptionTextInput.error =
                            getString(R.string.validation_empty_field)
                    }
                }
            }
        }

        observeConfigureAction()
        observeUpdateFieldsIfConfigureActionIsUpdate()
        observeValidationPass()
    }

    private fun observeConfigureAction() {
        configureTodoViewModel.configureAction.observe(viewLifecycleOwner) {
            it?.let {
                configureAppearance(it)
            }
        }
    }

    private fun observeUpdateFieldsIfConfigureActionIsUpdate() {
        configureTodoViewModel.updateFieldsIfConfigureActionIsUpdate.observe(viewLifecycleOwner) {
            it?.let { todo ->
                binding.titleTextInputEditText.setText(todo.title.orEmpty())
                binding.descriptionTextInputEditText.setText(todo.description.orEmpty())
                binding.iconUrlTextInputEditText.setText(todo.iconUrl.orEmpty())
            }
        }
    }

    private fun observeValidationPass() {
        configureTodoViewModel.validationPass.observe(viewLifecycleOwner) {
            it?.let { validationWithConfigureAction ->
                if (validationWithConfigureAction.pass) {
                    proceedConfigurationPass(validationWithConfigureAction)
                }
            }
        }
    }

    private fun proceedConfigurationPass(validationWithConfigureAction: ValidationWithConfigureAction) {
        val title = binding.titleTextInputEditText.text.toString()
        val description = binding.descriptionTextInputEditText.text.toString()
        val iconUrl = binding.iconUrlTextInputEditText.text.toString()

        when (validationWithConfigureAction.configureAction) {
            ADD -> {
                configureTodoViewModel.addTodo(title, description, iconUrl)
                    .observe(viewLifecycleOwner) { nullableResponse ->
                        nullableResponse?.let { response ->
                            handleResponse(response)
                        }
                    }
            }
            UPDATE -> {
                configureTodoViewModel.updateTodo(title, description, iconUrl)
                    .observe(viewLifecycleOwner) { nullableResponse ->
                        nullableResponse?.let { response ->
                            handleResponse(response)
                        }
                    }
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
                Snackbar.make(
                    requireView(),
                    getString(R.string.configure_todo_response_success),
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
            is Response.Error -> {
                binding.progressBar.isVisible = false
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.global_error))
                    .setMessage("${todoResponse.message}")
                    .setPositiveButton(getString(R.string.global_ok)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun configureAppearance(it: ConfigureAction) {
        when (it) {
            ADD -> {
                (activity as MainActivity).supportActionBar?.title =
                    getString(R.string.configure_todo_title_create)
                binding.configureTodoButton.text = getString(R.string.configure_todo_button_add)
            }
            UPDATE -> {
                (activity as MainActivity).supportActionBar?.title =
                    getString(R.string.configure_todo_title_update)
                binding.configureTodoButton.text = getString(R.string.configure_todo_button_update)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            moveToListOfTodo()
        }
        return super.onOptionsItemSelected(item)
    }

}