package com.andrii_a.walleria.ui.login

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.login.AccessToken
import com.andrii_a.walleria.ui.common.components.LoadingBanner
import com.andrii_a.walleria.ui.theme.LoginScreenAccentColor
import com.andrii_a.walleria.ui.theme.PrimaryDark
import com.andrii_a.walleria.ui.theme.PrimaryLight
import com.andrii_a.walleria.ui.theme.WalleriaLogoTextStyle
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.toast

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
                .data(stringResource(id = R.string.login_screen_image_asset_url))
                .crossfade(durationMillis = 1000)
                .placeholder(ColorDrawable(LoginScreenAccentColor.toArgb()))
                .build(),
            contentDescription = stringResource(id = R.string.topic_cover_photo),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
                }
        )

        TopSection(
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        BottomSection(
            onLoginClicked = onLoginClicked,
            onJoinClicked = onJoinClicked,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )

        when (loginState) {
            is LoginState.Empty -> Unit
            is LoginState.Loading -> {
                LoadingBanner(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PrimaryDark.copy(alpha = 0.4f))
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
private fun TopSection(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (backButton, appLogoText) = createRefs()

        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.constrainAs(backButton) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, 8.dp)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = PrimaryLight
            )
        }

        Text(
            text = stringResource(id = R.string.app_name),
            style = WalleriaLogoTextStyle,
            color = PrimaryLight,
            modifier = Modifier.constrainAs(appLogoText) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end, 18.dp)
            }
        )
    }
}

@Preview
@Composable
fun TopSectionPreview() {
    WalleriaTheme {
        Surface(color = Color.Black.copy(alpha = 0.5f)) {
            TopSection(
                onNavigateBack = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BottomSection(
    onLoginClicked: () -> Unit,
    onJoinClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.login_screen_slogan_p1),
                color = PrimaryLight,
                style = MaterialTheme.typography.h4
            )

            Text(
                text = stringResource(id = R.string.login_screen_slogan_p2),
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
                    color = PrimaryLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.dont_have_an_account),
                    fontWeight = FontWeight.Bold,
                    color = PrimaryLight
                )

                Spacer(modifier = Modifier.padding(end = 4.dp))

                Text(
                    text = stringResource(id = R.string.join),
                    fontWeight = FontWeight.Bold,
                    color = LoginScreenAccentColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onJoinClicked() }
                )
            }
        }
    }
}
