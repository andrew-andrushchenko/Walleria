package com.andrii_a.walleria.ui.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.preferences.MyProfileData
import com.andrii_a.walleria.ui.common.WTextButton
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ProfileScreen(
    isUserLoggedInStateFlow: StateFlow<Boolean>,
    userProfileDataStateFlow: StateFlow<MyProfileData>,
    navigateToLoginScreen: () -> Unit,
    logout: () -> Unit,
    navigateToViewProfileScreen: () -> Unit,
    navigateToEditProfileScreen: () -> Unit,
    navigateToSettingsScreen: () -> Unit,
    navigateToAboutScreen: () -> Unit
) {
    val isUserLoggedIn by isUserLoggedInStateFlow.collectAsState()

    Surface(
        color = MaterialTheme.colors.primary,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(12.dp)
                .animateContentSize()
        ) {
            if (isUserLoggedIn) {
                var showLogoutConfirmationDialog by remember { mutableStateOf(false) }

                val userProfileData by userProfileDataStateFlow.collectAsState()

                LoggedInUserSection(
                    userProfilePhotoUrl = userProfileData.profilePhotoUrl,
                    username = stringResource(
                        id = R.string.user_full_name_formatted,
                        userProfileData.firstName,
                        userProfileData.lastName
                    ),
                    userNickname = userProfileData.nickname,
                    userEmail = userProfileData.email,
                    navigateToViewProfileScreen = navigateToViewProfileScreen,
                    navigateToEditProfileScreen = navigateToEditProfileScreen,
                    onLogoutClick = { showLogoutConfirmationDialog = true }
                )

                if (showLogoutConfirmationDialog) {
                    LogoutConfirmationDialog(
                        onLogout = logout,
                        onDismiss = { showLogoutConfirmationDialog = false }
                    )
                }
            } else {
                LoggedOutUserSection(navigateToLoginScreen = navigateToLoginScreen)
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )

            WTextButton(
                onClick = navigateToSettingsScreen,
                iconPainter = painterResource(id = R.drawable.ic_settings_outlined),
                text = stringResource(id = R.string.settings),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            Spacer(modifier = Modifier.padding(bottom = 4.dp))

            WTextButton(
                onClick = navigateToAboutScreen,
                iconPainter = painterResource(id = R.drawable.ic_about_outlined),
                text = stringResource(id = R.string.about),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )
        }
    }
}

@Composable
fun LoggedOutUserSection(navigateToLoginScreen: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var showAddAccountSection by remember {
            mutableStateOf(false)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = { showAddAccountSection = !showAddAccountSection })
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(id = R.string.app_name),
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.padding(4.dp))

            Column {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.h6,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.creation_starts_here),
                        style = MaterialTheme.typography.subtitle2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )

                    Icon(
                        painter = painterResource(
                            id = if (showAddAccountSection) R.drawable.ic_arrow_up_alt
                            else R.drawable.ic_arrow_down_alt
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        AnimatedVisibility(visible = showAddAccountSection) {
            WTextButton(
                onClick = navigateToLoginScreen,
                iconPainter = painterResource(id = R.drawable.ic_add_outlined),
                text = stringResource(id = R.string.add_account),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )
        }
    }
}

@Composable
fun LoggedInUserSection(
    userProfilePhotoUrl: String,
    username: String,
    userNickname: String,
    userEmail: String,
    navigateToViewProfileScreen: () -> Unit,
    navigateToEditProfileScreen: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(userProfilePhotoUrl)
                .crossfade(true)
                .placeholder(ColorDrawable(Color.GRAY))
                .build(),
            contentDescription = stringResource(id = R.string.user_profile_image),
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )

        Text(
            text = username,
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = stringResource(id = R.string.user_nickname_formatted, userNickname),
            style = MaterialTheme.typography.subtitle2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = userEmail,
            style = MaterialTheme.typography.subtitle2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WTextButton(
            onClick = navigateToViewProfileScreen,
            iconPainter = painterResource(id = R.drawable.ic_view_outlined),
            text = stringResource(id = R.string.user_profile_view),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(0.33f)
        )

        Spacer(modifier = Modifier.padding(end = 4.dp))

        WTextButton(
            onClick = navigateToEditProfileScreen,
            iconPainter = painterResource(id = R.drawable.ic_edit_outlined),
            text = stringResource(id = R.string.user_profile_edit),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(0.33f)
        )

        Spacer(modifier = Modifier.padding(end = 4.dp))

        WTextButton(
            onClick = onLogoutClick,
            iconPainter = painterResource(id = R.drawable.ic_logout_outlined),
            text = stringResource(id = R.string.logout),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(0.33f)
        )
    }
}

@Composable
fun LogoutConfirmationDialog(
    onLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.logout)) },
        text = { Text(text = stringResource(id = R.string.logout_confirmation)) },
        shape = RoundedCornerShape(16.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            WTextButton(
                onClick = onLogout,
                text = stringResource(id = R.string.action_yes)
            )
        },
        dismissButton = {
            WTextButton(
                onClick = onDismiss,
                text = stringResource(id = R.string.action_no)
            )
        }
    )
}