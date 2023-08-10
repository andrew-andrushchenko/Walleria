package com.andrii_a.walleria.ui.profile

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import com.andrii_a.walleria.ui.common.WButton
import com.andrii_a.walleria.ui.common.WTextButton
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .navigationBarsPadding()
                .animateContentSize()
        ) {
            Spacer(
                modifier = Modifier
                    .padding(vertical = 22.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .background(
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(50)
                    )
                    .align(Alignment.CenterHorizontally)
            )

            if (isUserLoggedIn) {
                LoggedInUserSection(
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
                LoggedOutUserSection(
                    navigateToLoginScreen = navigateToLoginScreen,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )

            WTextButton(
                onClick = navigateToSettingsScreen,
                iconPainter = painterResource(id = R.drawable.ic_settings_outlined),
                text = stringResource(id = R.string.settings),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            WTextButton(
                onClick = navigateToAboutScreen,
                iconPainter = painterResource(id = R.drawable.ic_about_outlined),
                text = stringResource(id = R.string.about),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LoggedOutUserSection(
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
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

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
private fun LoggedInUserSection(
    userProfilePhotoUrl: String,
    userFullName: String,
    userNickname: String,
    userEmail: String,
    navigateToViewProfileScreen: () -> Unit,
    navigateToEditProfileScreen: () -> Unit,
    onLogout: () -> Unit
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
            text = userFullName,
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
        WTextButton(
            onClick = navigateToViewProfileScreen,
            iconPainter = painterResource(id = R.drawable.ic_view_outlined),
            text = stringResource(id = R.string.user_profile_view),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(0.33f)
        )

        Spacer(modifier = Modifier.width(4.dp))

        WTextButton(
            onClick = navigateToEditProfileScreen,
            iconPainter = painterResource(id = R.drawable.ic_edit_outlined),
            text = stringResource(id = R.string.user_profile_edit),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(0.33f)
        )

        Spacer(modifier = Modifier.width(4.dp))

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
private fun LogoutConfirmationRow(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (confirmationText, confirmButton, dismissButton) = createRefs()

        Text(
            text = stringResource(id = R.string.logout_confirmation),
            style = MaterialTheme.typography.subtitle1,
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
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface
            )
        }

        WButton(
            onClick = onDismiss,
            modifier = Modifier.constrainAs(dismissButton) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        ) {
            Text(
                stringResource(id = R.string.action_no),
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ProfileScreenPreview() {
    WalleriaTheme {
        var isUserLoggedIn by remember {
            mutableStateOf(false)
        }

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