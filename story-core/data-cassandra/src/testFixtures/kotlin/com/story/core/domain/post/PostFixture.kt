package com.story.core.domain.post

import com.story.core.common.model.AuditingTimeEntity
import com.story.core.support.RandomGenerator

object PostFixture {

    fun create(
        workspaceId: String = RandomGenerator.generateString(),
        componentId: String = RandomGenerator.generateString(),
        spaceId: String = RandomGenerator.generateString(),
        ownerId: String = RandomGenerator.generateString(),
        parentId: PostId? = null,
        postNo: Long = RandomGenerator.generateLong(),
        title: String = RandomGenerator.generateString(),
        extra: Map<String, String> = emptyMap(),
        metadata: Map<PostMetadataType, String> = emptyMap(),
    ) = PostEntity(
        key = PostPrimaryKey.of(
            workspaceId = workspaceId,
            componentId = componentId,
            spaceId = spaceId,
            parentId = parentId,
            postNo = postNo,
        ),
        ownerId = ownerId,
        title = title,
        extra = extra.toMutableMap(),
        metadata = metadata.toMutableMap(),
        auditingTime = AuditingTimeEntity.created(),
    )

}