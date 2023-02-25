package com.story.platform.core.domain.post

import com.story.platform.core.common.error.ForbiddenException
import com.story.platform.core.common.error.NotFoundException
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class PostModifier(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val postCoroutineRepository: PostCoroutineRepository,
) {

    suspend fun modify(
        postSpaceKey: PostSpaceKey,
        accountId: String,
        postId: Long,
        title: String,
        content: String,
        extraJson: String? = null,
    ) {
        val slotId = PostSlotAllocator.allocate(postId)
        val post = postCoroutineRepository.findByKeyServiceTypeAndKeySpaceTypeAndKeySpaceIdAndKeySlotIdAndKeyPostId(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            slotId = slotId,
            postId = postId,
        ) ?: throw NotFoundException("해당하는 포스트($postSpaceKey-$postId)는 존재하지 않습니다")

        if (!post.isOwner(accountId)) {
            throw ForbiddenException("계정($accountId)는 해당하는 포스트($postSpaceKey-$postId)를 수정할 권한이 없습니다")
        }

        post.update(
            title = title,
            content = content,
            extraJson = extraJson,
        )

        reactiveCassandraOperations.batchOps()
            .insert(post)
            .insert(PostReverse.of(post))
            .execute()
            .awaitSingleOrNull()
    }

}