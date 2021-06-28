package com.example.todolist.view_models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.example.todolist.data.Todo
import com.example.todolist.data.repository.BaseFirestoreTodoListRepository
import com.example.todolist.data.repository.FakeFirestoreTodoListRepository
import com.example.todolist.utils.Response
import com.example.todolist.utils.enums.ConfigureAction
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


@RunWith(JUnit4::class)
class ConfigureTodoViewModelTest {


    private lateinit var configureTodoViewModel: ConfigureTodoViewModel
    private lateinit var baseFirestoreTodoListRepository: BaseFirestoreTodoListRepository

    private val savedStateHandle = Mockito.mock(SavedStateHandle::class.java)
    private val firestore = Mockito.mock(FirebaseFirestore::class.java)


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(savedStateHandle.get<Todo>("todo")).thenReturn(
            Todo(
                "0",
                "Title0",
                "Description0",
                "IconUrl0",
                "0"
            )
        )
        `when`(savedStateHandle.get<ConfigureAction>("configureAction")).thenReturn(
            ConfigureAction.ADD
        )
        baseFirestoreTodoListRepository = FakeFirestoreTodoListRepository(firestore)
        configureTodoViewModel =
            ConfigureTodoViewModel(savedStateHandle, baseFirestoreTodoListRepository)
    }

    @Test
    fun todo_addTodo_receivedTodoIsTheSameAsPassedTodo() {

        //GIVEN, todo
        val newTodo = Todo(title = "Title0", description = "Description0", iconUrl = "IconUrl0")

        //WHEN, adding a new todo
        val addTodoViewModel =
            configureTodoViewModel.addTodo(newTodo.title!!, newTodo.description!!, newTodo.iconUrl!!)

        //THEN, the addTodo is triggered, and newTodo and received todo fields are the same
        assert(addTodoViewModel.value!!.data!!.title == newTodo.title)
        assert(addTodoViewModel.value!!.data!!.description == newTodo.description)
        assert(addTodoViewModel.value!!.data!!.iconUrl == newTodo.iconUrl)
    }

    @Test
    fun todo_updateTodo_receivedTodoIsTheSameAsPassedTodo() {

        //GIVEN, todo
        val updateTodo = Todo(id = "0",title = "Title0", description = "Description0", iconUrl = "IconUrl0", timestamp = "0")

        //WHEN, updating a todo
        val updateTodoViewModel =
            configureTodoViewModel.updateTodo(updateTodo.title!!, updateTodo.description!!, updateTodo.iconUrl!!)

        //THEN, the updateTodo is triggered, and updateTodo and received todo fields are the same
        assert(updateTodoViewModel.value!!.data!!.title == updateTodo.title)
        assert(updateTodoViewModel.value!!.data!!.description == updateTodo.description)
        assert(updateTodoViewModel.value!!.data!!.iconUrl == updateTodo.iconUrl)
        assert(updateTodoViewModel.value!!.data!!.id == updateTodo.id)
        assert(updateTodoViewModel.value!!.data!!.timestamp == updateTodo.timestamp)
    }
}