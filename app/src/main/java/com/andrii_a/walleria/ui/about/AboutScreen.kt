package com.andrii_a.walleria.ui.about

import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.openGithubProfile
import com.andrii_a.walleria.ui.util.openInstagramProfile
import com.andrii_a.walleria.ui.util.openLinkInBrowser
import com.andrii_a.walleria.ui.util.writeALetterTo

@Composable
fun AboutScreen(
    navigateBack: () -> Unit,
    openPhoto: (PhotoId) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AboutScreenContent(
            openPhoto = openPhoto,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = dimensionResource(id = R.dimen.top_bar_height))
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        )

        TopBar(
            navigateBack = navigateBack,
            modifier = Modifier
                .statusBarsPadding()
                .background(color = MaterialTheme.colors.primary)
                .height(dimensionResource(id = R.dimen.top_bar_height))
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun TopBar(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(onClick = navigateBack) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = stringResource(id = R.string.navigate_back)
            )
        }

        Text(
            text = stringResource(id = R.string.about),
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun AboutScreenContent(
    openPhoto: (PhotoId) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(id = R.string.app_name),
            tint = MaterialTheme.colors.onSurface,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h6,
            maxLines = 1
        )

        Text(
            text = stringResource(id = R.string.powered_by_unsplash),
            style = MaterialTheme.typography.subtitle2,
            maxLines = 1
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Text(
            text = stringResource(id = R.string.developed_and_designed_by),
            style = MaterialTheme.typography.subtitle2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = stringResource(id = R.string.developer_username),
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { context.openGithubProfile(context.getString(R.string.developer_github_username)) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_github),
                    contentDescription = stringResource(id = R.string.developer_github_username)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { context.writeALetterTo(context.getString(R.string.developer_email)) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_email_outlined),
                    contentDescription = stringResource(id = R.string.developer_email)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { context.openInstagramProfile(context.getString(R.string.developer_instagram_username)) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_instagram_outlined),
                    contentDescription = stringResource(id = R.string.developer_instagram_username)
                )
            }
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Text(
            text = stringResource(id = R.string.login_screen_photo_owner),
            style = MaterialTheme.typography.subtitle1,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clickable {
                    openPhoto(PhotoId("9fHMo1-5Io8"))
                }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.project_icons),
            style = MaterialTheme.typography.subtitle1,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clickable {
                    val link = Uri.decode(context.getString(R.string.link_to_icons_project))
                    context.openLinkInBrowser(link)
                }
                .padding(8.dp)
        )
    }
}

@Preview
@Composable
fun AboutScreenPreview() {
    WalleriaTheme {
        Surface {
            AboutScreen(navigateBack = {}, openPhoto = {})
        }
    }
}