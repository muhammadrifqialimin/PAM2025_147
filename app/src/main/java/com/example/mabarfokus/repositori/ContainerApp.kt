package com.example.mabarfokus.repositori

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

interface AppContainer {
    val mabarRepository: MabarRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    // Inisialisasi Firebase Auth
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance("https://mabarfokus-d445c-default-rtdb.asia-southeast1.firebasedatabase.app")
    }

    override val mabarRepository: MabarRepository by lazy {
        NetworkMabarRepository(auth, db)
    }
}