package com.sarthak.tufassessment.ui.screens

sealed class Screen(val route: String, val title: String) {
    data object Chats : Screen("chats", "Chats")
    data object Status : Screen("status", "Status")
    data object Calls : Screen("calls", "Calls")
    data object Chat : Screen("chat/{contactId}", "Chat") {
        fun createRoute(contactId: String): String = "chat/$contactId"
    }
}
