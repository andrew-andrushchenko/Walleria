package com.andrii_a.walleria.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.andrii_a.walleria.ui.login.LoginActivity
import com.andrii_a.walleria.ui.navigation.AppNavigationHost
import com.andrii_a.walleria.ui.navigation.NavigationScreen
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.profile.ProfileScreen
import com.andrii_a.walleria.ui.profile.ProfileViewModel
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.NavigationScreenRouteClassNames
import com.andrii_a.walleria.ui.util.currentRouteClassName
import com.andrii_a.walleria.ui.util.routeClassName
import com.andrii_a.walleria.ui.util.startActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalleriaApp() {
    WalleriaTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            val navController = rememberNavController()

            val bottomSheetState = rememberStandardBottomSheetState(
                skipHiddenState = false,
                initialValue = SheetValue.Hidden
            )
            val scaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = bottomSheetState
            )
            val scope = rememberCoroutineScope()

            // Temporary workaround solution to show ProfileScreen as bottom sheet
            // since accompanist navigation-material library is not compatible with material3.
            BottomSheetScaffold(
                sheetContent = {
                    val context = LocalContext.current

                    val viewModel: ProfileViewModel = hiltViewModel()

                    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsStateWithLifecycle()
                    val userProfileData by viewModel.userPrivateProfileData.collectAsStateWithLifecycle()

                    ProfileScreen(
                        isUserLoggedIn = isUserLoggedIn,
                        userPrivateProfileData = userProfileData,
                        navigateToLoginScreen = {
                            context.startActivity(LoginActivity::class.java)
                        },
                        onLogout = viewModel::logout,
                        navigateToViewProfileScreen = {
                            scope.launch { scaffoldState.bottomSheetState.hide() }
                            navController.navigate(Screen.UserDetails(it))
                        },
                        navigateToEditProfileScreen = {
                            scope.launch { scaffoldState.bottomSheetState.hide() }
                            navController.navigate(Screen.EditUserProfile)
                        },
                        navigateToSettingsScreen = {
                            scope.launch { scaffoldState.bottomSheetState.hide() }
                            navController.navigate(Screen.Settings)
                        },
                        navigateToAboutScreen = {
                            scope.launch { scaffoldState.bottomSheetState.hide() }
                            navController.navigate(Screen.About)
                        }
                    )
                },
                sheetPeekHeight = 0.dp,
                sheetTonalElevation = 2.dp,
                scaffoldState = scaffoldState
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .imePadding()
                        .padding(innerPadding)
                ) {
                    AppNavigationHost(
                        navHostController = navController,
                        openProfileBottomSheet = { scope.launch { scaffoldState.bottomSheetState.expand() } }
                    )

                    if (navController.currentRouteClassName in NavigationScreenRouteClassNames) {
                        NavigationBar(modifier = Modifier.align(Alignment.BottomCenter)) {
                            NavigationScreen.entries.forEach { screen ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = if (navController.currentRouteClassName == screen.routeClassName)
                                                screen.iconSelected
                                            else
                                                screen.iconUnselected,
                                            contentDescription = stringResource(id = screen.titleRes)
                                        )
                                    },
                                    label = { Text(text = stringResource(id = screen.titleRes)) },
                                    selected = navController.currentRouteClassName == screen.routeClassName,
                                    onClick = {
                                        navController.navigate(screen.route) {
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
}