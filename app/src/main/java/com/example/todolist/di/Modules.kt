package com.example.todolist.di

import com.example.todolist.data.repository.FirestoreTodoListRepository
import com.example.todolist.view_models.ConfigureTodoViewModel
import com.example.todolist.view_models.TodoListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirestoreTodoListRepository() }
}

val todoListViewModelModule = module {
    viewModel { TodoListViewModel(get()) }
}

val configureTodoViewModelModule = module {
    viewModel { ConfigureTodoViewModel(get()) }
}