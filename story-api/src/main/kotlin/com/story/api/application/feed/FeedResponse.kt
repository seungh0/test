package com.story.api.application.feed

import com.story.core.domain.feed.Feed
import com.story.core.domain.feed.FeedPayload

data class FeedResponse(
    val feedId: String,
    val resourceId: String,
    val componentId: String,
    val payload: FeedPayload,
) {

    companion object {
        fun of(feed: Feed, payload: FeedPayload) = FeedResponse(
            feedId = feed.feedId.toString(),
            resourceId = feed.sourceResourceId.code,
            componentId = feed.sourceComponentId,
            payload = payload,
        )
    }

}