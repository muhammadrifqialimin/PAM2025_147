package com.example.mabarfokus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mabarfokus.modeldata.Room
import com.example.mabarfokus.modeldata.User
import com.example.mabarfokus.modeldata.KonstantaData
import com.example.mabarfokus.repositori.MabarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LobbyViewModel(private val repository: MabarRepository) : ViewModel() {

    private val _roomState = MutableStateFlow<Room?>(null)
    val roomState: StateFlow<Room?> = _roomState.asStateFlow()

    private val _participantsState = MutableStateFlow<List<User>>(emptyList())
    val participantsState: StateFlow<List<User>> = _participantsState.asStateFlow()

    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    val currentUserId: String = repository.getCurrentUserId()

    val currentNickname: String = repository.getCachedNickname()

    fun createRoom(ignoredName: String = "") {
        viewModelScope.launch {
            _uiMessage.value = "Sedang membuat room..."

            // Panggil repo
            repository.createRoom("")
                .onSuccess { roomId ->
                    startMonitoringRoom(roomId)
                    _uiMessage.value = null
                }
                .onFailure {
                    _uiMessage.value = "Gagal buat room: ${it.message}"
                }
        }
    }

    // --- FUNGSI JOIN ROOM ---
    fun joinRoom(roomCode: String, ignoredName: String = "") {
        viewModelScope.launch {
            _uiMessage.value = "Bergabung..."

            repository.joinRoom(roomCode, "")
                .onSuccess { roomId ->
                    startMonitoringRoom(roomId)
                    _uiMessage.value = null
                }
                .onFailure {
                    _uiMessage.value = "Gagal join: ${it.message}"
                }
        }
    }

    private fun startMonitoringRoom(roomId: String) {
        viewModelScope.launch { repository.getRoomDetails(roomId).collect { _roomState.value = it } }
        viewModelScope.launch { repository.getRoomParticipants(roomId).collect { _participantsState.value = it } }
    }

    // --- FUNGSI MULAI SESI (Support Desimal) ---
    fun mulaiSesi(durasiMenit: String) {
        val currentRoom = _roomState.value ?: return

        // Konversi ke Double (Default 25.0 jika kosong)
        val durasi = durasiMenit.toDoubleOrNull() ?: 25.0

        viewModelScope.launch {
            repository.updateRoomStatus(currentRoom.roomId, KonstantaData.ROOM_STATUS_STARTED, durasi)
        }
    }
}