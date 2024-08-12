package com.andrii_a.walleria.ui.collect_photo.event

sealed interface CollectPhotoNavigationEvent {
    data object NavigateBack : CollectPhotoNavigationEvent
}