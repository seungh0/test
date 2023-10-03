package com.story.platform.core.domain.purge

import com.story.platform.core.common.annotation.EventProducer
import com.story.platform.core.common.annotation.IOBound
import com.story.platform.core.common.distribution.DistributionKey
import com.story.platform.core.common.json.toJson
import com.story.platform.core.domain.event.EventHistoryManager
import com.story.platform.core.domain.resource.ResourceId
import com.story.platform.core.infrastructure.kafka.KafkaProducerConfig
import com.story.platform.core.infrastructure.kafka.KafkaTopic
import com.story.platform.core.infrastructure.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate

@EventProducer
class PurgeDistributeEventProducer(
    @Qualifier(KafkaProducerConfig.DEFAULT_ACK_ALL_KAFKA_PRODUCER)
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val eventHistoryManager: EventHistoryManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun publishEvents(
        workspaceId: String,
        resourceId: ResourceId,
        componentId: String,
        distributionKeys: Collection<DistributionKey>,
    ) {
        val events = distributionKeys.map { distributionKey ->
            PurgeDistributeEvent.created(
                workspaceId = workspaceId,
                resourceId = resourceId,
                componentId = componentId,
                distributionKey = distributionKey,
            )
        }

        eventHistoryManager.withSaveEventHistories(
            workspaceId = workspaceId,
            resourceId = resourceId,
            componentId = componentId,
            events = events,
        ) {
            withContext(dispatcher) {
                events.map { event ->
                    launch {
                        kafkaTemplate.send(kafkaTopic = KafkaTopic.PURGE, data = event.toJson())
                    }
                }.joinAll()
            }
        }
    }

}
