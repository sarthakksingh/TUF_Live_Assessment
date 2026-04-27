package com.sarthak.tufassessment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sarthak.tufassessment.ui.theme.TUFAssessmentTheme
import com.sarthak.tufassessment.ui.navigation.TUFAssessmentApp
import com.sarthak.tufassessment.ui.screens.UchatSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TUFAssessmentTheme(darkTheme = true, dynamicColor = false) {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    UchatSplashScreen(
                        onFinished = { showSplash = false }
                    )
                } else {
                    TUFAssessmentApp()
                }
            }
        }
    }
}
