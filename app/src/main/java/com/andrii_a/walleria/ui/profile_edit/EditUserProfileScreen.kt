package com.andrii_a.walleria.ui.profile_edit

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.common.WButton
import com.andrii_a.walleria.ui.common.WOutlinedTextField
import com.andrii_a.walleria.ui.theme.WalleriaTheme

@Composable
fun EditUserProfileScreen(
    state: EditUserProfileScreenState,
    onEvent: (EditUserProfileEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(
                        top = dimensionResource(id = R.dimen.top_bar_height),
                        start = 16.dp,
                        end = WindowInsets.navigationBars
                            .asPaddingValues()
                            .calculateEndPadding(LayoutDirection.Ltr) + 16.dp
                    )
                    .verticalScroll(rememberScrollState())
            ) {
                Column(modifier = Modifier.animateContentSize()) {
                    WOutlinedTextField(
                        value = state.nickname,
                        onValueChange = { onEvent(EditUserProfileEvent.NicknameChanged(it)) },
                        label = {
                            Text(text = stringResource(id = R.string.user_nickname_hint))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AlternateEmail,
                                contentDescription = stringResource(id = R.string.user_nickname_hint)
                            )
                        },
                        isError = !state.isNicknameValid,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!state.isNicknameValid) {
                        Text(
                            text = stringResource(id = R.string.user_nickname_error_text),
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                }

                WOutlinedTextField(
                    value = state.firstName,
                    onValueChange = { onEvent(EditUserProfileEvent.FirstNameChanged(it)) },
                    label = {
                        Text(text = stringResource(id = R.string.first_name_hint))
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                WOutlinedTextField(
                    value = state.lastName,
                    onValueChange = { onEvent(EditUserProfileEvent.LastNameChanged(it)) },
                    label = {
                        Text(text = stringResource(id = R.string.last_name_hint))
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Column(modifier = Modifier.animateContentSize()) {
                    WOutlinedTextField(
                        value = state.email,
                        onValueChange = { onEvent(EditUserProfileEvent.EmailChanged(it)) },
                        label = {
                            Text(text = stringResource(id = R.string.email_hint))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_email_outlined),
                                contentDescription = stringResource(id = R.string.email_hint)
                            )
                        },
                        isError = !state.isEmailValid,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!state.isEmailValid) {
                        Text(
                            text = stringResource(id = R.string.enter_valid_email),
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }

                WOutlinedTextField(
                    value = state.portfolioLink,
                    onValueChange = { onEvent(EditUserProfileEvent.PortfolioLinkChanged(it)) },
                    label = {
                        Text(text = stringResource(id = R.string.portfolio_hint))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_web_outlined),
                            contentDescription = stringResource(id = R.string.portfolio_hint)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                WOutlinedTextField(
                    value = state.instagramUsername,
                    onValueChange = { onEvent(EditUserProfileEvent.InstagramUsernameChanged(it)) },
                    label = {
                        Text(text = stringResource(id = R.string.instagram_username_hint))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_instagram_outlined),
                            contentDescription = stringResource(id = R.string.instagram_username_hint)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                WOutlinedTextField(
                    value = state.location,
                    onValueChange = { onEvent(EditUserProfileEvent.LocationChanged(it)) },
                    label = {
                        Text(text = stringResource(id = R.string.location_hint))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = stringResource(id = R.string.location_hint)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                WOutlinedTextField(
                    value = state.bio,
                    onValueChange = { onEvent(EditUserProfileEvent.BioChanged(it)) },
                    label = {
                        Text(text = stringResource(id = R.string.bio_hint))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit_outlined),
                            contentDescription = stringResource(id = R.string.bio_hint)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TopBar(
                onSaveUserProfileData = {
                    onEvent(EditUserProfileEvent.SaveProfile)
                },
                onNavigateBack = onNavigateBack,
                isInputValid = state.isInputValid
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TopBar(
    onNavigateBack: () -> Unit,
    onSaveUserProfileData: () -> Unit,
    isInputValid: Boolean,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .height(
                dimensionResource(id = R.dimen.top_bar_height) +
                        WindowInsets.systemBars
                            .asPaddingValues()
                            .calculateTopPadding()
            )
            .fillMaxWidth()
            .padding(
                end = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateEndPadding(LayoutDirection.Ltr)
            )
    ) {
        val (backButton, title, editButton) = createRefs()

        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .constrainAs(backButton) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start, 8.dp)
                    end.linkTo(title.start)
                }
                .statusBarsPadding()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = stringResource(id = R.string.navigate_back)
            )
        }

        Text(
            text = stringResource(id = R.string.edit_my_profile),
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(backButton.top)
                    bottom.linkTo(backButton.bottom)
                    start.linkTo(backButton.end, 8.dp)
                    end.linkTo(editButton.start)
                    width = Dimension.fillToConstraints
                }
                .statusBarsPadding()
        )

        val keyboardController = LocalSoftwareKeyboardController.current

        WButton(
            onClick = {
                keyboardController?.hide()
                onSaveUserProfileData()
            },
            enabled = isInputValid,
            modifier = Modifier
                .constrainAs(editButton) {
                    top.linkTo(title.top)
                    bottom.linkTo(title.bottom)
                    end.linkTo(parent.end, 16.dp)
                }
                .statusBarsPadding()
        ) {
            Text(text = stringResource(id = R.string.save_my_profile_data))
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun EditUserProfileScreenPreview() {
    WalleriaTheme {
        EditUserProfileScreen(
            state = EditUserProfileScreenState(),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}