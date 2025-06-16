package com.andrii_a.walleria.domain

enum class PhotoListDisplayOrder(val value: String) {
    LATEST("latest"),
    OLDEST("oldest"),
    POPULAR("popular")
}

enum class SearchResultsDisplayOrder(val value: String) {
    RELEVANT("relevant"),
    LATEST("latest")
}

enum class SearchResultsContentFilter(val value: String) {
    LOW("low"),
    HIGH("high")
}

enum class SearchResultsPhotoColor(val value: String?) {
    ANY(null),
    BLACK_AND_WHITE("black_and_white"),
    BLACK("black"),
    WHITE("white"),
    YELLOW("yellow"),
    ORANGE("orange"),
    RED("red"),
    PURPLE("purple"),
    MAGENTA("magenta"),
    GREEN("green"),
    TEAL("teal"),
    BLUE("blue")
}

enum class SearchResultsPhotoOrientation(val value: String?) {
    ANY(null),
    LANDSCAPE("landscape"),
    PORTRAIT("portrait"),
    SQUARISH("squarish")
}

enum class TopicsDisplayOrder(val value: String) {
    FEATURED("featured"),
    LATEST("latest"),
    OLDEST("oldest"),
    POSITION("position")
}

enum class TopicPhotosOrientation(val value: String) {
    LANDSCAPE("landscape"),
    PORTRAIT("portrait"),
    SQUARISH("squarish")
}

enum class TopicStatus {
    OPEN,
    CLOSED,
    OTHER
}

enum class PhotoQuality {
    RAW,
    HIGH,
    MEDIUM,
    LOW,
    THUMBNAIL
}

enum class UserProfileImageQuality {
    LOW,
    MEDIUM,
    HIGH
}

enum class AppTheme {
    SYSTEM_DEFAULT,
    LIGHT,
    DARK
}