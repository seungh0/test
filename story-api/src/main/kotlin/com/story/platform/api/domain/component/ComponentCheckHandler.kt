package com.story.platform.api.domain.component

import com.story.platform.core.common.spring.HandlerAdapter
import com.story.platform.core.domain.component.ComponentNotExistsException
import com.story.platform.core.domain.component.ComponentRetriever
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ComponentCheckHandler(
    private val componentRetriever: ComponentRetriever,
) {

    suspend fun validateComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ) {
        val component = componentRetriever.getComponent(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
        )
        if (!component.isActivated()) {
            throw ComponentNotExistsException(message = "비활성화된 컴포넌트($componentId)입니다. [워크스페이스: ($workspaceId), 리소스: ($resourceId)]")
        }
    }

}
