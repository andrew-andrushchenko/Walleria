package com.andrii_a.walleria.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.andrii_a.walleria.ui.common.WNavigationBar
import com.andrii_a.walleria.ui.navigation.MainNavHost
import com.andrii_a.walleria.ui.navigation.NavigationScreen
import com.andrii_a.walleria.ui.navigation.NavigationScreenRoutes
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.currentRoute
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun Walleria() {
    WalleriaTheme {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            val systemUiController = rememberSystemUiController()
            val navController = rememberNavController()

            Box(modifier = Modifier.imePadding()) {
                MainNavHost(
                    navHostController = navController,
                    systemUiController = systemUiController
                )

                if (navController.currentRoute in NavigationScreenRoutes) {
                    WNavigationBar(
                        navScreenItems = NavigationScreen.values().toList(),
                        onItemSelected = { navigationScreen ->
                            navController.navigate(navigationScreen.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        },
                        navController = navController,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}