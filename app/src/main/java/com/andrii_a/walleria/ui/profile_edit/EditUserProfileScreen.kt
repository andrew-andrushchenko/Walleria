package com.andrii_a.walleria.ui.profile_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.theme.WalleriaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserProfileScreen(
    state: EditUserProfileUiState,
    onEvent: (EditUserProfileEvent) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.edit_my_profile))
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(EditUserProfileEvent.GoBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
                        )
                    }
                },
                actions = {
                    val keyboardController = LocalSoftwareKeyboardController.current

                    IconButton(
                        onClick = {
                            keyboardController?.hide()
                            onEvent(EditUserProfileEvent.SaveProfile)
                        },
                        enabled = state.isInputValid,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = stringResource(id = R.string.save_my_profile_data)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .width(450.dp)
                    .widthIn(min = 200.dp, max = 600.dp)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
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
                    supportingText = {
                        Text(text = stringResource(id = R.string.user_nickname_error_text))
                    },
                    isError = !state.isNicknameValid,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.firstName,
                    onValueChange = { onEvent(EditUserProfileEvent.FirstNameChanged(it)) },
                    label = {
                        Text(text = stringResource(id = R.string.first_name_hint))
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.lastName,
                    onValueChange = { onEvent(EditUserProfileEvent.LastNameChanged(it)) },
                    label = {
                        Text(text = stringResource(id = R.string.last_name_hint))
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = { onEvent(EditUserProfileEvent.EmailChanged(it)) },
                    label = {
                        Text(text = stringResource(id = R.string.email_hint))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.MailOutline,
                            contentDescription = stringResource(id = R.string.email_hint)
                        )
                    },
                    isError = !state.isEmailValid,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.portfolioLink,
                    onValueChange = { onEvent(EditUserProfileEvent.PortfolioLinkChanged(it)) },
                    label = {
                        Text(text = stringResource(id = R.string.portfolio_hint))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.WorkOutline,
                            contentDescription = stringResource(id = R.string.portfolio_hint)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
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

                OutlinedTextField(
                    value = state.location,
                    onValueChange = { onEvent(EditUserProfileEvent.LocationChanged(it)) },
                    label = {
                        Text(text = stringResource(id = R.string.location_hint))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = stringResource(id = R.string.location_hint)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.bio,
                    onValueChange = { onEvent(EditUserProfileEvent.BioChanged(it)) },
                    label = {
                        Text(text = stringResource(id = R.string.bio_hint))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.EditNote,
                            contentDescription = stringResource(id = R.string.bio_hint)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun EditUserProfileScreenPreview() {
    WalleriaTheme {
        EditUserProfileScreen(
            state = EditUserProfileUiState(),
            onEvent = {}
        )
    }
}