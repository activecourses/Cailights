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
import com.example.cailights.data.auth.FakeAuthRepository
import com.example.cailights.domain.auth.AuthRepository
import com.example.cailights.ui.FeedRoot
import com.example.cailights.ui.MessagesRoot
import com.example.cailights.ui.SignInRoot
import com.example.cailights.ui.SignUpRoot
import com.example.cailights.ui.SplashScreen
import com.example.cailights.ui.theme.CailightsTheme
import org.koin.android.ext.android.inject

enum class Screen {
    SPLASH,
    SIGN_IN,
    SIGN_UP,
    HOME,
    MESSAGES
}

class MainActivity : ComponentActivity() {
    
    private val authRepository: AuthRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CailightsTheme {
                var currentScreen by remember { mutableStateOf(Screen.SPLASH) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        Screen.SPLASH -> SplashScreen(
                            onTimeout = { 
                                val startScreen = if ((authRepository as FakeAuthRepository).isLoggedIn()) {
                                    Screen.HOME
                                } else {
                                    Screen.SIGN_IN
                                }
                                currentScreen = startScreen 
                            },
                        )
                        Screen.SIGN_IN -> SignInRoot(
                            onSignUpClick = { currentScreen = Screen.SIGN_UP },
                            onSignInSuccess = { currentScreen = Screen.HOME }
                        )
                        Screen.SIGN_UP -> SignUpRoot(
                            onSignInClick = { currentScreen = Screen.SIGN_IN },
                            onSignUpSuccess = { currentScreen = Screen.HOME }
                        )
                        Screen.HOME -> FeedRoot(
                            onNavigateToMessages = { currentScreen = Screen.MESSAGES }
                        )
                        Screen.MESSAGES -> MessagesRoot(
                            onBackClick = { currentScreen = Screen.HOME }
                        )
                    }
                }
            }
        }
    }
}
