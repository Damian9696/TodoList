package com.example.todolist.view_models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.todolist.data.repository.FakeTodoListRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ConfigureTodoViewModelTest {

    private lateinit var configureTodoViewModel: ConfigureTodoViewModel
    private lateinit var respository: FakeTodoListRepository

    @Before
    fun setup(){
        respository = FakeTodoListRepository()
    }

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()


}