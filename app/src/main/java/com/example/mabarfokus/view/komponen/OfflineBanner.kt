package com.example.mabarfokus.view.komponen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mabarfokus.R
import com.example.mabarfokus.util.ConnectionState
import com.example.mabarfokus.util.rememberConnectivityState

@Composable
fun OfflineBanner() {
    // Panggil utility yang kita buat tadi
    val connectionState = rememberConnectivityState()
    val isOffline = connectionState == ConnectionState.Unavailable

    // Animasi Muncul/Hilang
    AnimatedVisibility(
        visible = isOffline,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Red) // Warna Merah Warning
                .padding(8.dp), // Hardcode padding kecil saja biar compact
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Ikon Warning (Opsional, pakai icon bawaan android kalau blm ada library icon)
                // Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.White)

                Text(
                    text = stringResource(id = R.string.msg_no_connection),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}