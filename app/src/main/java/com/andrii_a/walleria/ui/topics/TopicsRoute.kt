package com.andrii_a.walleria.ui.topics

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.topicsNavigationBarRoute(navController: NavController) {
    composable<Screen.Topics> {
        val viewModel: TopicsViewModel = koinViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()
        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is TopicsNavigationEvent.NavigateToTopicDetails -> {
                    navController.navigate(Screen.TopicDetails(event.topicId))
                }

                is TopicsNavigationEvent.NavigateToSearchScreen -> {
                    navController.navigate(Screen.Search())
                }
            }
        }

        TopicsScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}