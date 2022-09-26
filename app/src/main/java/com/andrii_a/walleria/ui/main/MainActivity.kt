package com.andrii_a.walleria.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.andrii_a.walleria.ui.navigation.MainNavHost
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WalleriaTheme {
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val systemUiController = rememberSystemUiController()
                    val navController = rememberNavController()

                    MainNavHost(
                        navHostController = navController,
                        systemUiController = systemUiController
                    )
                }
            }
        }
    }
}