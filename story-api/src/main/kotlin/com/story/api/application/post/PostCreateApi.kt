package com.story.api.application.post

import com.story.api.config.auth.AuthContext
import com.story.api.config.auth.RequestAuthContext
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.post.PostSpaceKey
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PostCreateApi(
    private val postCreateHandler: PostCreateHandler,
) {

    /**
     * 신규 포스트를 등록한다
     */
    @PostMapping("/v1/resources/posts/components/{componentId}/spaces/{spaceId}/posts")
    suspend fun createPost(
        @PathVariable componentId: String,
        @PathVariable spaceId: String,
        @Valid @RequestBody request: PostCreateApiRequest,
        @RequestAuthContext authContext: AuthContext,
        @RequestParam(required = false) nonce: String? = null,
    ): ApiResponse<PostCreateApiResponse> {
        val postId = postCreateHandler.createPost(
            postSpaceKey = PostSpaceKey(
                workspaceId = authContext.workspaceId,
                componentId = componentId,
                spaceId = spaceId,
            ),
            accountId = authContext.getRequiredRequestAccountId(),
            title = request.title,
            sections = request.toSections(),
            nonce = nonce,
        )
        return ApiResponse.ok(PostCreateApiResponse.of(postId = postId))
    }

}