package com.example.mabarfokus.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource // Import Baru
import androidx.compose.ui.res.dimensionResource // Import Baru
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.*
import androidx.compose.ui.unit.sp
import com.example.mabarfokus.viewmodel.HomeViewModel
import com.example.mabarfokus.viewmodel.provider.PenyediaViewModel
import com.example.mabarfokus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanHome(
    onNavigateToLobby: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    var nickname by remember { mutableStateOf("") }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.anim_home)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_screen_edge)), // Pakai Dimens
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Animasi Header
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(dimensionResource(id = R.dimen.lottie_home_size)) // Pakai Dimens
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_large)))

            // 2. Judul & Slogan
            Text(
                text = stringResource(id = R.string.app_name), // Pakai Strings
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(id = R.string.app_slogan), // Pakai Strings
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_xl)))

            // 3. Kartu Input
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_large)), // Pakai Dimens
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
                    Text(
                        text = stringResource(id = R.string.label_nickname),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_small)))

                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        placeholder = { Text(stringResource(id = R.string.hint_nickname)) },
                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_medium)),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacer_large)))

            // 4. Tombol Aksi
            Button(
                onClick = {
                    if (nickname.isNotBlank()) {
                        viewModel.simpanNickname(nickname)
                        onNavigateToLobby()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.btn_height)),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_large)),
                enabled = nickname.isNotBlank()
            ) {
                Text(
                    text = stringResource(id = R.string.btn_enter_lobby),
                    fontSize = dimensionResource(id = R.dimen.text_btn).value.sp, // Convert Dp to Sp trick
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}