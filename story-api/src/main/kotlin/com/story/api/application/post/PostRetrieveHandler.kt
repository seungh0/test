package com.story.api.application.post

import com.story.api.application.component.ComponentCheckHandler
import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.post.PostId
import com.story.core.domain.post.PostRetriever
import com.story.core.domain.post.PostSpaceKey
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class PostRetrieveHandler(
    private val postRetriever: PostRetriever,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun getPost(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        postId: PostId,
        requestUserId: String?,
    ): PostResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = componentId,
        )

        val post = postRetriever.getPost(
            postSpaceKey = PostSpaceKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            postId = postId,
        )
        return PostResponse.of(post = post, requestUserId = requestUserId)
    }

    suspend fun listPosts(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        request: PostListRequest,
        requestUserId: String?,
    ): PostListResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = componentId,
        )

        val posts = postRetriever.listPosts(
            postSpaceKey = PostSpaceKey(
                workspaceId = workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            parentId = request.parentId,
            cursorRequest = request.toDecodedCursor(),
            sortBy = request.getSortBy(),
        )

        return PostListResponse.of(posts = posts, requestUserId = requestUserId)
    }

    suspend fun listOwnerPosts(
        workspaceId: String,
        componentId: String,
        request: PostListRequest,
        ownerId: String,
    ): PostListResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.POSTS,
            componentId = componentId,
        )

        val posts = postRetriever.listOwnerPosts(
            workspaceId = workspaceId,
            componentId = componentId,
            ownerId = ownerId,
            cursorRequest = request.toDecodedCursor(),
        )

        return PostListResponse.of(posts = posts, requestUserId = ownerId)
    }

}
