package com.story.platform.api.domain.component

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.component.ComponentEvent
import com.story.platform.core.domain.component.ComponentEventPublisher
import com.story.platform.core.domain.component.ComponentModifier
import com.story.platform.core.domain.component.ComponentResponse
import com.story.platform.core.domain.component.ComponentStatus
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ComponentModifyHandler(
    private val componentModifier: ComponentModifier,
    private val componentEventPublisher: ComponentEventPublisher,
) {

    suspend fun patchComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        description: String?,
        status: ComponentStatus?,
    ): ComponentResponse {
        val component = componentModifier.patchComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            description = description,
            status = status,
        )

        componentEventPublisher.publishEvent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            event = ComponentEvent.updated(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                status = component.status,
            )
        )
        return component
    }

}
