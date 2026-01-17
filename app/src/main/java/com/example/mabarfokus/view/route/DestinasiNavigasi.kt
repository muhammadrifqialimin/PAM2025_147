package com.example.mabarfokus.view.route

interface DestinasiNavigasi {
    val route: String
    val title: String
}

object DestinasiHome : DestinasiNavigasi {
    override val route = "home"
    override val title = "Selamat Datang"
}

object DestinasiLobby : DestinasiNavigasi {
    override val route = "lobby"
    override val title = "Lobby Room"
}

object DestinasiFokus : DestinasiNavigasi {
    override val route = "fokus"
    override val title = "Mode Fokus"
    const val roomIdArg = "roomId"
    // Route dengan argumen: fokus/{roomId}
    val routeWithArgs = "$route/{$roomIdArg}"
}