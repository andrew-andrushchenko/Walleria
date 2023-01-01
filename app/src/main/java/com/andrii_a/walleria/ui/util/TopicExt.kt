package com.andrii_a.walleria.ui.util

import com.andrii_a.walleria.domain.models.topic.Topic

val Topic.ownerUsername: String
    get() = this.owners?.first()?.username.orEmpty()