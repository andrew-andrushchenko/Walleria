package com.andrii_a.walleria.ui.photos

import app.cash.turbine.test
import com.andrii_a.walleria.data.repository.local.FakeLocalPreferencesRepository
import com.andrii_a.walleria.data.repository.remote.FakePhotoRepository
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.util.MainDispatcherExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class PhotosViewModelTest {

    private lateinit var photoRepository: FakePhotoRepository
    private lateinit var localPreferencesRepository: FakeLocalPreferencesRepository

    @BeforeEach
    fun setup() {
        photoRepository = FakePhotoRepository()
        localPreferencesRepository = FakeLocalPreferencesRepository()
    }

    private fun createViewModel() = PhotosViewModel(
        photoRepository = photoRepository,
        localPreferencesRepository = localPreferencesRepository
    )

    @Test
    fun `initial state sets default values and fetches photos with LATEST display order`() =
        runTest {
            val viewModel = createViewModel()

            viewModel.state.test {
                val state = awaitItem()
                assertEquals(PhotoListDisplayOrder.LATEST, state.photosListDisplayOrder)
                assertEquals(PhotoQuality.MEDIUM, state.photosLoadQuality)
                assertFalse(state.isOrderMenuExpanded)
                assertEquals(PhotoListDisplayOrder.LATEST, photoRepository.lastRequestedOrder)
            }
        }

    @Test
    fun `ChangeListOrder updates photo list display order in state and repository`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()

            viewModel.onEvent(PhotosEvent.ChangeListOrder(PhotoListDisplayOrder.POPULAR.ordinal))

            val updatedState = awaitItem()
            assertEquals(PhotoListDisplayOrder.POPULAR, updatedState.photosListDisplayOrder)
            assertEquals(PhotoListDisplayOrder.POPULAR, photoRepository.lastRequestedOrder)
        }
    }

    @Test
    fun `ToggleListOrderMenu toggles order menu expansion state`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            val initial = awaitItem()
            assertFalse(initial.isOrderMenuExpanded)

            viewModel.onEvent(PhotosEvent.ToggleListOrderMenu(isExpanded = true))
            assertTrue(awaitItem().isOrderMenuExpanded)

            viewModel.onEvent(PhotosEvent.ToggleListOrderMenu(isExpanded = false))
            assertFalse(awaitItem().isOrderMenuExpanded)
        }
    }

    @Test
    fun `updates in local preferences update photosLoadQuality in state`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            val initial = awaitItem()
            assertEquals(PhotoQuality.MEDIUM, initial.photosLoadQuality)

            localPreferencesRepository.updatePhotosLoadQuality(PhotoQuality.HIGH)

            val updatedState = awaitItem()
            assertEquals(PhotoQuality.HIGH, updatedState.photosLoadQuality)
        }
    }

    @Test
    fun `SelectPhoto emits NavigateToPhotoDetailsScreen navigation event`() = runTest {
        val viewModel = createViewModel()
        val photoId = "photo_123"

        viewModel.navigationEventsChannelFlow.test {
            viewModel.onEvent(PhotosEvent.SelectPhoto(photoId))
            assertEquals(PhotosNavigationEvent.NavigateToPhotoDetailsScreen(photoId), awaitItem())
        }
    }

    @Test
    fun `SelectUser emits NavigateToUserDetails navigation event`() = runTest {
        val viewModel = createViewModel()
        val userNickname = "john_doe"

        viewModel.navigationEventsChannelFlow.test {
            viewModel.onEvent(PhotosEvent.SelectUser(userNickname))
            assertEquals(PhotosNavigationEvent.NavigateToUserDetails(userNickname), awaitItem())
        }
    }

    @Test
    fun `SelectSearch emits NavigateToSearchScreen navigation event`() = runTest {
        val viewModel = createViewModel()

        viewModel.navigationEventsChannelFlow.test {
            viewModel.onEvent(PhotosEvent.SelectSearch)
            assertEquals(PhotosNavigationEvent.NavigateToSearchScreen, awaitItem())
        }
    }
}