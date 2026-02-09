package com.example.mabarfokus.view.controller

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mabarfokus.view.HalamanFokus
import com.example.mabarfokus.view.HalamanHome
import com.example.mabarfokus.view.HalamanLobby
import com.example.mabarfokus.view.route.DestinasiFokus
import com.example.mabarfokus.view.route.DestinasiHome
import com.example.mabarfokus.view.route.DestinasiLobby

@Composable
fun MabarApp(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiHome.route,
        modifier = modifier
    ) {
        // 1. HOME (Login Nickname)
        composable(DestinasiHome.route) {
            HalamanHome(
                onNavigateToLobby = {
                    navController.navigate(DestinasiLobby.route)
                }
            )
        }

        // 2. LOBBY (Buat/Join Room)
        composable(DestinasiLobby.route) {
            HalamanLobby(
                onNavigateToFokus = { roomId ->
                    navController.navigate("${DestinasiFokus.route}/$roomId")
                }
            )
        }

        // 3. FOKUS (Timer & Game Over)
        composable(
            route = DestinasiFokus.routeWithArgs,
            arguments = listOf(navArgument(DestinasiFokus.roomIdArg) { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString(DestinasiFokus.roomIdArg)
            if (roomId != null) {
                HalamanFokus(
                    roomId = roomId,
                    // --- PERBAIKAN LOGIKA NAVIGASI DI SINI ---
                    onNavigateToHome = {
                        // Kita navigasi ke LOBBY (bukan Home/Login)
                        navController.navigate(DestinasiLobby.route) {
                            popUpTo(DestinasiLobby.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}