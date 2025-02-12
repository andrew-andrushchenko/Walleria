package com.andrii_a.walleria.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.navigation.AppNavigationHost
import com.andrii_a.walleria.ui.navigation.NavigationScreen
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.currentRouteClassName
import com.andrii_a.walleria.ui.util.noNavBarsRoutes

@Composable
fun WalleriaApp() {
    WalleriaTheme {
        val navController = rememberNavController()

        var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

        val adaptiveInfo = currentWindowAdaptiveInfo()
        val navigationSuiteType =
            NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)

        NavigationSuiteScaffold(
            navigationSuiteItems = {
                NavigationScreen.entries.forEachIndexed { index, entry ->
                    item(
                        icon = {
                            Icon(
                                imageVector = if (index == selectedTabIndex)
                                    entry.iconSelected
                                else
                                    entry.iconUnselected,
                                contentDescription = stringResource(id = R.string.navigation_bar_icon)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(id = entry.titleRes),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        selected = index == selectedTabIndex,
                        onClick = {
                            selectedTabIndex = index
                            navController.navigate(entry.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }

                                launchSingleTop = true
                                restoreState = true

                            }
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            },
            layoutType = if (navController.currentRouteClassName in noNavBarsRoutes)
                NavigationSuiteType.None
            else
                navigationSuiteType,
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            AppNavigationHost(navHostController = navController)
        }
    }
}
