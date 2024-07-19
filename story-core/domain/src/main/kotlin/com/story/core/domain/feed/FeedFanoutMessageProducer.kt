package com.story.core.domain.feed

import com.story.core.common.annotation.EventProducer
import com.story.core.common.annotation.IOBound
import com.story.core.common.json.toJson
import com.story.core.domain.event.EventHistoryManager
import com.story.core.domain.event.EventRecord
import com.story.core.domain.resource.ResourceId
import com.story.core.support.kafka.KafkaProducerConfig
import com.story.core.support.kafka.KafkaRecordKeyGenerator
import com.story.core.support.kafka.KafkaTopic
import com.story.core.support.kafka.send
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate

@EventProducer
class FeedFanoutMessageProducer(
    @Qualifier(KafkaProducerConfig.FEED_KAFKA_PRODUCER)
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val eventHistoryManager: EventHistoryManager,

    @IOBound
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun publish(event: EventRecord<FeedFanoutMessage>) {
        eventHistoryManager.withSaveEventHistory(
            workspaceId = event.payload.workspaceId,
            resourceId = ResourceId.FEEDS,
            componentId = event.payload.feedComponentId,
            event = event,
        ) {
            withContext(dispatcher) {
                kafkaTemplate.send(
                    kafkaTopic = KafkaTopic.FEED_FANOUT,
                    key = KafkaRecordKeyGenerator.feed(
                        eventKey = event.eventKey,
                        slotId = event.payload.slotId,
                        workspaceId = event.payload.workspaceId,
                        feedComponentId = event.payload.feedComponentId,
                    ),
                    data = event.toJson(),
                )
            }
        }
    }

}