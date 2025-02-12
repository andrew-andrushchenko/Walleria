package com.andrii_a.walleria.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.titleRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentPhotosLoadQuality: PhotoQuality,
    currentPhotosDownloadQuality: PhotoQuality,
    onEvent: (SettingsEvent) -> Unit,
    navigateBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.settings))
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(
                                id = R.string.navigate_back
                            )
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            SettingsGroup(name = stringResource(id = R.string.load_settings)) {
                SettingsItem(
                    title = stringResource(id = R.string.photo_load_quality),
                    selectedValue = stringResource(id = currentPhotosLoadQuality.titleRes),
                    selectionOptions = PhotoQuality.entries
                        .map { stringResource(id = it.titleRes) },
                    selectedItemPositionOrdinal = currentPhotosLoadQuality.ordinal,
                    onChangeParameter = { selectedPhotoQualityOrdinal ->
                        onEvent(
                            SettingsEvent.UpdatePhotosLoadQuality(
                                PhotoQuality.entries[selectedPhotoQualityOrdinal]
                            )
                        )
                    }
                )

                SettingsItem(
                    title = stringResource(id = R.string.photo_download_quality),
                    selectedValue = stringResource(id = currentPhotosDownloadQuality.titleRes),
                    selectionOptions = PhotoQuality.entries
                        .map { stringResource(id = it.titleRes) },
                    selectedItemPositionOrdinal = currentPhotosDownloadQuality.ordinal,
                    onChangeParameter = { selectedPhotoQualityOrdinal ->
                        onEvent(
                            SettingsEvent.UpdatePhotosDownloadQuality(
                                PhotoQuality.entries[selectedPhotoQualityOrdinal]
                            )
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    WalleriaTheme {
        Surface {
            SettingsScreen(
                currentPhotosLoadQuality = PhotoQuality.HIGH,
                currentPhotosDownloadQuality = PhotoQuality.HIGH,
                onEvent = {},
                navigateBack = {}
            )
        }
    }
}