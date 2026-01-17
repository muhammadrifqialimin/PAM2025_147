package com.example.mabarfokus.modeldata

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class User(
    // UBAH SEMUA 'val' JADI 'var'

    @get:PropertyName("user_id") @set:PropertyName("user_id")
    var userId: String = "",

    @get:PropertyName("room_id") @set:PropertyName("room_id")
    var roomId: String = "",

    @get:PropertyName("nickname") @set:PropertyName("nickname")
    var nickname: String = "",

    @get:PropertyName("is_host") @set:PropertyName("is_host")
    var isHost: Boolean = false,

    @get:PropertyName("status") @set:PropertyName("status")
    var status: String = "READY",

    @get:PropertyName("joined_at") @set:PropertyName("joined_at")
    var joinedAt: Long = System.currentTimeMillis()
)