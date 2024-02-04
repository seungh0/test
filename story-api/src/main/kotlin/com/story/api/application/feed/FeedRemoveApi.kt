package com.story.api.application.feed

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FeedRemoveApi(
    private val feedRemoveHandler: FeedRemoveHandler,
) {

    @DeleteMapping("/v1/resources/feeds/components/{componentId}/subscribers/{subscriberId}/feeds/{feedId}")
    suspend fun removeFeed(
        @PathVariable componentId: String,
        @PathVariable subscriberId: String,
        @PathVariable feedId: Long,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<Nothing?> {
        feedRemoveHandler.remove(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            subscriberId = subscriberId,
            feedId = feedId,
        )
        return ApiResponse.OK
    }

}
