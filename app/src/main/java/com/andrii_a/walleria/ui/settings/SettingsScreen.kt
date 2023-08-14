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
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    currentPhotoPreviewsQuality: PhotoQuality,
    onEvent: (SettingsEvent) -> Unit,
    navigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        var showPhotosLayoutDialog by rememberSaveable { mutableStateOf(false) }
        var showCollectionsLayoutDialog by rememberSaveable { mutableStateOf(false) }
        var showPhotoPreviewsQualityDialog by rememberSaveable { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = dimensionResource(id = R.dimen.top_bar_height))
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(id = R.string.list_layout_settings),
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(16.dp)
            )

            SettingsItemRow(
                title = stringResource(id = R.string.photos_layout),
                selectedValue = stringResource(id = currentPhotosListLayoutType.titleRes),
                onSelect = { showPhotosLayoutDialog = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsItemRow(
                title = stringResource(id = R.string.collections_layout),
                selectedValue = stringResource(id = currentCollectionListLayoutType.titleRes),
                onSelect = { showCollectionsLayoutDialog = true }
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            SettingsItemRow(
                title = stringResource(id = R.string.photo_previews_quality),
                selectedValue = stringResource(id = currentPhotoPreviewsQuality.titleRes),
                onSelect = { showPhotoPreviewsQualityDialog = true }
            )
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

        if (showPhotosLayoutDialog) {
            SingleChoiceSelectionDialog(
                title = stringResource(id = R.string.photos_layout),
                items = PhotosListLayoutType.values().map { stringResource(id = it.titleRes) },
                selectedItemPositionOrdinal = currentPhotosListLayoutType.ordinal,
                onSelect = { selectedLayoutTypeOrdinal ->
                    onEvent(
                        SettingsEvent.UpdatePhotosListLayoutType(
                            PhotosListLayoutType.values()[selectedLayoutTypeOrdinal]
                        )
                    )
                },
                onDismiss = { showPhotosLayoutDialog = false }
            )
        }

        if (showCollectionsLayoutDialog) {
            SingleChoiceSelectionDialog(
                title = stringResource(id = R.string.collections_layout),
                items = CollectionListLayoutType.values().map { stringResource(id = it.titleRes) },
                selectedItemPositionOrdinal = currentCollectionListLayoutType.ordinal,
                onSelect = { selectedLayoutTypeOrdinal ->
                    onEvent(
                        SettingsEvent.UpdateCollectionsListLayoutType(
                            CollectionListLayoutType.values()[selectedLayoutTypeOrdinal]
                        )
                    )
                },
                onDismiss = { showCollectionsLayoutDialog = false }
            )
        }

        if (showPhotoPreviewsQualityDialog) {
            SingleChoiceSelectionDialog(
                title = stringResource(id = R.string.photo_previews_quality),
                items = PhotoQuality.values().map { stringResource(id = it.titleRes) },
                selectedItemPositionOrdinal = currentPhotoPreviewsQuality.ordinal,
                onSelect = { selectedPhotoQualityOrdinal ->
                    onEvent(
                        SettingsEvent.UpdatePhotoPreviewsQuality(
                            PhotoQuality.values()[selectedPhotoQualityOrdinal]
                        )
                    )
                },
                onDismiss = { showPhotoPreviewsQualityDialog = false }
            )
        }
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
        SettingsScreen(
            currentPhotosListLayoutType = PhotosListLayoutType.DEFAULT,
            currentCollectionListLayoutType = CollectionListLayoutType.DEFAULT,
            currentPhotoPreviewsQuality = PhotoQuality.HIGH,
            onEvent = {},
            navigateBack = {}
        )
    }
}