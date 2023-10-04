package com.story.platform.api.domain.reaction

import com.story.platform.api.domain.component.ComponentCheckHandler
import com.story.platform.core.common.annotation.HandlerAdapter
import com.story.platform.core.domain.reaction.ReactionRetriever
import com.story.platform.core.domain.resource.ResourceId

@HandlerAdapter
class ReactionRetrieveHandler(
    private val reactionRetriever: ReactionRetriever,
    private val componentCheckHandler: ComponentCheckHandler,
) {

    suspend fun getReaction(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        request: ReactionGetApiRequest,
    ): ReactionApiResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTION,
            componentId = componentId,
        )

        val reaction = reactionRetriever.getReaction(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            accountId = request.accountId,
        )

        return ReactionApiResponse.of(reaction = reaction)
    }

    suspend fun listReactions(
        workspaceId: String,
        componentId: String,
        request: ReactionListApiRequest,
    ): ReactionListApiResponse {
        componentCheckHandler.checkExistsComponent(
            workspaceId = workspaceId,
            resourceId = ResourceId.REACTION,
            componentId = componentId,
        )

        val reactions = reactionRetriever.listReactions(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceIds = request.spaceIds,
            accountId = request.accountId,
        )

        return ReactionListApiResponse.of(reactions = reactions)
    }

}