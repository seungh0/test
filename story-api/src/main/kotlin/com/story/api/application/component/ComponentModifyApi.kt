package com.story.api.application.component

import com.story.api.config.apikey.ApiKeyContext
import com.story.api.config.apikey.RequestApiKey
import com.story.core.common.model.dto.ApiResponse
import com.story.core.domain.resource.ResourceId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ComponentModifyApi(
    private val componentModifyHandler: ComponentModifyHandler,
) {

    /**
     * 컴포넌트를 수정합니다
     */
    @PatchMapping("/v1/resources/{resourceId}/components/{componentId}")
    suspend fun patchComponent(
        @PathVariable resourceId: String,
        @PathVariable componentId: String,
        @RequestApiKey authContext: ApiKeyContext,
        @Valid @RequestBody request: ComponentModifyRequest,
    ): ApiResponse<Nothing?> {
        componentModifyHandler.patchComponent(
            workspaceId = authContext.workspaceId,
            resourceId = ResourceId.findByCode(resourceId),
            componentId = componentId,
            description = request.description,
            status = request.status,
        )
        return ApiResponse.OK
    }

}
