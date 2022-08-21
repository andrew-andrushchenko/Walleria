package com.andrii_a.walleria.core

enum class PhotoListOrder(val value: String) {
    LATEST("latest"),
    OLDEST("oldest"),
    POPULAR("popular")
}

enum class SearchOrder(val value: String) {
    RELEVANT("relevant"),
    LATEST("latest")
}

enum class SearchContentFilter(val value: String) {
    LOW("low"),
    HIGH("high")
}

enum class SearchPhotoColor(val value: String?) {
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

enum class SearchPhotoOrientation(val value: String?) {
    ANY(null),
    LANDSCAPE("landscape"),
    PORTRAIT("portrait"),
    SQUARISH("squarish")
}

enum class TopicsOrder(val value: String) {
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

enum class PhotosListLayoutType {
    DEFAULT,
    MINIMAL_LIST,
    GRID
}

enum class CollectionListLayoutType {
    DEFAULT,
    GRID,
    ALBUM_COVER
}

enum class PhotoSize {
    ORIGINAL,
    LARGE,
    MEDIUM,
    SMALL,
    THUMB
}