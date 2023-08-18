package com.andrii_a.walleria.ui.common.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.theme.WalleriaTheme

@Composable
fun LoadingListItem(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.TopCenter) {
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_list_item_animation))

        LottieAnimation(
            composition = composition,
            iterations = Int.MAX_VALUE,
            modifier = modifier
                .requiredSize(64.dp)
                .scale(2f, 2f)
        )
    }
}

@Composable
fun LoadingBanner(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_animation_light))

        LottieAnimation(
            composition = composition,
            iterations = Int.MAX_VALUE
        )
    }
}

@Composable
fun ErrorBanner(
    modifier: Modifier = Modifier,
    message: String = stringResource(id = R.string.error_banner_text),
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        val composition by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(
                if (isSystemInDarkTheme()) R.raw.error_animation_dark
                else R.raw.error_animation_light
            )
        )

        LottieAnimation(
            composition = composition,
            iterations = Int.MAX_VALUE,
            modifier = Modifier
                .requiredSize(250.dp)
                .scale(1.3f, 1.3f)
        )

        Text(
            text = message,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 32.dp, end = 32.dp)
        )

        Spacer(modifier = Modifier.padding(bottom = 8.dp))

        Button(onClick = onRetry) {
            Text(text = stringResource(id = R.string.action_retry))
        }
    }
}

@Composable
fun ErrorItem(
    modifier: Modifier = Modifier,
    message: String = stringResource(id = R.string.error_loading_items),
    onRetry: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (text, button) = createRefs()

            Text(
                text = message,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(text) {
                    top.linkTo(parent.top, 16.dp)
                    bottom.linkTo(parent.bottom, 16.dp)
                    start.linkTo(parent.start, 16.dp)
                    end.linkTo(button.start, 8.dp)

                    width = Dimension.fillToConstraints
                }
            )

            Button(
                onClick = onRetry,
                modifier = Modifier.constrainAs(button) {
                    top.linkTo(text.top, 8.dp)
                    bottom.linkTo(text.bottom, 8.dp)
                    start.linkTo(text.end)
                    end.linkTo(parent.end, 16.dp)
                }
            ) {
                Text(text = stringResource(id = R.string.action_retry))
            }
        }
    }
}

@Preview
@Composable
fun ErrorItemPreview() {
    WalleriaTheme {
        ErrorItem(onRetry = {})
    }
}

@Composable
fun EmptyContentBanner(
    modifier: Modifier = Modifier,
    message: String = stringResource(id = R.string.empty_content_banner_text)
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val composition by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(
                if (isSystemInDarkTheme()) R.raw.empty_animation_dark
                else R.raw.empty_animation_light
            )
        )

        LottieAnimation(
            composition = composition,
            iterations = 1,
            modifier = Modifier
                .requiredSize(250.dp)
                .scale(1.6f, 1.6f)
        )

        Text(
            text = message,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}