package com.andrii_a.walleria.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.andrii_a.walleria.ui.navigation.Screen

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

val noNavBarsRoutes = listOf(
    Screen.PhotoDetails::class.simpleName,
    Screen.Settings::class.simpleName,
    Screen.About::class.simpleName,
    Screen.EditUserProfile::class.simpleName,
    Screen.CollectPhoto::class.simpleName,
    Screen.Search::class.simpleName,
    Screen.Login::class.simpleName,
    Screen.UserDetails::class.simpleName
)