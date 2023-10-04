package com.story.platform.api.domain.subscription

import com.story.platform.core.common.model.ContentsWithCursor
import com.story.platform.core.common.model.Cursor
import com.story.platform.core.domain.subscription.SubscriptionResponse

data class SubscriptionTargetListApiResponse(
    val targets: List<SubscriptionTargetApiResponse>,
    val cursor: Cursor<String>,
) {

    companion object {
        fun of(subscriptions: ContentsWithCursor<SubscriptionResponse, String>) = SubscriptionTargetListApiResponse(
            targets = subscriptions.data.map { subscription -> SubscriptionTargetApiResponse.of(subscription = subscription) },
            cursor = subscriptions.cursor,
        )
    }

}