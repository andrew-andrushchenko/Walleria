package com.andrii_a.walleria.data.local.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val TAG = "LocalPreferencesRepository"

private val Context.appLocalPreferences: DataStore<Preferences> by preferencesDataStore(name = "walleria_local_preferences")

class LocalPreferencesRepositoryImpl(context: Context) : LocalPreferencesRepository {

    private val appLocalPreferences: DataStore<Preferences> by lazy {
        return@lazy context.appLocalPreferences
    }

    private val preferencesFlow = appLocalPreferences.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

    override val photosListLayoutType: Flow<PhotosListLayoutType> =
        preferencesFlow.map { preferences ->
            PhotosListLayoutType.valueOf(
                preferences[WalleriaAppPreferencesKeys.PHOTOS_LIST_LAYOUT_TYPE]
                    ?: PhotosListLayoutType.DEFAULT.name
            )
        }

    override val collectionsListLayoutType: Flow<CollectionListLayoutType> =
        preferencesFlow.map { preferences ->
            CollectionListLayoutType.valueOf(
                preferences[WalleriaAppPreferencesKeys.COLLECTIONS_LIST_LAYOUT_TYPE]
                    ?: CollectionListLayoutType.DEFAULT.name
            )
        }

    override val photoPreviewsQuality: Flow<PhotoQuality> = preferencesFlow.map { preferences ->
        PhotoQuality.valueOf(
            preferences[WalleriaAppPreferencesKeys.PHOTO_PREVIEWS_QUALITY] ?: PhotoQuality.MEDIUM.name
        )
    }

    override suspend fun updatePhotosListLayoutType(layoutType: PhotosListLayoutType) {
        appLocalPreferences.edit { preferences ->
            preferences[WalleriaAppPreferencesKeys.PHOTOS_LIST_LAYOUT_TYPE] =
                layoutType.name
        }
    }

    override suspend fun updateCollectionsListLayoutType(layoutType: CollectionListLayoutType) {
        appLocalPreferences.edit { preferences ->
            preferences[WalleriaAppPreferencesKeys.COLLECTIONS_LIST_LAYOUT_TYPE] =
                layoutType.name
        }
    }

    override suspend fun updatePhotoPreviewsQuality(photoQuality: PhotoQuality) {
        appLocalPreferences.edit { preferences ->
            preferences[WalleriaAppPreferencesKeys.PHOTO_PREVIEWS_QUALITY] =
                photoQuality.name
        }
    }

    object WalleriaAppPreferencesKeys {
        val PHOTOS_LIST_LAYOUT_TYPE = stringPreferencesKey("photos_list_layout_type")
        val COLLECTIONS_LIST_LAYOUT_TYPE = stringPreferencesKey("collections_list_layout_type")
        val PHOTO_PREVIEWS_QUALITY = stringPreferencesKey("photo_previews_quality")
    }
}