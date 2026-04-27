package com.sarthak.tufassessment.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sarthak.tufassessment.ui.screens.Screen

private data class BottomItem(
    val screen: Screen,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val bottomItems = listOf(
    BottomItem(Screen.Chats, Icons.AutoMirrored.Outlined.Chat),
    BottomItem(Screen.Status, Icons.Outlined.Circle),
    BottomItem(Screen.Calls, Icons.Outlined.Call)
)

@Composable
fun AppBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        bottomItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.screen.title
                    )
                },
                label = {
                    Text(text = item.screen.title)
                }
            )
        }
    }
}
