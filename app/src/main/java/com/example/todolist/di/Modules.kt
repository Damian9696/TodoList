package com.example.todolist.di

import androidx.navigation.fragment.navArgs
import com.example.todolist.data.Todo
import com.example.todolist.data.repository.FirestoreTodoListRepository
import com.example.todolist.ui.fragments.ConfigureTodoArgs
import com.example.todolist.utils.ConfigureAction
import com.example.todolist.view_models.ConfigureTodoViewModel
import com.example.todolist.view_models.TodoListViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Firebase.firestore }
    factory { FirestoreTodoListRepository(get()) }
}

val todoListViewModelModule = module {
    viewModel { TodoListViewModel(get()) }
}

val configureTodoViewModelModule = module {
    viewModel {
        ConfigureTodoViewModel(get(), get())
    }
}