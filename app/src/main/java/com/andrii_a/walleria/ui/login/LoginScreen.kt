package com.andrii_a.walleria.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.common.components.LoadingListItem
import com.andrii_a.walleria.ui.theme.WalleriaLogoTextStyle
import com.andrii_a.walleria.ui.theme.WalleriaTheme

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEvent: (LoginEvent) -> Unit
) {
    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = { onEvent(LoginEvent.GoBack) },
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = stringResource(id = R.string.continue_as_a_guest),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(min = 250.dp, max = 350.dp)
                    .verticalScroll(rememberScrollState())
                    .animateContentSize()
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = WalleriaLogoTextStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onEvent(LoginEvent.PerformLogin) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Text(text = stringResource(id = R.string.login))
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { onEvent(LoginEvent.PerformJoin) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Text(text = stringResource(id = R.string.sign_up))
                }

                AnimatedVisibility(visible = state.isLoading) {
                    Spacer(modifier = Modifier.height(8.dp))

                    LoadingListItem()
                }
            }
        }
    }
}


@PreviewScreenSizes
@Composable
fun LoginScreenPreview() {
    WalleriaTheme {
        LoginScreen(
            state = LoginUiState(isLoading = true),
            onEvent = {}
        )
    }
}
