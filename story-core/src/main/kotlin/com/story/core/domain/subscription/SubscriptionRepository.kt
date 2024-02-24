package com.story.core.domain.subscription

import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface SubscriptionRepository :
    CassandraBasicRepository<Subscription, SubscriptionPrimaryKey> {

    suspend fun findByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyTargetId(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        targetId: String,
    ): Subscription?

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyOrderByKeyTargetIdAsc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        pageable: Pageable,
    ): Slice<Subscription>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdOrderByKeyTargetIdAsc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<Subscription>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyTargetIdGreaterThanOrderByKeyTargetIdAsc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Flow<Subscription>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdOrderByKeyTargetIdDesc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        pageable: Pageable,
    ): Flow<Subscription>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKeyAndKeySubscriberIdAndKeyTargetIdLessThanOrderByKeyTargetIdDesc(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
        subscriberId: String,
        targetId: String,
        pageable: Pageable,
    ): Flow<Subscription>

    suspend fun deleteAllByKeyWorkspaceIdAndKeyComponentIdAndKeyDistributionKey(
        workspaceId: String,
        componentId: String,
        distributionKey: String,
    )

}
