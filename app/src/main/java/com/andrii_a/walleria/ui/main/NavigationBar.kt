package com.andrii_a.walleria.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.andrii_a.walleria.ui.navigation.NavigationScreen

@Composable
fun WNavigationBar(
    navScreenItems: List<NavigationScreen>,
    onItemSelected: (NavigationScreen) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = 16.dp,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        backgroundColor = MaterialTheme.colors.primary,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 18.dp, end = 18.dp, top = 24.dp, bottom = 18.dp)
                .navigationBarsPadding()
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            navScreenItems.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                WNavigationBarItem(
                    item = item,
                    isSelected = selected,
                    onClick = { onItemSelected(item) },
                    backgroundColor = if (selected) MaterialTheme.colors.onPrimary else Color.Transparent,
                    contentColor = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

@Composable
fun WNavigationBarItem(
    item: NavigationScreen,
    isSelected: Boolean,
    onClick: () -> Unit,
    backgroundColor: Color,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(12.dp)
        ) {

            Icon(
                painter = painterResource(id = if (isSelected) item.iconSelected else item.iconUnselected),
                contentDescription = null,
                tint = contentColor
            )

            AnimatedVisibility(visible = isSelected) {
                Text(
                    text = stringResource(id = item.titleRes),
                    color = contentColor
                )
            }
        }
    }
}