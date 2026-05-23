package com.example.cailights

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.cailights.ui.SignInScreen
import com.example.cailights.ui.SignUpScreen
import com.example.cailights.ui.theme.CailightsTheme

enum class Screen {
    SIGN_IN,
    SIGN_UP
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CailightsTheme {
                var currentScreen by remember { mutableStateOf(Screen.SIGN_IN) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        Screen.SIGN_IN -> SignInScreen(
                            onSignUpClick = { currentScreen = Screen.SIGN_UP }
                        )
                        Screen.SIGN_UP -> SignUpScreen(
                            onSignInClick = { currentScreen = Screen.SIGN_IN }
                        )
                    }
                }
            }
        }
    }
}
