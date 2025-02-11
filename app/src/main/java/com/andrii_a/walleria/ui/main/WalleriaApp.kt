package com.andrii_a.walleria.ui.main

import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.login.LoginActivity
import com.andrii_a.walleria.ui.navigation.AppNavigationHost
import com.andrii_a.walleria.ui.navigation.NavigationScreen
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.profile.ProfileScreen
import com.andrii_a.walleria.ui.profile.ProfileViewModel
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.currentRouteClassName
import com.andrii_a.walleria.ui.util.startActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalleriaApp() {
    WalleriaTheme {
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
                            label = { Text(text = stringResource(id = entry.titleRes)) },
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
                layoutType = if (navController.currentRouteClassName == Screen.PhotoDetails::class.simpleName)
                    NavigationSuiteType.None
                else
                    navigationSuiteType,
                modifier = Modifier
                    .imePadding()
                    .padding(innerPadding)
            ) {
                AppNavigationHost(
                    navHostController = navController,
                    openProfileBottomSheet = { scope.launch { scaffoldState.bottomSheetState.expand() } }
                )
            }
        }
    }
}