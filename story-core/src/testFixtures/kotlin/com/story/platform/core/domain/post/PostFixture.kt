package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.AuditingTime
import com.story.platform.core.support.RandomGenerator

object PostFixture {

    fun create(
        serviceType: ServiceType = RandomGenerator.generateEnum(ServiceType::class.java),
        spaceType: PostSpaceType = RandomGenerator.generateEnum(PostSpaceType::class.java),
        spaceId: String = RandomGenerator.generateString(),
        accountId: String = RandomGenerator.generateString(),
        postId: String = RandomGenerator.generateLong().toString(),
        title: String = RandomGenerator.generateString(),
        content: String = RandomGenerator.generateString(),
        extraJson: String? = RandomGenerator.generateString(),
    ) = Post(
        key = PostPrimaryKey.of(
            postSpaceKey = PostSpaceKey(
                serviceType = serviceType,
                spaceType = spaceType,
                spaceId = spaceId,
            ),
            postId = postId,
        ),
        accountId = accountId,
        title = title,
        content = content,
        extraJson = extraJson,
        auditingTime = AuditingTime.newEntity(),
    )

}
