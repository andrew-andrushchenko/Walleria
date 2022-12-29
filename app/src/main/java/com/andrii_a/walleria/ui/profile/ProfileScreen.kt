package com.andrii_a.walleria.ui.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.animateContentSize
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

                UserProfileHeader(
                    userProfilePhotoUrl = userProfileData.profilePhotoUrl,
                    username = stringResource(
                        id = R.string.user_full_name_formatted,
                        userProfileData.firstName,
                        userProfileData.lastName
                    ),
                    userNickname = userProfileData.nickname,
                    userEmail = userProfileData.email
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WTextButton(
                        onClick = navigateToViewProfileScreen,
                        iconPainter = painterResource(id = R.drawable.ic_view_outlined),
                        text = stringResource(id = R.string.user_profile_view),
                        modifier = Modifier.weight(0.33f)
                    )

                    Spacer(modifier = Modifier.padding(end = 4.dp))

                    WTextButton(
                        onClick = navigateToEditProfileScreen,
                        iconPainter = painterResource(id = R.drawable.ic_edit_outlined),
                        text = stringResource(id = R.string.user_profile_edit),
                        modifier = Modifier.weight(0.33f)
                    )

                    Spacer(modifier = Modifier.padding(end = 4.dp))

                    WTextButton(
                        onClick = { showLogoutConfirmationDialog = true },
                        iconPainter = painterResource(id = R.drawable.ic_logout_outlined),
                        text = stringResource(id = R.string.logout),
                        modifier = Modifier.weight(0.33f)
                    )

                    if (showLogoutConfirmationDialog) {
                        LogoutConfirmationDialog(
                            onLogout = logout,
                            onDismiss = { showLogoutConfirmationDialog = false }
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(id = R.string.login_rationale),
                    style = MaterialTheme.typography.h6,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )

                WTextButton(
                    onClick = navigateToLoginScreen,
                    iconPainter = painterResource(id = R.drawable.ic_login_outlined),
                    text = stringResource(id = R.string.login),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )

            WTextButton(
                onClick = navigateToAboutScreen,
                iconPainter = painterResource(id = R.drawable.ic_about_outlined),
                text = stringResource(id = R.string.about),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            )
        }
    }
}

@Composable
fun UserProfileHeader(
    userProfilePhotoUrl: String,
    username: String,
    userNickname: String,
    userEmail: String
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
}

@Composable
fun LogoutConfirmationDialog(
    onLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = { Text(text = stringResource(id = R.string.logout)) },
        text = { Text(text = stringResource(id = R.string.logout_confirmation)) },
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