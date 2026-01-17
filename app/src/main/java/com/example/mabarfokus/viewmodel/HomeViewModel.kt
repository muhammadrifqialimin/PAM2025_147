package com.example.mabarfokus.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mabarfokus.repositori.MabarRepository
import kotlinx.coroutines.launch

// Status UI (Opsional, tapi bagus untuk loading)
sealed interface HomeUiState {
    object Idle : HomeUiState
    object Loading : HomeUiState
    object Success : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(private val repository: MabarRepository) : ViewModel() {
    var uiState: HomeUiState by mutableStateOf(HomeUiState.Idle)
        private set

    // --- FUNGSI BARU (YANG DICARI HALAMAN HOME) ---
    fun simpanNickname(nickname: String) {
        if (nickname.isBlank()) {
            uiState = HomeUiState.Error("Nama tidak boleh kosong")
            return
        }

        uiState = HomeUiState.Loading
        viewModelScope.launch {
            // Panggil Repository
            val result = repository.signInAnonymously(nickname)

            uiState = if (result.isSuccess) {
                HomeUiState.Success
            } else {
                HomeUiState.Error(result.exceptionOrNull()?.message ?: "Gagal Login")
            }
        }
    }
}