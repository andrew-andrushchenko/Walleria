package com.andrii_a.walleria.ui.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.theme.WalleriaTheme

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
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.connection),
            contentDescription = "",
            colorFilter = ColorFilter.lighting(
                multiply = Color.Gray,
                add = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.size(150.dp)
        )

        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
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
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.empty_content),
            contentDescription = "",
            colorFilter = ColorFilter.lighting(
                multiply = Color.Gray,
                add = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Preview
@Composable
fun EmptyContentBannerPreview() {
    WalleriaTheme {
        Surface {
            EmptyContentBanner(
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
fun ErrorBannerPreview() {
    WalleriaTheme {
        Surface {
            ErrorBanner(
                modifier = Modifier.padding(16.dp),
                onRetry = {}
            )
        }
    }
}
