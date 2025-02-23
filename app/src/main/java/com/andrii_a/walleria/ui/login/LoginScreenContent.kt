package com.andrii_a.walleria.ui.login

import android.graphics.Bitmap
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.ui.common.components.LoadingBanner
import com.andrii_a.walleria.ui.theme.WalleriaLogoTextStyle
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.BlurHashDecoder
import com.andrii_a.walleria.ui.util.accentColorForLoginScreen
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.primaryColorInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LoginScreenContent(
    state: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(modifier = modifier) {
        val placeholderBitmap by produceState<Bitmap?>(initialValue = null) {
            value = withContext(Dispatchers.Default) {
                BlurHashDecoder.decode(
                    blurHash = state.bannerPhoto?.blurHash,
                    width = 4,
                    height = 3
                )
            }
        }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(state.bannerPhoto?.getUrlByQuality(quality = PhotoQuality.HIGH))
                .crossfade(durationMillis = 1000)
                .placeholder(placeholderBitmap?.toDrawable(context.resources))
                .fallback(placeholderBitmap?.toDrawable(context.resources))
                .error(ColorDrawable(state.bannerPhoto?.primaryColorInt ?: Color.Gray.toArgb()))
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
            onNavigateBack = { onEvent(LoginEvent.GoBack) },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        BottomSection(
            accentColor = state.bannerPhoto?.accentColorForLoginScreen ?: Color.White,
            onLoginClicked = { onEvent(LoginEvent.PerformLogin) },
            onJoinClicked = { onEvent(LoginEvent.PerformJoin) },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )

        if (state.isLoading) {
            LoadingBanner(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
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
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }

        Text(
            text = stringResource(id = R.string.app_name),
            style = WalleriaLogoTextStyle,
            color = Color.White,
            modifier = Modifier.constrainAs(appLogoText) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end, 18.dp)
            }
        )
    }
}

@Composable
private fun BottomSection(
    accentColor: Color,
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.login_screen_greetings_text),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(Color.White)) {
                        append(stringResource(id = R.string.login_screen_slogan_p1))
                    }

                    withStyle(style = SpanStyle(accentColor)) {
                        append(stringResource(id = R.string.login_screen_slogan_p2))
                    }
                },
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLoginClicked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = contentColorFor(accentColor)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 32.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.login),
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(id = R.string.dont_have_an_account),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(id = R.string.join),
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onJoinClicked() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@PreviewScreenSizes
@Composable
fun LoginScreenContentPreview() {
    WalleriaTheme {
        LoginScreenContent(
            state = LoginUiState(),
            onEvent = {}
        )
    }
}
