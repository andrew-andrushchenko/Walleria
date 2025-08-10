package com.andrii_a.walleria.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.theme.WalleriaLogoTextStyle
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.openGithubProfile
import com.andrii_a.walleria.ui.util.openInstagramProfile
import com.andrii_a.walleria.ui.util.writeLetterTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.about))
                },
                navigationIcon = {
                    FilledTonalIconButton(
                        onClick = navigateBack,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        AboutScreenContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
fun AboutScreenContent(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = WalleriaLogoTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(id = R.string.powered_by_unsplash),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )

        Text(
            text = "Version 1.0",
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.developer_username),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "Copyright @ 2025",
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        DeveloperContactRow(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun DeveloperContactRow(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        FilledIconButton(onClick = { context.openGithubProfile(context.getString(R.string.developer_github_username)) }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_github),
                contentDescription = stringResource(id = R.string.developer_github_username)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        FilledIconButton(onClick = { context.writeLetterTo(context.getString(R.string.developer_email)) }) {
            Icon(
                imageVector = Icons.Outlined.MailOutline,
                contentDescription = stringResource(id = R.string.developer_email)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        FilledIconButton(onClick = { context.openInstagramProfile(context.getString(R.string.developer_instagram_username)) }) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_instagram_outlined),
                contentDescription = stringResource(id = R.string.developer_instagram_username)
            )
        }
    }
}

@Preview
@Composable
fun AboutScreenPreview() {
    WalleriaTheme {
        Surface {
            AboutScreen(navigateBack = {} /*openPhoto = {}*/)
        }
    }
}