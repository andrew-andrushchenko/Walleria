package com.andrii_a.walleria.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andrii_a.walleria.ui.theme.WalleriaTheme

@Composable
fun SettingsItemRow(
    painter: Painter? = null,
    title: String,
    selectedValue: String,
    onSelect: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(16.dp)
    ) {
        if (painter != null) {
            Icon(painter = painter, contentDescription = title)
        } else {
            Spacer(modifier = Modifier.width(16.dp))
        }

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = selectedValue,
                style = MaterialTheme.typography.subtitle2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SingleChoiceSelectionDialog(
    title: String,
    items: List<String>,
    selectedItemPositionOrdinal: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                )

                items.forEachIndexed { index, item ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(index)
                                onDismiss()
                            }
                    ) {
                        RadioButton(
                            selected = index == selectedItemPositionOrdinal,
                            onClick = {
                                onSelect(index)
                                onDismiss()
                            }
                        )

                        Text(text = item)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingsItemRowPreview() {
    WalleriaTheme {
        SettingsItemRow(
            title = "Title",
            selectedValue = "Selected value",
            onSelect = {}
        )
    }
}

@Preview
@Composable
fun SingleChoiceSelectionDialog() {
    WalleriaTheme {
        SingleChoiceSelectionDialog(
            title = "Multiselect",
            items = listOf("Option A", "Option B", "Option C"),
            selectedItemPositionOrdinal = 0,
            onSelect = {},
            onDismiss = {}
        )
    }
}