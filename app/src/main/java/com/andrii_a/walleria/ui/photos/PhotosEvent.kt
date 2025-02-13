package com.andrii_a.walleria.ui.photos

import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname

sealed interface PhotosEvent {
    data class ChangeListOrder(val orderOptionOrdinalNum: Int) : PhotosEvent
    data class SelectPhoto(val photoId: PhotoId) : PhotosEvent
    data class SelectUser(val userNickname: UserNickname) : PhotosEvent
    data object SelectSearch : PhotosEvent
    data class ToggleListOrderMenu(val isExpanded: Boolean) : PhotosEvent
}