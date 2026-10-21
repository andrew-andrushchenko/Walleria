package com.andrii_a.walleria.ui.photo_details

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.cash.turbine.test
import com.andrii_a.walleria.data.repository.local.FakeLocalAccountRepository
import com.andrii_a.walleria.data.repository.local.FakeLocalPreferencesRepository
import com.andrii_a.walleria.data.repository.remote.FakePhotoRepository
import com.andrii_a.walleria.data.services.FakePhotoDownloader
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.photo.PhotoUrls
import com.andrii_a.walleria.ui.common.UiErrorWithRetry
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.util.MainDispatcherExtension
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class PhotoDetailsViewModelTest {

    private lateinit var photoRepository: FakePhotoRepository
    private lateinit var localAccountRepository: FakeLocalAccountRepository
    private lateinit var localPreferencesRepository: FakeLocalPreferencesRepository
    private lateinit var photoDownloader: FakePhotoDownloader
    private lateinit var savedStateHandle: SavedStateHandle

    @BeforeEach
    fun setUp() {
        photoRepository = FakePhotoRepository()
        localAccountRepository = FakeLocalAccountRepository()
        localPreferencesRepository = FakeLocalPreferencesRepository()
        photoDownloader = FakePhotoDownloader()
        savedStateHandle = SavedStateHandle()

        mockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    private fun createViewModel(photoId: String = "photo_1"): PhotoDetailsViewModel {
        every { savedStateHandle.toRoute<Screen.PhotoDetails>() } returns Screen.PhotoDetails(
            photoId = photoId
        )
        return PhotoDetailsViewModel(
            photoRepository = photoRepository,
            localAccountRepository = localAccountRepository,
            localPreferencesRepository = localPreferencesRepository,
            photoDownloader = photoDownloader,
            savedStateHandle = savedStateHandle
        )
    }

    private fun createDummyPhoto(
        id: String = "photo_1",
        likes: Long = 10,
        likedByUser: Boolean = false,
        currentUserCollections: List<Collection>? = null
    ): Photo {
        return Photo(
            id = id,
            width = 1000,
            height = 1000,
            createdAt = "2023-01-01",
            blurHash = null,
            color = "#000000",
            views = 100,
            downloads = 50,
            likes = likes,
            likedByUser = likedByUser,
            description = "Test photo",
            exif = null,
            location = null,
            tags = null,
            relatedCollections = null,
            currentUserCollections = currentUserCollections,
            sponsorship = null,
            urls = PhotoUrls(
                raw = "https://example.com/raw",
                full = "https://example.com/full",
                regular = "https://example.com/regular",
                small = "https://example.com/small",
                thumb = "https://example.com/thumb"
            ),
            links = null,
            user = null
        )
    }

    private fun createDummyCollection(id: String = "col_1", title: String = "Test Collection") =
        Collection(
            id = id,
            title = title,
            description = null,
            curated = false,
            featured = false,
            totalPhotos = 1,
            isPrivate = false,
            tags = null,
            coverPhoto = null,
            previewPhotos = null,
            links = null,
            user = null
        )

    @Test
    fun `initialization requests photo details successfully`() = runTest {
        val dummyPhoto = createDummyPhoto(
            id = "photo_123",
            likes = 42,
            likedByUser = true,
            currentUserCollections = listOf(createDummyCollection())
        )
        photoRepository.photoToReturn = dummyPhoto

        val viewModel = createViewModel(photoId = "photo_123")

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(dummyPhoto, state.photo)
            assertEquals(42L, state.currentPhotoLikes)
            assertTrue(state.isLikedByLoggedInUser)
            assertTrue(state.isCollected)
            assertNull(state.error)
            assertEquals("photo_123", photoRepository.lastRequestedPhotoId)
        }
    }

    @Test
    fun `RequestPhoto displays error when repository returns error and retry refetches photo`() =
        runTest {
            photoRepository.shouldReturnError = true
            photoRepository.errorMessage = "Failed to load photo"

            val photoId = "photo_1"
            val viewModel = createViewModel(photoId)

            viewModel.state.test {
                val errorState = awaitItem()
                assertFalse(errorState.isLoading)
                assertNotNull(errorState.error)

                photoRepository.shouldReturnError = false
                photoRepository.photoToReturn = createDummyPhoto(photoId)

                val errorWithRetry = errorState.error as UiErrorWithRetry
                errorWithRetry.onRetry()

                val successState = awaitItem()
                assertFalse(successState.isLoading)
                assertNull(successState.error)
                assertNotNull(successState.photo)
            }
        }

    @Test
    fun `LikePhoto updates liked state and increments like count on success`() = runTest {
        val photoId = "photo_1"
        val initialPhoto = createDummyPhoto(id = photoId, likes = 10, likedByUser = false)
        photoRepository.photoToReturn = initialPhoto

        val viewModel = createViewModel(photoId)

        viewModel.state.test {
            awaitItem()

            viewModel.onEvent(PhotoDetailsEvent.LikePhoto(photoId))

            val likedState = awaitItem()
            assertTrue(likedState.isLikedByLoggedInUser)
            assertEquals(11L, likedState.currentPhotoLikes)
            assertTrue(photoRepository.likedPhotoIds.contains(photoId))
        }
    }

    @Test
    fun `LikePhoto sets error and resets liked state on repository failure`() = runTest {
        val photoId = "photo_1"
        val initialPhoto = createDummyPhoto(id = photoId, likes = 10, likedByUser = false)
        photoRepository.photoToReturn = initialPhoto

        val viewModel = createViewModel(photoId = "photo_1")

        viewModel.state.test {
            awaitItem()

            photoRepository.shouldReturnError = true
            photoRepository.errorMessage = "Like failed"
            viewModel.onEvent(PhotoDetailsEvent.LikePhoto(photoId))

            val errorState = awaitItem()
            assertFalse(errorState.isLikedByLoggedInUser)
            assertNotNull(errorState.error)
        }
    }

    @Test
    fun `DislikePhoto updates liked state and resets like count on success`() = runTest {
        val photoId = "photo_1"
        val initialPhoto = createDummyPhoto(id = photoId, likes = 10, likedByUser = true)
        photoRepository.photoToReturn = initialPhoto

        val viewModel = createViewModel(photoId)

        viewModel.state.test {
            awaitItem()

            viewModel.onEvent(PhotoDetailsEvent.DislikePhoto(photoId))

            val dislikedState = awaitItem()
            assertFalse(dislikedState.isLikedByLoggedInUser)
            assertEquals(10L, dislikedState.currentPhotoLikes)
            assertTrue(photoRepository.dislikedPhotoIds.contains(photoId))
        }
    }

    @Test
    fun `MakeCollected and MakeDropped toggle isCollected state`() = runTest {
        val photoId = "photo_1"
        photoRepository.photoToReturn = createDummyPhoto(id = photoId)
        val viewModel = createViewModel(photoId)

        viewModel.state.test {
            val initial = awaitItem()
            assertFalse(initial.isCollected)

            viewModel.onEvent(PhotoDetailsEvent.MakeCollected)
            assertTrue(awaitItem().isCollected)

            viewModel.onEvent(PhotoDetailsEvent.MakeDropped)
            assertFalse(awaitItem().isCollected)
        }
    }

    @Test
    fun `ShowInfoDialog and DismissInfoDialog toggle info dialog state`() = runTest {
        val photoId = "photo_1"
        photoRepository.photoToReturn = createDummyPhoto(photoId)
        val viewModel = createViewModel(photoId)

        viewModel.state.test {
            val initial = awaitItem()
            assertFalse(initial.isInfoDialogOpened)

            viewModel.onEvent(PhotoDetailsEvent.ShowInfoDialog)
            assertTrue(awaitItem().isInfoDialogOpened)

            viewModel.onEvent(PhotoDetailsEvent.DismissInfoDialog)
            assertFalse(awaitItem().isInfoDialogOpened)
        }
    }

    @Test
    fun `ToggleControlsVisibility toggles showControls state`() = runTest {
        val photoId = "photo_1"
        photoRepository.photoToReturn = createDummyPhoto(id = photoId)
        val viewModel = createViewModel(photoId)

        viewModel.state.test {
            val initial = awaitItem()
            assertTrue(initial.showControls)

            viewModel.onEvent(PhotoDetailsEvent.ToggleControlsVisibility)
            assertFalse(awaitItem().showControls)

            viewModel.onEvent(PhotoDetailsEvent.ToggleControlsVisibility)
            assertTrue(awaitItem().showControls)
        }
    }

    @Test
    fun `DownloadPhoto triggers photo downloader`() = runTest {
        val photoId = "photo_1"
        photoRepository.photoToReturn = createDummyPhoto(id = photoId)
        val viewModel = createViewModel(photoId)

        viewModel.state.test {
            val state = awaitItem()
            val photo = state.photo!!

            viewModel.onEvent(PhotoDetailsEvent.DownloadPhoto(photo, PhotoQuality.HIGH))

            assertEquals(1, photoDownloader.downloadCallCount)
            assertEquals(photo, photoDownloader.downloadedPhoto)
            assertEquals(PhotoQuality.HIGH, photoDownloader.downloadedQuality)
        }
    }

    @Test
    fun `UI item selection events emit expected navigation events`() = runTest {
        val photoId = "photo_1"
        val collectionId = "col_1"
        val userNickname = "john_doe"
        val tag = "nature"
        val photoLinkOnWeb = "https://example.com/photo"

        photoRepository.photoToReturn = createDummyPhoto(id = photoId)
        val viewModel = createViewModel(photoId)

        viewModel.navigationEventsChannelFlow.test {
            viewModel.onEvent(PhotoDetailsEvent.GoBack)
            assertEquals(PhotoDetailsNavigationEvent.NavigateBack, awaitItem())

            viewModel.onEvent(PhotoDetailsEvent.SearchByTag(tag))
            assertEquals(PhotoDetailsNavigationEvent.NavigateToSearch(tag), awaitItem())

            viewModel.onEvent(PhotoDetailsEvent.SelectCollection(collectionId))
            assertEquals(
                PhotoDetailsNavigationEvent.NavigateToCollectionDetails(collectionId),
                awaitItem()
            )

            viewModel.onEvent(PhotoDetailsEvent.SelectUser(userNickname))
            assertEquals(
                PhotoDetailsNavigationEvent.NavigateToUserDetails(userNickname),
                awaitItem()
            )

            viewModel.onEvent(PhotoDetailsEvent.SelectCollectOption(photoId))
            assertEquals(PhotoDetailsNavigationEvent.NavigateToCollectPhoto(photoId), awaitItem())

            viewModel.onEvent(PhotoDetailsEvent.OpenInBrowser(photoLinkOnWeb))
            assertEquals(
                PhotoDetailsNavigationEvent.NavigateToChromeCustomTab(photoLinkOnWeb),
                awaitItem()
            )

            viewModel.onEvent(PhotoDetailsEvent.RedirectToLogin)
            assertEquals(PhotoDetailsNavigationEvent.NavigateToLogin, awaitItem())

            viewModel.onEvent(PhotoDetailsEvent.SharePhoto(photoLinkOnWeb, "desc"))
            assertEquals(
                PhotoDetailsNavigationEvent.NavigateToShareDialog(
                    link = photoLinkOnWeb,
                    description = "desc"
                ),
                awaitItem()
            )
        }
    }
}