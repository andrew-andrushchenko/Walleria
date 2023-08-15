package com.andrii_a.walleria.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.titleRes

@Composable
fun SettingsScreen(
    currentPhotosListLayoutType: PhotosListLayoutType,
    currentCollectionListLayoutType: CollectionListLayoutType,
    currentPhotosLoadQuality: PhotoQuality,
    currentPhotosDownloadQuality: PhotoQuality,
    onEvent: (SettingsEvent) -> Unit,
    navigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = dimensionResource(id = R.dimen.top_bar_height))
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            SettingsGroup(name = stringResource(id = R.string.layout_settings)) {
                SettingsItem(
                    title = stringResource(id = R.string.photos_layout),
                    selectedValue = stringResource(id = currentPhotosListLayoutType.titleRes),
                    selectionOptions = PhotosListLayoutType.values().map { stringResource(id = it.titleRes) },
                    selectedItemPositionOrdinal = currentPhotosListLayoutType.ordinal,
                    onChangeParameter = { selectedLayoutTypeOrdinal ->
                        onEvent(
                            SettingsEvent.UpdatePhotosListLayoutType(
                                PhotosListLayoutType.values()[selectedLayoutTypeOrdinal]
                            )
                        )
                    }
                )

                SettingsItem(
                    title = stringResource(id = R.string.collections_layout),
                    selectedValue = stringResource(id = currentCollectionListLayoutType.titleRes),
                    selectionOptions = CollectionListLayoutType.values().map { stringResource(id = it.titleRes) },
                    selectedItemPositionOrdinal = currentCollectionListLayoutType.ordinal,
                    onChangeParameter = { selectedLayoutTypeOrdinal ->
                        onEvent(
                            SettingsEvent.UpdateCollectionsListLayoutType(
                                CollectionListLayoutType.values()[selectedLayoutTypeOrdinal]
                            )
                        )
                    },
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SettingsGroup(name = stringResource(id = R.string.load_settings)) {
                SettingsItem(
                    title = stringResource(id = R.string.photo_load_quality),
                    selectedValue = stringResource(id = currentPhotosLoadQuality.titleRes),
                    selectionOptions = PhotoQuality.values().map { stringResource(id = it.titleRes) },
                    selectedItemPositionOrdinal = currentPhotosLoadQuality.ordinal,
                    onChangeParameter = { selectedPhotoQualityOrdinal ->
                        onEvent(
                            SettingsEvent.UpdatePhotosLoadQuality(
                                PhotoQuality.values()[selectedPhotoQualityOrdinal]
                            )
                        )
                    }
                )

                SettingsItem(
                    title = stringResource(id = R.string.photo_download_quality),
                    selectedValue = stringResource(id = currentPhotosDownloadQuality.titleRes),
                    selectionOptions = PhotoQuality.values().map { stringResource(id = it.titleRes) },
                    selectedItemPositionOrdinal = currentPhotosDownloadQuality.ordinal,
                    onChangeParameter = { selectedPhotoQualityOrdinal ->
                        onEvent(
                            SettingsEvent.UpdatePhotosDownloadQuality(
                                PhotoQuality.values()[selectedPhotoQualityOrdinal]
                            )
                        )
                    }
                )
            }
        }

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
            text = stringResource(id = R.string.settings),
            style = MaterialTheme.typography.h6,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun SettingsScreen() {
    WalleriaTheme {
        Surface {
            SettingsScreen(
                currentPhotosListLayoutType = PhotosListLayoutType.DEFAULT,
                currentCollectionListLayoutType = CollectionListLayoutType.DEFAULT,
                currentPhotosLoadQuality = PhotoQuality.HIGH,
                currentPhotosDownloadQuality = PhotoQuality.HIGH,
                onEvent = {},
                navigateBack = {}
            )
        }
    }
}