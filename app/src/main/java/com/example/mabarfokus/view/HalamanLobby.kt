package com.example.mabarfokus.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Import SP penting
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.*
import com.example.mabarfokus.R
import com.example.mabarfokus.modeldata.KonstantaData
import com.example.mabarfokus.viewmodel.LobbyViewModel
import com.example.mabarfokus.viewmodel.provider.PenyediaViewModel
// Pastikan import OfflineBanner ada
import com.example.mabarfokus.view.komponen.OfflineBanner

@Composable
fun HalamanLobby(
    onNavigateToFokus: (String) -> Unit,
    viewModel: LobbyViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val roomState by viewModel.roomState.collectAsStateWithLifecycle()
    val participants by viewModel.participantsState.collectAsStateWithLifecycle()
    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()

    // Setup Lottie Offline
    val lottieLobby by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.anim_lobby)
    )

    var inputRoomCode by remember { mutableStateOf("") }
    var inputDuration by remember { mutableStateOf("25") }
    val clipboardManager = LocalClipboardManager.current

    // Navigasi otomatis
    LaunchedEffect(roomState?.status) {
        if (roomState?.status == KonstantaData.ROOM_STATUS_STARTED) {
            roomState?.roomId?.let { onNavigateToFokus(it) }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        // Column Induk (Tanpa Padding Horizontal) agar Banner Full Width
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            // 1. BANNER KONEKSI (Paling Atas)
            OfflineBanner()

            // 2. KONTEN UTAMA (Dengan Padding)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.padding_large),
                        vertical = dimensionResource(id = R.dimen.padding_medium)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Header Title
                Text(
                    text = if (roomState == null) stringResource(id = R.string.title_start_session) else stringResource(id = R.string.title_lobby_room),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_small))
                )

                // Pesan Error
                uiMessage?.let {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth().padding(bottom = dimensionResource(id = R.dimen.padding_medium))
                    ) {
                        Text(text = it, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)))
                    }
                }

                if (roomState == null) {
                    // --- STATE 1: BELUM MASUK ROOM ---

                    // Sapaan Personal
                    Text(
                        text = stringResource(id = R.string.greeting_lobby, viewModel.currentNickname),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.padding_medium))
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))

                    // Animasi Lobby
                    Box(
                        modifier = Modifier
                            .height(dimensionResource(id = R.dimen.lobby_anim_box_height))
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        LottieAnimation(
                            composition = lottieLobby,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier.size(dimensionResource(id = R.dimen.lottie_lobby_size))
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_large)))

                    // Menu Utama
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacer_medium))
                    ) {
                        // Tombol Buat Room
                        Card(
                            onClick = { viewModel.createRoom() },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(id = R.string.icon_crown), style = MaterialTheme.typography.headlineLarge)
                                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))
                                Text(stringResource(id = R.string.btn_create_room), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(stringResource(id = R.string.desc_create_room), style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        // Input Gabung Room
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(dimensionResource(id = R.dimen.border_width), MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
                                Text(stringResource(id = R.string.label_have_code), style = MaterialTheme.typography.labelMedium)
                                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = inputRoomCode,
                                        onValueChange = { inputRoomCode = it.uppercase() },
                                        placeholder = { Text(stringResource(id = R.string.hint_room_code)) },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_medium))
                                    )
                                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacer_small)))
                                    Button(
                                        onClick = { viewModel.joinRoom(inputRoomCode) },
                                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_medium)),
                                        modifier = Modifier.height(dimensionResource(id = R.dimen.btn_height))
                                    ) {
                                        Text(stringResource(id = R.string.btn_join))
                                    }
                                }
                            }
                        }
                    }

                } else {
                    // --- STATE 2: SUDAH DI DALAM LOBBY ---

                    // Kartu Kode Room
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                clipboardManager.setText(AnnotatedString(roomState?.roomCode ?: ""))
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stringResource(id = R.string.label_access_code), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // --- PERBAIKAN SP DI SINI ---
                                Text(
                                    text = roomState?.roomCode ?: stringResource(id = R.string.placeholder_code),
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        // Gunakan nilai manual 4.sp agar aman dari error resolusi
                                        letterSpacing = 4.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacer_medium)))
                                Icon(Icons.Default.ContentCopy, contentDescription = stringResource(id = R.string.cd_copy), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))
                            Text(stringResource(id = R.string.hint_tap_copy), style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_large)))

                    // Daftar Peserta
                    Text(
                        text = "${stringResource(id = R.string.title_team_focus)} (${participants.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(top = dimensionResource(id = R.dimen.padding_small)),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
                    ) {
                        items(participants) { user ->
                            val isMe = user.userId == viewModel.currentUserId
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isMe) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_medium))
                            ) {
                                Row(
                                    modifier = Modifier.padding(dimensionResource(id = R.dimen.corner_medium)).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(dimensionResource(id = R.dimen.avatar_size))
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(user.nickname.take(1).uppercase(), fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.corner_medium)))
                                    Column(modifier = Modifier.weight(1f)) {
                                        val suffix = if (isMe) " ${stringResource(id = R.string.suffix_you)}" else ""
                                        Text(text = "${user.nickname}$suffix", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                        if (user.isHost) {
                                            Text(stringResource(id = R.string.label_host), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Kontrol Host / Guest
                    val myUser = participants.find { it.userId == viewModel.currentUserId }
                    if (myUser?.isHost == true) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 4.dp,
                            shape = RoundedCornerShape(
                                topStart = dimensionResource(id = R.dimen.corner_bottom_sheet),
                                topEnd = dimensionResource(id = R.dimen.corner_bottom_sheet)
                            ),
                            modifier = Modifier.fillMaxWidth().padding(top = dimensionResource(id = R.dimen.padding_small))
                        ) {
                            Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.spacer_small)))
                                    Text(stringResource(id = R.string.label_set_duration), style = MaterialTheme.typography.titleSmall)
                                }
                                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))
                                OutlinedTextField(
                                    value = inputDuration,
                                    onValueChange = { inputDuration = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_medium))
                                )
                                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_medium)))
                                Button(
                                    onClick = { viewModel.mulaiSesi(inputDuration) },
                                    modifier = Modifier.fillMaxWidth().height(dimensionResource(id = R.dimen.btn_height)),
                                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_large))
                                ) {
                                    Text(stringResource(id = R.string.btn_start_focus), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                            modifier = Modifier.fillMaxWidth().padding(top = dimensionResource(id = R.dimen.padding_medium))
                        ) {
                            Row(
                                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(dimensionResource(id = R.dimen.progress_size)), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.corner_medium)))
                                Text(stringResource(id = R.string.msg_waiting_host), color = MaterialTheme.colorScheme.onTertiaryContainer)
                            }
                        }
                    }
                }
            }
        }
    }
}