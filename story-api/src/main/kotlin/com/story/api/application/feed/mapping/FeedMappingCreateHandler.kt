package com.story.api.application.feed.mapping

import com.story.core.common.annotation.HandlerAdapter
import com.story.core.domain.component.ComponentNotExistsException
import com.story.core.domain.component.ComponentRetriever
import com.story.core.domain.feed.mapping.FeedMappingCreateRequest
import com.story.core.domain.feed.mapping.FeedMappingCreator
import com.story.core.domain.feed.mapping.FeedMappingEventProducer
import com.story.core.domain.resource.ResourceId

@HandlerAdapter
class FeedMappingCreateHandler(
    private val componentRetriever: ComponentRetriever,
    private val feedMappingCreator: FeedMappingCreator,
    private val feedMappingEventProducer: FeedMappingEventProducer,
) {

    suspend fun create(
        workspaceId: String,
        feedComponentId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
        subscriptionComponentId: String,
        request: FeedMappingCreateApiRequest,
    ) {
        setOf(
            ResourceId.FEEDS to feedComponentId,
            sourceResourceId to sourceComponentId,
            ResourceId.SUBSCRIPTIONS to subscriptionComponentId,
        ).forEach { (resourceId: ResourceId, componentId: String) ->
            componentRetriever.getComponent(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
                .orElseThrow { ComponentNotExistsException(message = "워크스페이스($workspaceId)에 등록되지 않은 컴포넌트($resourceId-$componentId)입니다") }
        }

        feedMappingCreator.create(
            request = FeedMappingCreateRequest(
                workspaceId = workspaceId,
                feedComponentId = feedComponentId,
                sourceResourceId = sourceResourceId,
                sourceComponentId = sourceComponentId,
                description = request.description,
                retention = request.retention,
                subscriptionComponentId = subscriptionComponentId,
            )
        )

        feedMappingEventProducer.publishCreatedEvent(
            workspaceId = workspaceId,
            feedComponentId = feedComponentId,
            sourceComponentId = sourceComponentId,
            sourceResourceId = sourceResourceId,
            subscriptionComponentId = subscriptionComponentId,
        )
    }

}
