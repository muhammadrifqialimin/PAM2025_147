package com.example.mabarfokus

import android.app.Application
import com.example.mabarfokus.repositori.AppContainer
import com.example.mabarfokus.repositori.DefaultAppContainer

class MabarFokusApplication : Application() {
    // Container untuk Dependency Injection
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}