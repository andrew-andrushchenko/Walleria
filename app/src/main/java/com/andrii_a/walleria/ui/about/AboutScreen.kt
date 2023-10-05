package com.andrii_a.walleria.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.openGithubProfile
import com.andrii_a.walleria.ui.util.openInstagramProfile
import com.andrii_a.walleria.ui.util.writeALetterTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navigateBack: () -> Unit,
    //openPhoto: (PhotoId) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.about))
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        AboutScreenContent(
            //openPhoto = openPhoto,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
fun AboutScreenContent(
    //openPhoto: (PhotoId) -> Unit,
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
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.powered_by_unsplash),
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Text(
            text = stringResource(id = R.string.developed_and_designed_by),
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.developer_username),
            style = MaterialTheme.typography.titleLarge,
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
                    imageVector = Icons.Outlined.MailOutline,
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
    }
}

@Preview
@Composable
fun AboutScreenPreview() {
    WalleriaTheme {
        Surface {
            AboutScreen(navigateBack = {}, /*openPhoto = {}*/)
        }
    }
}