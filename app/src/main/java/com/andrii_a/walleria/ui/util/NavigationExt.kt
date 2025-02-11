package com.andrii_a.walleria.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

typealias RouteClassName = String

val NavController.currentRouteClassName: RouteClassName?
    @Composable
    get() {
        val navBackStackEntry by this.currentBackStackEntryAsState()
        return navBackStackEntry?.destination?.route
            ?.substringBefore("?")
            ?.substringBefore("/")
            ?.substringAfterLast(".")
    }
