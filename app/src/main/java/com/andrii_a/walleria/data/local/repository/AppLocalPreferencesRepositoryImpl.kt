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
import com.andrii_a.walleria.domain.repository.AppLocalPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val TAG = "AppLocalPreferencesRepo"

private val Context.appLocalPreferences: DataStore<Preferences> by preferencesDataStore(name = "walleria_app_local_preferences")

class AppLocalPreferencesRepositoryImpl(context: Context) : AppLocalPreferencesRepository {

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

    override val imagePreviewsQuality: Flow<PhotoQuality> = preferencesFlow.map { preferences ->
        PhotoQuality.valueOf(
            preferences[WalleriaAppPreferencesKeys.IMAGE_PREVIEWS_QUALITY] ?: PhotoQuality.MEDIUM.name
        )
    }

    override suspend fun savePhotosListLayoutType(photosListLayoutType: PhotosListLayoutType) {
        appLocalPreferences.edit { preferences ->
            preferences[WalleriaAppPreferencesKeys.PHOTOS_LIST_LAYOUT_TYPE] =
                photosListLayoutType.name
        }
    }

    override suspend fun saveCollectionsListLayoutType(collectionListLayoutType: CollectionListLayoutType) {
        appLocalPreferences.edit { preferences ->
            preferences[WalleriaAppPreferencesKeys.COLLECTIONS_LIST_LAYOUT_TYPE] =
                collectionListLayoutType.name
        }
    }

    override suspend fun saveImagePreviewsQuality(imagePreviewsQuality: PhotoQuality) {
        appLocalPreferences.edit { preferences ->
            preferences[WalleriaAppPreferencesKeys.IMAGE_PREVIEWS_QUALITY] =
                imagePreviewsQuality.name
        }
    }

    object WalleriaAppPreferencesKeys {

        val PHOTOS_LIST_LAYOUT_TYPE = stringPreferencesKey("photos_list_layout_type")

        val COLLECTIONS_LIST_LAYOUT_TYPE = stringPreferencesKey("collections_list_layout_type")

        val IMAGE_PREVIEWS_QUALITY = stringPreferencesKey("image_previews_quality")
    }
}