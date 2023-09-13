package com.story.platform.core.domain.feed.mapping

import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import com.story.platform.core.infrastructure.lock.DistributedLock
import com.story.platform.core.infrastructure.lock.DistributedLockType
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.stereotype.Service

@Service
class FeedMappingCreator(
    private val feedMappingConfigurationRepository: FeedMappingConfigurationRepository,
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
) {

    @DistributedLock(
        lockType = DistributedLockType.FEED_MAPPING,
        key = "'workspaceId:' + {#request.workspaceId} + ':feedComponentId:' + {#request.feedComponentId}"
    )
    suspend fun create(
        request: FeedMappingCreateRequest,
    ) {
        if (feedMappingConfigurationRepository.existsById(request.toConfiguration().key)) {
            throw FeedMappingAlreadyConnectedException("이미 워크스페이스(${request.workspaceId})의 리소스(${request.sourceResourceId})의 컴포넌트(${request.sourceComponentId})의 구독(${request.subscriptionComponentId})와 피드 연동 설정이 등록되어 있습니다")
        }

        val feeds = feedMappingConfigurationRepository.findAllByKeyWorkspaceIdAndKeyFeedComponentIdAndKeySourceResourceIdAndKeySourceComponentId(
            workspaceId = request.workspaceId,
            feedComponentId = request.feedComponentId,
            sourceResourceId = request.sourceResourceId,
            sourceComponentId = request.sourceComponentId,
            pageable = CassandraPageRequest.first(3)
        )

        if (feeds.size >= 3) {
            throw FeedMappingCapacityExceededException("워크스페이스(${request.workspaceId})에서 FeedComponent(${request.feedComponentId})의 Source(${request.sourceResourceId}-${request.sourceComponentId})에서 발행할 수 있는 최대 연결은 3개로 제한됩니다")
        }

        val feedMappingConfiguration = request.toConfiguration()
        reactiveCassandraOperations.batchOps()
            .upsert(feedMappingConfiguration)
            .upsert(FeedReverseMappingConfiguration.of(feedMappingConfiguration))
            .executeCoroutine()
    }

}
