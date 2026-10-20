package com.andrii_a.walleria.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.andrii_a.walleria.data.repository.local.FakeLocalPreferencesRepository
import com.andrii_a.walleria.data.repository.local.FakeSearchHistoryRepository
import com.andrii_a.walleria.data.repository.remote.FakeSearchRepository
import com.andrii_a.walleria.domain.SearchResultsDisplayOrder
import com.andrii_a.walleria.domain.models.search.SearchHistoryItem
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.util.MainDispatcherExtension
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class SearchViewModelTest {

    private lateinit var searchRepository: FakeSearchRepository
    private lateinit var searchHistoryRepository: FakeSearchHistoryRepository
    private lateinit var localPreferencesRepository: FakeLocalPreferencesRepository
    private lateinit var savedStateHandle: SavedStateHandle

    @BeforeEach
    fun setUp() {
        searchRepository = FakeSearchRepository()
        searchHistoryRepository = FakeSearchHistoryRepository()
        localPreferencesRepository = FakeLocalPreferencesRepository()
        savedStateHandle = SavedStateHandle()

        mockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    private fun createViewModel(initialQuery: String = ""): SearchViewModel {
        every { savedStateHandle.toRoute<Screen.Search>() } returns Screen.Search(searchQuery = initialQuery)
        return SearchViewModel(
            searchRepository = searchRepository,
            searchHistoryRepository = searchHistoryRepository,
            localPreferencesRepository = localPreferencesRepository,
            savedStateHandle = savedStateHandle
        )
    }

    private suspend fun prepopulateHistory(titles: List<String>) {
        titles.forEachIndexed { index, title ->
            searchHistoryRepository.insertItem(
                SearchHistoryItem(title = title, timeMillis = 1000L * index)
            )
        }
    }

    @Test
    fun `initial state contains default values when query is blank`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.query.isBlank())
            assertFalse(state.isFilterDialogOpened)
            assertTrue(state.searchHistory.isEmpty())
        }
    }

    @Test
    fun `non-blank initial query triggers search automatically`() = runTest {
        val initialQuery = "nature"
        val viewModel = createViewModel(initialQuery = initialQuery)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(initialQuery, state.query)
            assertEquals(initialQuery, searchRepository.lastSearchedQuery)
        }
    }

    @Test
    fun `ChangeQuery updates search query in state`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()

            val expectedQuery = "mountains"
            viewModel.onEvent(SearchEvent.ChangeQuery(expectedQuery))

            val updatedState = awaitItem()
            assertEquals(expectedQuery, updatedState.query)
        }
    }

    @Test
    fun `PerformSearch triggers search in repository and adds query to history`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()

            val query = "architecture"
            viewModel.onEvent(SearchEvent.ChangeQuery(query))
            awaitItem()

            viewModel.onEvent(SearchEvent.PerformSearch)

            assertEquals(query, searchRepository.lastSearchedQuery)

            val stateWithHistory = expectMostRecentItem()
            assertTrue(stateWithHistory.searchHistory.any { it.title == query })
        }
    }

    @Test
    fun `ChangePhotoFilters updates filters in state and re-fetches photos`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()

            val newFilters = PhotoFilters(order = SearchResultsDisplayOrder.LATEST)
            viewModel.onEvent(SearchEvent.ChangePhotoFilters(newFilters))

            val updatedState = awaitItem()
            assertEquals(newFilters, updatedState.photoFilters)
            assertEquals(SearchResultsDisplayOrder.LATEST, searchRepository.lastOrder)
        }
    }

    @Test
    fun `OpenFilterDialog and DismissFilterDialog toggle filter dialog state`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            val initial = awaitItem()
            assertFalse(initial.isFilterDialogOpened)

            viewModel.onEvent(SearchEvent.OpenFilterDialog)
            assertTrue(awaitItem().isFilterDialogOpened)

            viewModel.onEvent(SearchEvent.DismissFilterDialog)
            assertFalse(awaitItem().isFilterDialogOpened)
        }
    }

    @Test
    fun `DeleteSearchHistoryItem removes specific item from history`() = runTest {
        prepopulateHistory(listOf("nature", "city", "ocean"))
        val viewModel = createViewModel()

        viewModel.state.test {
            val initial = awaitItem()
            assertEquals(3, initial.searchHistory.size)

            val itemToDelete = initial.searchHistory.first { it.title == "city" }
            viewModel.onEvent(SearchEvent.DeleteSearchHistoryItem(itemToDelete))

            val updatedHistory = expectMostRecentItem().searchHistory
            assertEquals(2, updatedHistory.size)
            assertFalse(updatedHistory.any { it.title == "city" })
        }
    }

    @Test
    fun `DeleteSearchHistory removes all items from history`() = runTest {
        prepopulateHistory(listOf("nature", "city"))
        val viewModel = createViewModel()

        viewModel.state.test {
            val initial = awaitItem()
            assertEquals(2, initial.searchHistory.size)

            viewModel.onEvent(SearchEvent.DeleteSearchHistory)

            val updatedHistory = expectMostRecentItem().searchHistory
            assertTrue(updatedHistory.isEmpty())
        }
    }

    @Test
    fun `UI item selection events emit expected navigation events`() = runTest {
        val viewModel = createViewModel()

        viewModel.navigationEventsChannelFlow.test {
            viewModel.onEvent(SearchEvent.SelectPhoto("photo_1"))
            assertEquals(SearchNavigationEvent.NavigateToPhotoDetails("photo_1"), awaitItem())

            viewModel.onEvent(SearchEvent.SelectCollection("col_1"))
            assertEquals(SearchNavigationEvent.NavigateToCollectionDetails("col_1"), awaitItem())

            viewModel.onEvent(SearchEvent.SelectUser("user_1"))
            assertEquals(SearchNavigationEvent.NavigateToUserDetails("user_1"), awaitItem())

            viewModel.onEvent(SearchEvent.GoBack)
            assertEquals(SearchNavigationEvent.NavigateBack, awaitItem())
        }
    }
}
