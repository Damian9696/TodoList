package com.example.todolist.view_models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.todolist.data.FakeTodoListLiveData
import com.example.todolist.data.Todo
import com.example.todolist.data.repository.BaseFirestoreTodoListRepository
import com.example.todolist.data.repository.FakeFirestoreTodoListRepository
import com.example.todolist.utils.enums.ConfigureAction
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentChange.Type.ADDED
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class TodoListViewModelTest {

    private lateinit var todoListViewModel: TodoListViewModel
    private lateinit var baseFirestoreTodoListRepository: BaseFirestoreTodoListRepository

    private val firestore = Mockito.mock(FirebaseFirestore::class.java)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        baseFirestoreTodoListRepository = FakeFirestoreTodoListRepository(firestore)
        todoListViewModel =
            TodoListViewModel(baseFirestoreTodoListRepository)
    }

    @Test
    fun operation_receivedOperation_todoIsNotNull() {

        //WHEN, get operation
        val result = todoListViewModel.getTodoListLiveData()

        //THEN, the getTodoListLiveData is triggered, and return value is not null
        assert(result!!.value != null)
    }

    @Test
    fun operation_receivedOperation_operationTypeIsAdded() {

        //WHEN, get operation
        val result = todoListViewModel.getTodoListLiveData()

        //THEN, the getTodoListLiveData is triggered, and return value type is ADDED
        assert(result!!.value!!.type == FakeTodoListLiveData.constOperation.type)
    }

}