package com.example.mabarfokus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mabarfokus.ui.theme.MabarFokusTheme
import com.example.mabarfokus.view.controller.MabarApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MabarFokusTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Memanggil MabarApp dengan modifier padding dari Scaffold
                    MabarApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}