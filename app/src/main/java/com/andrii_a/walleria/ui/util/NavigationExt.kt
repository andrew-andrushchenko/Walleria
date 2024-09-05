package com.andrii_a.walleria.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.andrii_a.walleria.ui.navigation.NavigationScreen

typealias RouteClassName = String

val NavigationScreenRouteClassNames: List<RouteClassName?> by lazy {
    NavigationScreen.entries.map { it.route::class.simpleName }
}

val NavController.currentRouteClassName: RouteClassName?
    @Composable
    get() {
        val navBackStackEntry by this.currentBackStackEntryAsState()
        return navBackStackEntry?.destination?.route
            ?.substringBefore("?")
            ?.substringBefore("/")
            ?.substringAfterLast(".")
    }

val NavigationScreen.routeClassName: RouteClassName?
    get() = this.route::class.simpleName