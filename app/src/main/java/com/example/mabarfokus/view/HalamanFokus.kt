package com.example.mabarfokus.view

import android.content.Context
import android.os.PowerManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource // Import
import androidx.compose.ui.res.dimensionResource // Import
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.*
import com.example.mabarfokus.R
import com.example.mabarfokus.MabarFokusApplication
import com.example.mabarfokus.modeldata.KonstantaData
import com.example.mabarfokus.viewmodel.TimerViewModel

@Composable
fun HalamanFokus(
    roomId: String,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val app = context.applicationContext as MabarFokusApplication
    val realNickname = app.container.mabarRepository.getCachedNickname()

    val viewModel: TimerViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return TimerViewModel(
                    repository = app.container.mabarRepository,
                    roomId = roomId,
                    myUserId = "temp_uid",
                    myNickname = realNickname
                ) as T
            }
        }
    )

    val timerText by viewModel.timerText.collectAsStateWithLifecycle()
    val status by viewModel.roomStatus.collectAsStateWithLifecycle()
    val failedBy by viewModel.distractionCausedBy.collectAsStateWithLifecycle()

    val lottieFocus by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_focus))
    val lottieSuccess by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_success))
    val lottieFail by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_fail))

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                if (powerManager.isInteractive) {
                    viewModel.laporkanDistraksi()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        containerColor = when(status) {
            KonstantaData.ROOM_STATUS_FAILED -> MaterialTheme.colorScheme.errorContainer
            KonstantaData.ROOM_STATUS_SUCCESS -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surface
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_screen_edge)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(contentAlignment = Alignment.Center) {
                LottieAnimation(
                    composition = when(status) {
                        KonstantaData.ROOM_STATUS_FAILED -> lottieFail
                        KonstantaData.ROOM_STATUS_SUCCESS -> lottieSuccess
                        else -> lottieFocus
                    },
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.lottie_focus_size))
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_extra_large)))

            when (status) {
                KonstantaData.ROOM_STATUS_FAILED -> {
                    Text(stringResource(id = R.string.title_game_over), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))
                    Text(stringResource(id = R.string.label_failed_by), style = MaterialTheme.typography.titleMedium)
                    Text(failedBy ?: stringResource(id = R.string.placeholder_someone), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }
                KonstantaData.ROOM_STATUS_SUCCESS -> {
                    Text(stringResource(id = R.string.title_success), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))
                    Text(stringResource(id = R.string.msg_success), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
                }
                else -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_capsule))
                    ) {
                        Text(
                            text = timerText,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = dimensionResource(id = R.dimen.letter_spacing_timer).value.sp,
                                fontSize = dimensionResource(id = R.dimen.text_timer).value.sp // Konversi Dimens SP ke Compose
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_extra_large), vertical = dimensionResource(id = R.dimen.padding_medium))
                        )
                    }
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_medium)))
                    Text(
                        stringResource(id = R.string.msg_warning_focus),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            AnimatedVisibility(visible = status != KonstantaData.ROOM_STATUS_STARTED && status != KonstantaData.ROOM_STATUS_WAITING) {
                Column {
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_xxl)))
                    Button(
                        onClick = onNavigateToHome,
                        modifier = Modifier.fillMaxWidth().height(dimensionResource(id = R.dimen.btn_height)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if(status == KonstantaData.ROOM_STATUS_FAILED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if(status == KonstantaData.ROOM_STATUS_SUCCESS) stringResource(id = R.string.btn_play_again) else stringResource(id = R.string.btn_back_to_lobby),
                            fontSize = dimensionResource(id = R.dimen.text_btn).value.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}