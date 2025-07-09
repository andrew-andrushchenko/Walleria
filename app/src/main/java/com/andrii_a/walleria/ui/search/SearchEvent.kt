package com.andrii_a.walleria.ui.search

import com.andrii_a.walleria.domain.models.search.SearchHistoryItem
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.UserNickname

sealed interface SearchEvent {
    data class ChangeQuery(val query: SearchQuery) : SearchEvent

    data object PerformSearch : SearchEvent

    data class ChangePhotoFilters(val photoFilters: PhotoFilters) : SearchEvent

    data class DeleteSearchHistoryItem(val item: SearchHistoryItem) : SearchEvent

    data object DeleteSearchHistory : SearchEvent

    data class SelectPhoto(val photoId: PhotoId) : SearchEvent

    data class SelectCollection(val collectionId: CollectionId) : SearchEvent

    data class SelectUser(val userNickname: UserNickname) : SearchEvent

    data object GoBack : SearchEvent

    data object OpenFilterDialog : SearchEvent

    data object DismissFilterDialog : SearchEvent
}