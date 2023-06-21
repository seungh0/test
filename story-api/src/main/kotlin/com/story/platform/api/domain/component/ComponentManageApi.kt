package com.story.platform.api.domain.component

import com.story.platform.api.config.auth.AuthContext
import com.story.platform.api.config.auth.RequestAuthContext
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.component.ComponentManager
import com.story.platform.core.domain.resource.ResourceId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ComponentManageApi(
    private val componentManager: ComponentManager,
) {

    /**
     * 신규 컴포넌트를 생성합니다
     */
    @PostMapping("/v1/resources/{resourceId}/components/{componentId}")
    suspend fun create(
        @PathVariable resourceId: String,
        @PathVariable componentId: String,
        @RequestAuthContext authContext: AuthContext,
        @Valid @RequestBody request: ComponentCreateApiRequest,
    ): ApiResponse<String> {
        componentManager.create(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            componentId = componentId,
            description = request.description,
        )
        return ApiResponse.OK
    }

    /**
     * 컴포넌트를 수정합니다
     */
    @PatchMapping("/v1/resources/{resourceId}/components/{componentId}")
    suspend fun modify(
        @PathVariable resourceId: String,
        @PathVariable componentId: String,
        @RequestAuthContext authContext: AuthContext,
        @Valid @RequestBody request: ComponentModifyApiRequest,
    ): ApiResponse<String> {
        componentManager.patch(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            componentId = componentId,
            description = request.description,
            status = request.status,
        )
        return ApiResponse.OK
    }

}
