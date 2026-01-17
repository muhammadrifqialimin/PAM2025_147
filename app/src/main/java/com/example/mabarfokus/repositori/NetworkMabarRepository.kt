package com.example.mabarfokus.repositori

import com.example.mabarfokus.modeldata.Room
import com.example.mabarfokus.modeldata.User
import com.example.mabarfokus.modeldata.KonstantaData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID


class NetworkMabarRepository(
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase
) : MabarRepository {

    private var currentSessionNickname: String = "User Tanpa Nama"

    // --- 1. AUTHENTICATION ---
    override suspend fun signInAnonymously(nickname: String): Result<String> {
        return try {
            val authResult = auth.signInAnonymously().await()
            val userId = authResult.user?.uid ?: throw Exception("Gagal mendapatkan UID")
            currentSessionNickname = nickname
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCachedNickname(): String = currentSessionNickname
    override fun getCurrentUserId(): String = auth.currentUser?.uid ?: ""

    // --- 2. CREATE ROOM ---
    override suspend fun createRoom(hostName: String): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User belum login")
            val roomCode = UUID.randomUUID().toString().substring(0, 6).uppercase()
            val roomId = db.reference.child("rooms").push().key ?: throw Exception("Gagal generate ID")
            val finalName = currentSessionNickname

            val newRoom = Room(
                roomId = roomId,
                roomCode = roomCode,
                status = KonstantaData.ROOM_STATUS_WAITING,
                createdAt = System.currentTimeMillis()
            )

            val hostUser = User(
                userId = userId,
                roomId = roomId,
                nickname = finalName,
                isHost = true,
                status = KonstantaData.USER_STATUS_READY
            )

            db.reference.child("rooms").child(roomId).setValue(newRoom).await()
            db.reference.child("rooms").child(roomId).child("users").child(userId).setValue(hostUser).await()

            Result.success(roomId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- 3. JOIN ROOM (DENGAN LAZY DELETION) ---
    override suspend fun joinRoom(roomCode: String, nickname: String): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User belum login")
            val finalName = currentSessionNickname

            val snapshot = db.reference.child("rooms")
                .orderByChild("room_code") // Sesuai aturan Firebase kamu
                .equalTo(roomCode)
                .get()
                .await()

            if (!snapshot.exists()) return Result.failure(Exception("Room tidak ditemukan"))

            val roomNode = snapshot.children.first()
            val roomId = roomNode.key ?: throw Exception("Error Key")

            // Ambil data penting untuk pengecekan
            val currentStatus = roomNode.child("status").getValue(String::class.java)
            val createdAt = roomNode.child("createdAt").getValue(Long::class.java) ?: 0L

            // === FITUR LAZY DELETION (SRS REQ-6b) ===
            // Cek apakah usia room sudah lebih dari 1 jam (3600000 ms)
            val oneHourInMillis = 60 * 60 * 1000
            val isExpired = (System.currentTimeMillis() - createdAt) > oneHourInMillis

            // Hapus jika Expired DAN Statusnya sudah selesai/gagal
            if (isExpired && (currentStatus == KonstantaData.ROOM_STATUS_SUCCESS || currentStatus == KonstantaData.ROOM_STATUS_FAILED)) {
                db.reference.child("rooms").child(roomId).removeValue().await()
                return Result.failure(Exception("Room sudah kadaluwarsa"))
            }
            // =========================================

            if (currentStatus != KonstantaData.ROOM_STATUS_WAITING) return Result.failure(Exception("Room sudah berjalan"))

            val newUser = User(
                userId = userId,
                roomId = roomId,
                nickname = finalName,
                isHost = false,
                status = KonstantaData.USER_STATUS_READY
            )

            db.reference.child("rooms").child(roomId).child("users").child(userId).setValue(newUser).await()
            Result.success(roomId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- STREAM DATA ---
    override fun getRoomDetails(roomId: String): Flow<Room> = callbackFlow {
        val ref = db.reference.child("rooms").child(roomId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(Room::class.java)?.let { trySend(it) }
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun getRoomParticipants(roomId: String): Flow<List<User>> = callbackFlow {
        val ref = db.reference.child("rooms").child(roomId).child("users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                trySend(userList)
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // --- UPDATE STATUS & WAKTU (FIX DOUBLE) ---
    override suspend fun updateRoomStatus(roomId: String, status: String, durationInMinutes: Double) {
        val updates = hashMapOf<String, Any>("status" to status)

        if (status == KonstantaData.ROOM_STATUS_STARTED) {
            // Hitung milidetik: Menit * 60 * 1000
            val durationInMillis = (durationInMinutes * 60 * 1000).toLong()
            val targetTime = System.currentTimeMillis() + durationInMillis

            updates["target_timestamp"] = targetTime
            updates["duration"] = durationInMinutes
        }

        db.reference.child("rooms").child(roomId).updateChildren(updates).await()
    }

    override suspend fun updateUserStatus(roomId: String, userId: String, status: String) {
        db.reference.child("rooms").child(roomId).child("users").child(userId)
            .child("status").setValue(status).await()
    }

    override suspend fun setFailure(roomId: String, failedBy: String) {
        val updates = hashMapOf<String, Any>(
            "status" to KonstantaData.ROOM_STATUS_FAILED,
            "failed_by" to failedBy
        )
        db.reference.child("rooms").child(roomId).updateChildren(updates).await()
    }
}