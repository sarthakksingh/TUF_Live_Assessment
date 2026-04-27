package com.sarthak.tufassessment.ui.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.sarthak.tufassessment.ui.components.AppBottomBar
import com.sarthak.tufassessment.ui.screens.CallsScreen
import com.sarthak.tufassessment.ui.screens.ChatScreen
import com.sarthak.tufassessment.ui.screens.ContactsScreen
import com.sarthak.tufassessment.ui.screens.Screen
import com.sarthak.tufassessment.ui.screens.StatusScreen
import com.sarthak.tufassessment.viewmodel.ChatViewModel

@Composable
fun TUFAssessmentApp() {
    val navController = rememberNavController()
    val chatViewModel: ChatViewModel = viewModel()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute?.startsWith("chat/") != true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Chats.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Chats.route) {
                ContactsScreen(
                    viewModel = chatViewModel,
                    onContactSelected = { contactId ->
                        navController.navigate(Screen.Chat.createRoute(contactId)) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.Status.route) {
                StatusScreen(viewModel = chatViewModel)
            }
            composable(Screen.Calls.route) {
                CallsScreen(viewModel = chatViewModel)
            }
            composable(
                route = Screen.Chat.route,
                arguments = listOf(
                    navArgument("contactId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val contactId = backStackEntry.arguments?.getString("contactId").orEmpty()
                ChatScreen(
                    contactId = contactId,
                    viewModel = chatViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
