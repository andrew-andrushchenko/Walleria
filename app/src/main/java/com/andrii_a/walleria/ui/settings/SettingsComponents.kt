package com.andrii_a.walleria.ui.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.theme.WalleriaTheme

@Composable
fun SettingsGroup(
    name: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(text = name, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            color = MaterialTheme.colors.surface,
            elevation = 1.dp,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    @DrawableRes leadingIconRes: Int? = null,
    title: String,
    selectedValue: String,
    selectionOptions: List<String>,
    selectedItemPositionOrdinal: Int,
    onChangeParameter: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        SingleChoiceSelectionDialog(
            title = title,
            items = selectionOptions,
            selectedItemPositionOrdinal = selectedItemPositionOrdinal,
            onSelect = onChangeParameter,
            onDismiss = { showDialog = false }
        )
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = { showDialog = true })
    ) {
        val (leadingIcon, titleText, selectedValueText, trailingIcon) = createRefs()

        leadingIconRes?.let {
            Icon(
                painter = painterResource(id = leadingIconRes),
                contentDescription = title,
                modifier = Modifier.constrainAs(leadingIcon) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.subtitle1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(titleText) {
                if (leadingIconRes != null) {
                    start.linkTo(leadingIcon.end, 8.dp)
                    top.linkTo(leadingIcon.top)
                } else {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                end.linkTo(trailingIcon.start, 8.dp)
                bottom.linkTo(selectedValueText.top, 4.dp)

                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = selectedValue,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(selectedValueText) {
                if (leadingIconRes != null) {
                    start.linkTo(leadingIcon.end, 8.dp)
                    bottom.linkTo(leadingIcon.bottom)
                } else {
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }

                end.linkTo(trailingIcon.start, 8.dp)
                top.linkTo(titleText.bottom)

                width = Dimension.fillToConstraints
            }
        )

        Icon(
            painterResource(id = R.drawable.ic_arrow_up_alt),
            contentDescription = "",
            modifier = Modifier
                .constrainAs(trailingIcon) {
                    top.linkTo(titleText.top)
                    bottom.linkTo(selectedValueText.bottom)
                    end.linkTo(parent.end)
                }
                .graphicsLayer {
                    rotationZ = 90f
                }
        )
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
fun SettingsItemPreview() {
    WalleriaTheme {
        SettingsItem(
            title = "Title",
            selectedValue = "Selected value",
            selectionOptions = listOf("Option A", "Option B", "Option C"),
            selectedItemPositionOrdinal = 0,
            onChangeParameter = {},
        )
    }
}

@Preview
@Composable
fun SettingsGroupPreview() {
    WalleriaTheme {
        Surface {
            SettingsGroup(
                name = "Parameters",
                modifier = Modifier.padding(16.dp)
            ) {
                SettingsItem(
                    title = "Title1",
                    selectedValue = "Selected value",
                    selectionOptions = listOf("Option A", "Option B", "Option C"),
                    selectedItemPositionOrdinal = 0,
                    onChangeParameter = {},
                )

                SettingsItem(
                    title = "Title2",
                    selectedValue = "Selected value",
                    selectionOptions = listOf("Option A", "Option B", "Option C"),
                    selectedItemPositionOrdinal = 0,
                    onChangeParameter = {},
                )

                SettingsItem(
                    title = "Title3",
                    selectedValue = "Selected value",
                    selectionOptions = listOf("Option A", "Option B", "Option C"),
                    selectedItemPositionOrdinal = 0,
                    onChangeParameter = {},
                )
            }
        }
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