package com.example.mabarfokus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mabarfokus.modeldata.KonstantaData
import com.example.mabarfokus.repositori.MabarRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel(
    private val repository: MabarRepository,
    private val roomId: String,
    private val myUserId: String,
    private val myNickname: String
) : ViewModel() {

    private val _timerText = MutableStateFlow("Loading...")
    val timerText: StateFlow<String> = _timerText.asStateFlow()

    private val _roomStatus = MutableStateFlow(KonstantaData.ROOM_STATUS_WAITING) // Default Waiting
    val roomStatus: StateFlow<String> = _roomStatus.asStateFlow()

    private val _distractionCausedBy = MutableStateFlow<String?>(null)
    val distractionCausedBy: StateFlow<String?> = _distractionCausedBy.asStateFlow()

    init {
        monitorRoomStatus()
    }

    private fun monitorRoomStatus() {
        viewModelScope.launch {
            repository.getRoomDetails(roomId).collect { room ->
                _roomStatus.value = room.status
                _distractionCausedBy.value = room.failedBy

                if (room.status == KonstantaData.ROOM_STATUS_STARTED) {
                    hitungMundur(room.targetTimestamp)
                } else if (room.status == KonstantaData.ROOM_STATUS_FAILED) {
                    _timerText.value = "GAME OVER"
                } else if (room.status == KonstantaData.ROOM_STATUS_SUCCESS) {
                    _timerText.value = "SELESAI"
                }
            }
        }
    }

    // Hitung mundur (SRS REQ-7)
    private fun hitungMundur(targetTime: Long) {
        viewModelScope.launch {
            // Loop hanya jika status masih STARTED
            while (_roomStatus.value == KonstantaData.ROOM_STATUS_STARTED) {
                val sisaWaktu = targetTime - System.currentTimeMillis()

                if (sisaWaktu <= 0) {
                    finishSessionSuccess()
                    break // Keluar dari loop
                }

                val minutes = (sisaWaktu / 1000) / 60
                val seconds = (sisaWaktu / 1000) % 60
                _timerText.value = String.format("%02d:%02d", minutes, seconds)

                delay(1000)
            }
        }
    }

    // Fungsi Kritis Langkah 7 (Dipanggil UI saat curang)
    fun laporkanDistraksi() {
        // Cek double-lock: Jangan lapor jika room sudah gagal atau sukses
        if (_roomStatus.value == KonstantaData.ROOM_STATUS_STARTED) {
            viewModelScope.launch {
                // Set status ROOM menjadi FAILED (Semua orang akan melihat ini)
                repository.setFailure(roomId, myNickname)

                // Opsional: Jika kamu punya fungsi update status per-user
                // repository.updateUserStatus(roomId, myUserId, "FAILED")
            }
        }
    }

    private suspend fun finishSessionSuccess() {
        // Cek agar tidak double update
        if (_roomStatus.value == KonstantaData.ROOM_STATUS_STARTED) {
            repository.updateRoomStatus(roomId, KonstantaData.ROOM_STATUS_SUCCESS)
        }
    }
}