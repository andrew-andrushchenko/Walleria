package com.andrii_a.walleria.ui.account

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.preferences.UserPrivateProfileData
import com.andrii_a.walleria.ui.theme.CloverShape
import com.andrii_a.walleria.ui.theme.WalleriaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: AccountScreenUiState,
    onEvent: (AccountScreenEvent) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.account_settings_screen))
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .animateContentSize()
        ) {
            if (state.isUserLoggedIn) {
                LoggedInHeader(
                    userPrivateProfileData = state.userPrivateProfileData!!,
                    showConfirmation = state.shouldShowLogoutConfirmation,
                    onShowLogoutConfirmation = {
                        onEvent(AccountScreenEvent.ToggleLogoutConfirmation(true))
                    },
                    onDismissLogout = {
                        onEvent(AccountScreenEvent.ToggleLogoutConfirmation(false))
                    },
                    navigateToViewProfileScreen = {
                        onEvent(AccountScreenEvent.OpenViewProfileScreen(state.userPrivateProfileData.nickname))
                    },
                    navigateToEditProfileScreen = {
                        onEvent(AccountScreenEvent.OpenEditProfileScreen)
                    },
                    onLogout = { onEvent(AccountScreenEvent.Logout) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

            } else {
                LoggedOutHeader(
                    navigateToLoginScreen = { onEvent(AccountScreenEvent.OpenLoginScreen) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { onEvent(AccountScreenEvent.OpenSettingsScreen) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(id = R.string.settings)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = stringResource(id = R.string.settings))
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { onEvent(AccountScreenEvent.OpenAboutScreen) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(id = R.string.about)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = stringResource(id = R.string.about))
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LoggedOutHeader(
    navigateToLoginScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
        ) {
            Box(
                modifier = Modifier
            ) {
                Surface(
                    shape = CircleShape,
                    tonalElevation = 8.dp,
                    modifier = Modifier.graphicsLayer {
                        translationX = -100f
                        alpha = 0.4f
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = stringResource(id = R.string.app_name),
                        tint = MaterialTheme.colorScheme.surfaceTint,
                        modifier = Modifier
                            .size(128.dp)
                            .scale(1.3f)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.align(Alignment.Center),
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(id = R.string.creation_starts_here),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = navigateToLoginScreen,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AddCircleOutline,
                contentDescription = stringResource(id = R.string.add_account)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = stringResource(id = R.string.add_account))
        }
    }
}

@Composable
private fun LoggedInHeader(
    showConfirmation: Boolean,
    onShowLogoutConfirmation: () -> Unit,
    onDismissLogout: () -> Unit,
    userPrivateProfileData: UserPrivateProfileData,
    navigateToViewProfileScreen: () -> Unit,
    navigateToEditProfileScreen: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(userPrivateProfileData.profilePhotoUrl)
                    .crossfade(true)
                    .placeholder(ColorDrawable(Color.LTGRAY))
                    .build(),
                contentDescription = stringResource(id = R.string.user_profile_image),
                modifier = Modifier
                    .size(64.dp)
                    .clip(CloverShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(
                    id = R.string.user_full_name_with_nickname_formatted,
                    userPrivateProfileData.firstName,
                    userPrivateProfileData.lastName,
                    userPrivateProfileData.nickname
                ),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = userPrivateProfileData.email,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = showConfirmation,
                label = "",
                modifier = Modifier.fillMaxWidth()
            ) {
                if (it) {
                    LogoutConfirmationRow(
                        onConfirm = onLogout,
                        onDismiss = onDismissLogout,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                } else {
                    ProfileActionRow(
                        navigateToViewProfileScreen = navigateToViewProfileScreen,
                        navigateToEditProfileScreen = navigateToEditProfileScreen,
                        onLogoutClick = onShowLogoutConfirmation,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileActionRow(
    navigateToViewProfileScreen: () -> Unit,
    navigateToEditProfileScreen: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        horizontalArrangement = Arrangement.Center,
        maxLines = 3,
        modifier = modifier
    ) {
        OutlinedButton(
            onClick = navigateToViewProfileScreen,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.RemoveRedEye,
                contentDescription = stringResource(id = R.string.user_profile_view)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(id = R.string.user_profile_view),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        OutlinedButton(
            onClick = navigateToEditProfileScreen,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = stringResource(id = R.string.edit_my_profile)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(id = R.string.user_profile_edit),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        OutlinedButton(
            onClick = onLogoutClick,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = stringResource(id = R.string.logout)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(id = R.string.logout),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LogoutConfirmationRow(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (confirmationText, confirmButton, dismissButton) = createRefs()

        Text(
            text = stringResource(id = R.string.logout_confirmation),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(confirmationText) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(confirmButton.start)
                width = Dimension.fillToConstraints
            }
        )

        TextButton(
            onClick = onConfirm,
            modifier = Modifier.constrainAs(confirmButton) {
                top.linkTo(dismissButton.top)
                bottom.linkTo(dismissButton.bottom)
                end.linkTo(dismissButton.start)
            }
        ) {
            Text(
                text = stringResource(id = R.string.action_yes),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Button(
            onClick = onDismiss,
            modifier = Modifier.constrainAs(dismissButton) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        ) {
            Text(
                stringResource(id = R.string.action_no),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreview() {
    WalleriaTheme {
        val state = AccountScreenUiState(
            isUserLoggedIn = true,
            userPrivateProfileData = UserPrivateProfileData(
                nickname = "john",
                firstName = "John",
                lastName = "Smith",
                email = "john.smith@example.com"
            )
        )

        Surface {
            ProfileScreen(
                state = state,
                onEvent = {}
            )
        }
    }
}