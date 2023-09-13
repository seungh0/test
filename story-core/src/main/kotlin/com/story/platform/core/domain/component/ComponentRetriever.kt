package com.story.platform.core.domain.component

import com.story.platform.core.common.model.CursorResult
import com.story.platform.core.common.model.CursorUtils
import com.story.platform.core.common.model.dto.CursorRequest
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.infrastructure.cache.CacheType
import com.story.platform.core.infrastructure.cache.Cacheable
import kotlinx.coroutines.flow.toList
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class ComponentRetriever(
    private val componentRepository: ComponentRepository,
) {

    @Cacheable(
        cacheType = CacheType.COMPONENT,
        key = "'workspaceId:' + {#workspaceId} + ':resourceId:' + {#resourceId} + ':componentId:' + {#componentId}",
    )
    suspend fun getComponent(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
    ): ComponentResponse {
        val component = componentRepository.findById(
            ComponentPrimaryKey(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
            )
        )
            ?: throw ComponentNotExistsException(message = "워크스페이스($workspaceId)에 등록되지 않은 컴포넌트($resourceId-$componentId)입니다")

        return ComponentResponse.of(component = component)
    }

    suspend fun listComponents(
        workspaceId: String,
        resourceId: ResourceId,
        cursorRequest: CursorRequest,
    ): CursorResult<ComponentResponse, String> {
        val components = listComponentsWithCursor(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = cursorRequest.cursor,
            pageSize = cursorRequest.pageSize,
        )

        return CursorResult.of(
            data = components.subList(0, cursorRequest.pageSize.coerceAtMost(components.size))
                .map { component -> ComponentResponse.of(component) },
            cursor = CursorUtils.getCursor(
                listWithNextCursor = components,
                pageSize = cursorRequest.pageSize,
                keyGenerator = { component -> component?.key?.componentId }
            )
        )
    }

    private suspend fun listComponentsWithCursor(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String?,
        pageSize: Int,
    ): List<Component> {
        if (componentId == null) {
            return componentRepository.findAllByKeyWorkspaceIdAndKeyResourceId(
                workspaceId = workspaceId,
                resourceId = resourceId,
                pageable = CassandraPageRequest.first(pageSize + 1),
            ).toList()
        }

        return componentRepository.findAllByKeyWorkspaceIdAndKeyResourceIdAndKeyComponentIdLessThan(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            pageable = CassandraPageRequest.first(pageSize + 1),
        ).toList()
    }

}
