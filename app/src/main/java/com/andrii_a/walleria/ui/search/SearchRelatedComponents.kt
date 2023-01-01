package com.andrii_a.walleria.ui.search

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.photo.PhotoUrls
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import kotlinx.coroutines.flow.Flow

@Composable
fun UsersList(
    pagingDataFlow: Flow<PagingData<User>>,
    modifier: Modifier = Modifier,
    onUserClick: (UserNickname) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues()
) {
    val lazyUserItems = pagingDataFlow.collectAsLazyPagingItems()

    LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        modifier = modifier
    ) {
        items(lazyUserItems) { user ->
            user?.let {
                DefaultUserItem(
                    nickname = user.username.orEmpty(),
                    username = "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}",
                    profileImageUrl = user.profileImage?.large.orEmpty(),
                    previewPhotos = user.photos?.takeIf { it.size > 2 }?.take(3) ?: emptyList(),
                    onUserClick = onUserClick,
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 8.dp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DefaultUserItem(
    nickname: String,
    username: String,
    profileImageUrl: String,
    previewPhotos: List<Photo>,
    modifier: Modifier = Modifier,
    onUserClick: (UserNickname) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        onClick = {
            onUserClick(UserNickname(nickname))
        },
        modifier = modifier
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (
                profilePhoto, nicknameText, usernameText,
                photo0, photo1, photo2
            ) = createRefs()

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profileImageUrl)
                    .crossfade(true)
                    .placeholder(ColorDrawable(Color.GRAY))
                    .build(),
                contentDescription = stringResource(id = R.string.user_profile_image),
                modifier = Modifier
                    .constrainAs(profilePhoto) {
                        top.linkTo(parent.top, margin = 12.dp)
                        if (previewPhotos.isNotEmpty()) {
                            bottom.linkTo(photo0.top, margin = 12.dp)
                        } else {
                            bottom.linkTo(parent.bottom, margin = 12.dp)
                        }
                        start.linkTo(parent.start, margin = 12.dp)
                    }
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Text(
                text = username,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(usernameText) {
                    start.linkTo(profilePhoto.end, margin = 12.dp)
                    top.linkTo(profilePhoto.top)
                    end.linkTo(parent.end, margin = 12.dp)
                    width = Dimension.fillToConstraints
                }
            )

            Text(
                text = "@$nickname",
                style = MaterialTheme.typography.subtitle2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(nicknameText) {
                    start.linkTo(profilePhoto.end, margin = 12.dp)
                    top.linkTo(usernameText.bottom)
                    end.linkTo(parent.end, margin = 12.dp)
                    width = Dimension.fillToConstraints
                }
            )

            if (previewPhotos.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(previewPhotos[0].urls.small)
                        .crossfade(true)
                        .placeholder(ColorDrawable(previewPhotos[0].color?.let { Color.parseColor(it) }
                            ?: Color.GRAY))
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 100.dp, height = 120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .constrainAs(photo0) {
                            top.linkTo(profilePhoto.bottom, margin = 8.dp)
                            start.linkTo(parent.start, margin = 12.dp)
                            end.linkTo(photo1.start)
                            bottom.linkTo(parent.bottom, margin = 12.dp)
                        }
                )

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(previewPhotos[1].urls.small)
                        .crossfade(true)
                        .placeholder(ColorDrawable(previewPhotos[1].color?.let { Color.parseColor(it) }
                            ?: Color.GRAY))
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 100.dp, height = 120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .constrainAs(photo1) {
                            top.linkTo(photo0.top)
                            start.linkTo(photo0.end)
                            end.linkTo(photo2.start)
                            bottom.linkTo(photo0.bottom)
                        }
                )

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(previewPhotos[2].urls.small)
                        .crossfade(true)
                        .placeholder(ColorDrawable(previewPhotos[2].color?.let { Color.parseColor(it) }
                            ?: Color.GRAY))
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 100.dp, height = 120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .constrainAs(photo2) {
                            top.linkTo(photo1.top)
                            start.linkTo(photo1.end)
                            end.linkTo(parent.end, margin = 12.dp)
                            bottom.linkTo(photo1.bottom)
                        }
                )
            }
        }
    }
}

@Preview
@Composable
fun DefaultUserItemPreview() {
    WalleriaTheme {
        val previewPhotos = listOf(
            Photo(
                id = "",
                width = 200,
                height = 300,
                color = null,
                blurHash = "",
                views = 200,
                downloads = 200,
                likes = 10,
                likedByUser = false,
                description = "",
                exif = null,
                location = null,
                tags = null,
                relatedCollections = null,
                currentUserCollections = null,
                sponsorship = null,
                urls = PhotoUrls("", "", "", "", ""),
                links = null,
                user = null
            ),
            Photo(
                id = "",
                width = 200,
                height = 300,
                blurHash = "",
                color = null,
                views = 200,
                downloads = 200,
                likes = 10,
                likedByUser = false,
                description = "",
                exif = null,
                location = null,
                tags = null,
                relatedCollections = null,
                currentUserCollections = null,
                sponsorship = null,
                urls = PhotoUrls("", "", "", "", ""),
                links = null,
                user = null
            ),
            Photo(
                id = "",
                width = 200,
                height = 300,
                blurHash = "",
                views = 200,
                downloads = 200,
                likes = 10,
                color = null,
                likedByUser = false,
                description = "",
                exif = null,
                location = null,
                tags = null,
                relatedCollections = null,
                currentUserCollections = null,
                sponsorship = null,
                urls = PhotoUrls("", "", "", "", ""),
                links = null,
                user = null
            )
        )
        DefaultUserItem(
            nickname = "nickname",
            username = "User Name",
            profileImageUrl = "",
            previewPhotos = previewPhotos,
            onUserClick = {}
        )
    }
}