package com.example.mabarfokus.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mabarfokus.MabarFokusApplication
import com.example.mabarfokus.viewmodel.HomeViewModel
import com.example.mabarfokus.viewmodel.LobbyViewModel

object PenyediaViewModel {
    val Factory = viewModelFactory {

        initializer {
            HomeViewModel(mabarFokusApplication().container.mabarRepository)
        }

        initializer {
            LobbyViewModel(mabarFokusApplication().container.mabarRepository)
        }
    }
}
fun CreationExtras.mabarFokusApplication(): MabarFokusApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MabarFokusApplication)