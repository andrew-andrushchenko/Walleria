package com.andrii_a.walleria.ui.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.andrii_a.walleria.domain.models.preferences.MyProfileData
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.theme.WalleriaTheme

@Composable
fun ProfileScreen(
    isUserLoggedIn: Boolean,
    userProfileData: MyProfileData,
    navigateToLoginScreen: () -> Unit,
    onLogout: () -> Unit,
    navigateToViewProfileScreen: (UserNickname) -> Unit,
    navigateToEditProfileScreen: () -> Unit,
    navigateToSettingsScreen: () -> Unit,
    navigateToAboutScreen: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .navigationBarsPadding()
            .animateContentSize()
    ) {
        if (isUserLoggedIn) {
            LoggedInHeader(
                userProfilePhotoUrl = userProfileData.profilePhotoUrl,
                userFullName = stringResource(
                    id = R.string.user_full_name_formatted,
                    userProfileData.firstName,
                    userProfileData.lastName
                ),
                userNickname = userProfileData.nickname,
                userEmail = userProfileData.email,
                navigateToViewProfileScreen = {
                    navigateToViewProfileScreen(
                        UserNickname(userProfileData.nickname)
                    )
                },
                navigateToEditProfileScreen = navigateToEditProfileScreen,
                onLogout = onLogout
            )

        } else {
            LoggedOutHeader(
                navigateToLoginScreen = navigateToLoginScreen,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        )

        TextButton(
            onClick = navigateToSettingsScreen,
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

        Spacer(modifier = Modifier.height(4.dp))

        TextButton(
            onClick = navigateToAboutScreen,
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
        var showAddAccountSection by remember {
            mutableStateOf(false)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = { showAddAccountSection = !showAddAccountSection })
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Column {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.creation_starts_here),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )

                    Icon(
                        imageVector = if (showAddAccountSection) {
                            Icons.Default.ArrowDropUp
                        } else {
                            Icons.Default.ArrowDropDown
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        AnimatedVisibility(visible = showAddAccountSection) {
            TextButton(
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
}

@Composable
private fun LoggedInHeader(
    userProfilePhotoUrl: String,
    userFullName: String,
    userNickname: String,
    userEmail: String,
    navigateToViewProfileScreen: () -> Unit,
    navigateToEditProfileScreen: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
            text = userFullName,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = stringResource(id = R.string.user_nickname_formatted, userNickname),
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = userEmail,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    var showConfirmationRow by remember {
        mutableStateOf(false)
    }

    AnimatedContent(
        targetState = showConfirmationRow,
        label = "",
        modifier = Modifier.fillMaxWidth()
    ) {
        if (it) {
            LogoutConfirmationRow(
                onConfirm = onLogout,
                onDismiss = { showConfirmationRow = false },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            ProfileActionRow(
                navigateToViewProfileScreen = navigateToViewProfileScreen,
                navigateToEditProfileScreen = navigateToEditProfileScreen,
                onLogoutClick = { showConfirmationRow = true },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun ProfileActionRow(
    navigateToViewProfileScreen: () -> Unit,
    navigateToEditProfileScreen: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TextButton(
            onClick = navigateToViewProfileScreen,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(0.33f)
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

        TextButton(
            onClick = navigateToEditProfileScreen,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(0.33f)
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

        TextButton(
            onClick = onLogoutClick,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(0.33f)
        ) {
            Icon(
                imageVector = Icons.Outlined.Logout,
                contentDescription = stringResource(id = R.string.logout)
            )

            Spacer(modifier = Modifier.width(8.dp))

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

@Preview
@Composable
fun ProfileScreenPreview() {
    WalleriaTheme {
        var isUserLoggedIn by remember {
            mutableStateOf(false)
        }

        Surface {
            ProfileScreen(
                isUserLoggedIn = isUserLoggedIn,
                userProfileData = MyProfileData(
                    nickname = "john",
                    firstName = "John",
                    lastName = "Smith",
                    email = "john.smith@example.com"
                ),
                navigateToLoginScreen = { isUserLoggedIn = true },
                onLogout = { isUserLoggedIn = false },
                navigateToViewProfileScreen = {},
                navigateToEditProfileScreen = {},
                navigateToSettingsScreen = {},
                navigateToAboutScreen = {}
            )
        }
    }
}