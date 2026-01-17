package com.example.mabarfokus.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mabarfokus.MabarFokusApplication
import com.example.mabarfokus.viewmodel.HomeViewModel
import com.example.mabarfokus.viewmodel.LobbyViewModel
// Note: TimerViewModel biasanya dibuat manual di UI karena butuh parameter dinamis (roomId),
// atau menggunakan AssistedInjection. Untuk tahap ini kita fokus ke Home & Lobby dulu.

object PenyediaViewModel {
    val Factory = viewModelFactory {

        // Initializer untuk HomeViewModel
        initializer {
            HomeViewModel(mabarFokusApplication().container.mabarRepository)
        }

        // Initializer untuk LobbyViewModel
        initializer {
            LobbyViewModel(mabarFokusApplication().container.mabarRepository)
        }
    }
}

// Extension function untuk mengambil Application class
fun CreationExtras.mabarFokusApplication(): MabarFokusApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MabarFokusApplication)