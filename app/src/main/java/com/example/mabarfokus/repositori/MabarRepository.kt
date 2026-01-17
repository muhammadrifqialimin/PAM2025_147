package com.example.mabarfokus.repositori

import com.example.mabarfokus.modeldata.Room
import com.example.mabarfokus.modeldata.User
import kotlinx.coroutines.flow.Flow

interface MabarRepository {
    // Auth
    suspend fun signInAnonymously(nickname: String): Result<String>

    // Fungsi Ambil Data Session
    fun getCachedNickname(): String
    fun getCurrentUserId(): String

    // Room Management
    suspend fun createRoom(hostName: String): Result<String>
    suspend fun joinRoom(roomCode: String, nickname: String): Result<String>

    // Real-time Data Streams (Flow)
    fun getRoomDetails(roomId: String): Flow<Room>
    fun getRoomParticipants(roomId: String): Flow<List<User>>

    // Logic
    // <--- UBAH TIPE DATA DI SINI (Int -> Double)
    suspend fun updateRoomStatus(roomId: String, status: String, durationInMinutes: Double = 25.0)

    suspend fun updateUserStatus(roomId: String, userId: String, status: String)
    suspend fun setFailure(roomId: String, failedBy: String)
}