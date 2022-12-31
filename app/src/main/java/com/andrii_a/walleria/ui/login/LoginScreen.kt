package com.andrii_a.walleria.ui.login

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.ui.common.LoadingBanner
import com.andrii_a.walleria.ui.theme.LoginScreenAccentColor
import com.andrii_a.walleria.ui.util.toast

private const val LOGIN_IMAGE_ASSET_URL = "file:///android_asset/login_screen_bg.jpg"

@Composable
fun LoginScreen(
    loginState: LoginState,
    retrieveUserData: (AccessToken) -> Unit,
    onLoginClicked: () -> Unit,
    onJoinClicked: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(LOGIN_IMAGE_ASSET_URL)
                .crossfade(true)
                .placeholder(ColorDrawable(android.graphics.Color.GRAY))
                .build(),
            contentDescription = stringResource(id = R.string.topic_cover_photo),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        TopSection(onNavigateBack = onNavigateBack)

        BottomSection(onLoginClicked = onLoginClicked, onJoinClicked = onJoinClicked)

        when (loginState) {
            is LoginState.Empty -> Unit
            is LoginState.Loading -> {
                LoadingBanner(
                    indicatorColor = Color.White,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.primary.copy(alpha = 0.4f))
                )
            }
            is LoginState.Error -> {
                LocalContext.current.toast(R.string.login_failed)
            }
            is LoginState.Success -> {
                retrieveUserData(loginState.accessToken)

                LocalContext.current.toast(R.string.login_successful)
                onNavigateBack()
            }
        }
    }
}

@Composable
fun TopSection(onNavigateBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = MaterialTheme.colors.primary
            )
        }

        Text(
            text = "Walleria",
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.primary
        )
    }
}

@Composable
fun BottomSection(
    onLoginClicked: () -> Unit,
    onJoinClicked: () -> Unit
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                )
            )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Capture love, joy and",
                color = Color.White,
                style = MaterialTheme.typography.h4
            )

            Text(
                text = "everything in between",
                color = LoginScreenAccentColor,
                style = MaterialTheme.typography.h4
            )

            Spacer(modifier = Modifier.padding(top = 16.dp))

            Button(
                onClick = onLoginClicked,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(backgroundColor = LoginScreenAccentColor),
                modifier = Modifier
                    .size(width = 250.dp, height = 80.dp)
                    .padding(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.login),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 24.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = stringResource(id = R.string.dont_have_an_account),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.padding(start = 4.dp, end = 4.dp))

                Text(
                    text = stringResource(id = R.string.join),
                    fontWeight = FontWeight.Bold,
                    color = LoginScreenAccentColor,
                    modifier = Modifier.clickable { onJoinClicked() }
                )
            }
        }
    }
}