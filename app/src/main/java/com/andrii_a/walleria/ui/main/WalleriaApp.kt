package com.andrii_a.walleria.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.andrii_a.walleria.ui.navigation.MainNavigationHost
import com.andrii_a.walleria.ui.navigation.NavigationScreen
import com.andrii_a.walleria.ui.navigation.NavigationScreenRoutes
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.currentRoute
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun WalleriaApp() {
    WalleriaTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            val systemUiController = rememberSystemUiController()
            val navController = rememberNavController()

            Box(modifier = Modifier.imePadding()) {
                MainNavigationHost(
                    navHostController = navController,
                    systemUiController = systemUiController
                )

                if (navController.currentRoute in NavigationScreenRoutes) {
                    NavigationBar(modifier = Modifier.align(Alignment.BottomCenter)) {
                        NavigationScreen.values().forEach { item ->
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (navController.currentRoute == item.route)
                                            item.iconSelected
                                        else
                                            item.iconUnselected,
                                        contentDescription = stringResource(id = item.titleRes)
                                    )
                                },
                                label = { Text(text = stringResource(id = item.titleRes)) },
                                selected = navController.currentRoute == item.route,
                                onClick = {
                                    navController.navigate(item.route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}