package com.story.api.application.subscription

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class SubscriptionRemoveApi(
    private val subscriptionRemoveHandler: SubscriptionRemoveHandler,
) {

    /**
     * 구독을 취소한다
     */
    @DeleteMapping("/v1/resources/subscriptions/components/{componentId}/subscribers/{subscriberId}/targets/{targetId}")
    suspend fun removeSubscription(
        @PathVariable componentId: String,
        @PathVariable subscriberId: String,
        @PathVariable targetId: String,
        @RequestApiKey authContext: ApiKeyContext,
    ): ApiResponse<Nothing?> {
        subscriptionRemoveHandler.removeSubscription(
            workspaceId = authContext.workspaceId,
            componentId = componentId,
            targetId = targetId,
            subscriberId = subscriberId,
        )
        return ApiResponse.OK
    }

}
