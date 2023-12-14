package com.story.platform.core.domain.subscription

import com.story.platform.core.infrastructure.cassandra.executeCoroutine
import com.story.platform.core.infrastructure.cassandra.upsert
import com.story.platform.core.infrastructure.lock.DistributedLock
import com.story.platform.core.infrastructure.lock.DistributedLockType
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.stereotype.Service

@Service
class SubscriptionUnSubscriber(
    private val reactiveCassandraOperations: ReactiveCassandraOperations,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriberRepository: SubscriberRepository,
) {

    @DistributedLock(
        lockType = DistributedLockType.SUBSCRIBE,
        key = "'workspaceId:' + {#workspaceId} + ':componentId:' + {#componentId} + ':targetId:' + {#targetId} + ':subscriberId:' + {#subscriberId}",
    )
    suspend fun removeSubscription(
        workspaceId: String,
        componentId: String,
        targetId: String,
        subscriberId: String,
    ): Boolean {
        val subscription = subscriptionRepository.findById(
            SubscriptionPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                distributionKey = SubscriptionDistributionKey.makeKey(subscriberId),
                subscriberId = subscriberId,
                targetId = targetId,
            )
        )

        if (subscription == null || subscription.isDeleted()) {
            return false
        }

        val subscriber = subscriberRepository.findById(
            SubscriberPrimaryKey(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                slotId = subscription.slotId,
                subscriberId = subscriberId,
            )
        )
        subscription.delete()

        reactiveCassandraOperations.batchOps()
            .delete(subscriber)
            .upsert(subscription)
            .executeCoroutine()

        return true
    }

}
