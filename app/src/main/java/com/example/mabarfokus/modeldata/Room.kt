package com.example.mabarfokus.modeldata

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class Room(

    @get:PropertyName("room_id") @set:PropertyName("room_id")
    var roomId: String = "",

    @get:PropertyName("room_code") @set:PropertyName("room_code")
    var roomCode: String = "",

    @get:PropertyName("status") @set:PropertyName("status")
    var status: String = "WAITING",

    @get:PropertyName("created_at") @set:PropertyName("created_at")
    var createdAt: Long = System.currentTimeMillis(),

    @get:PropertyName("target_timestamp") @set:PropertyName("target_timestamp")
    var targetTimestamp: Long = 0L,

    @get:PropertyName("duration") @set:PropertyName("duration")
    var duration: Int = 0,

    @get:PropertyName("failed_by") @set:PropertyName("failed_by")
    var failedBy: String? = null
)