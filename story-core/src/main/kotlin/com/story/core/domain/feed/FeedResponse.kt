package com.story.core.domain.feed

import com.story.core.common.json.toObject
import com.story.core.domain.event.BaseEvent

data class FeedResponse<T : BaseEvent>(
    val feedId: Long,
    val resourceId: String,
    val componentId: String,
    val payload: T,
) {

    companion object {
        fun <T : BaseEvent> of(feed: Feed): FeedResponse<T> {
            val payload = feed.payloadJson.toObject(feed.sourceResourceId.feedPayloadClazz!!) as T
            return FeedResponse(
                feedId = feed.key.feedId,
                resourceId = feed.sourceResourceId.code,
                componentId = feed.sourceComponentId,
                payload = payload,
            )
        }
    }

}