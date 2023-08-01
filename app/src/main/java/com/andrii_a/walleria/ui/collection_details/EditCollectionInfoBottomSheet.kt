package com.andrii_a.walleria.ui.collection_details

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.common.CheckBoxRow
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.WButton
import com.andrii_a.walleria.ui.common.WOutlinedTextField
import com.andrii_a.walleria.ui.theme.WalleriaTheme

@Composable
fun EditCollectionInfoBottomSheet(
    collection: Collection,
    onEvent: (CollectionDetailsEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var title by rememberSaveable {
        mutableStateOf(collection.title)
    }

    var description by rememberSaveable {
        mutableStateOf(collection.description.orEmpty())
    }

    var isPrivate by rememberSaveable {
        mutableStateOf(collection.isPrivate)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(
                modifier = Modifier
                    .padding(vertical = 22.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .background(
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(50)
                    )
                    .align(Alignment.CenterHorizontally)
            )

            WOutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(text = stringResource(id = R.string.collection_name_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            WOutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(text = stringResource(id = R.string.collection_description_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            CheckBoxRow(
                checked = isPrivate,
                onCheckedChange = { isPrivate = it },
                labelText = stringResource(id = R.string.collection_private),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            var showConfirmationRow by rememberSaveable {
                mutableStateOf(false)
            }

            AnimatedContent(
                targetState = showConfirmationRow,
                label = "",
                modifier = Modifier.fillMaxWidth()
            ) {
                if (it) {
                    DeleteConfirmationRow(
                        onConfirm = {
                            onEvent(
                                CollectionDetailsEvent.DeleteCollection(
                                    CollectionId(collection.id)
                                )
                            )
                        },
                        onDismiss = { showConfirmationRow = false },
                        modifier = Modifier
                    )
                } else {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ActionRow(
                            onUpdate = {
                                onEvent(
                                    CollectionDetailsEvent.UpdateCollection(
                                        collectionId = CollectionId(collection.id),
                                        title = title,
                                        description = description,
                                        isPrivate = isPrivate
                                    )
                                )
                                onDismiss()
                            },
                            onDelete = {
                                showConfirmationRow = true
                            },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ActionRow(
    onUpdate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        TextButton(onClick = onDelete) {
            Text(
                text = stringResource(id = R.string.delete_collection_action),
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        WButton(onClick = onUpdate) {
            Text(
                text = stringResource(id = R.string.update_collection_action),
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@Composable
private fun DeleteConfirmationRow(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (confirmationText, confirmButton, dismissButton) = createRefs()

        Text(
            text = stringResource(id = R.string.delete_collection_confirmation),
            style = MaterialTheme.typography.subtitle1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(confirmationText) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(confirmButton.start)
                width = Dimension.fillToConstraints
            }
        )

        TextButton(
            onClick = onConfirm,
            modifier = Modifier.constrainAs(confirmButton) {
                top.linkTo(dismissButton.top)
                bottom.linkTo(dismissButton.bottom)
                end.linkTo(dismissButton.start)
            }
        ) {
            Text(
                text = stringResource(id = R.string.action_yes),
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface
            )
        }

        WButton(
            onClick = onDismiss,
            modifier = Modifier.constrainAs(dismissButton) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        ) {
            Text(
                stringResource(id = R.string.action_no),
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun EditCollectionInfoBottomSheetPreview() {
    WalleriaTheme {
        EditCollectionInfoBottomSheet(
            collection = Collection(
                id = "",
                title = "Title",
                description = null,
                curated = false,
                featured = false,
                totalPhotos = 100,
                isPrivate = false,
                tags = null,
                coverPhoto = null,
                previewPhotos = null,
                links = null,
                user = null
            ),
            onEvent = {},
            onDismiss = {}
        )
    }
}