package com.story.platform.worker.event.domain.feed

import com.story.platform.core.common.coroutine.IOBound
import com.story.platform.core.common.json.toJson
import com.story.platform.core.common.json.toObject
import com.story.platform.core.common.spring.EventConsumer
import com.story.platform.core.domain.event.EventRecord
import com.story.platform.core.domain.post.PostEvent
import com.story.platform.core.domain.subscription.SubscriptionEvent
import com.story.platform.core.infrastructure.kafka.KafkaConsumerConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload

@EventConsumer
class FeedDistributeEventConsumer(
    @IOBound
    private val dispatcher: CoroutineDispatcher,
    private val postFeedDistributeHandler: PostFeedDistributeHandler,
    private val subscriptionFeedDistributeHandler: SubscriptionFeedDistributeHandler,
) {

    @KafkaListener(
        topics = ["\${story.kafka.topic.post}"],
        groupId = GROUP_ID,
        containerFactory = KafkaConsumerConfig.POST_CONTAINER_FACTORY,
    )
    fun handlePostFeedEvent(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        val event = record.value().toObject(EventRecord::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        val payload = event.payload.toJson().toObject(PostEvent::class.java)
            ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")

        withContext(dispatcher) {
            postFeedDistributeHandler.distributePostFeeds(
                payload = payload,
                eventId = event.eventId,
                eventAction = event.eventAction,
                eventKey = event.eventKey,
            )
        }
    }

    @KafkaListener(
        topics = ["\${story.kafka.topic.subscription}"],
        groupId = GROUP_ID,
        containerFactory = KafkaConsumerConfig.SUBSCRIPTION_CONTAINER_FACTORY,
    )
    fun handleSubscriptionFeedEvent(
        @Payload record: ConsumerRecord<String, String>,
        @Headers headers: Map<String, Any>,
    ) = runBlocking {
        val event = record.value().toObject(EventRecord::class.java)
            ?: throw IllegalArgumentException("Record can't be deserialize, record: $record")

        val payload = event.payload.toJson().toObject(SubscriptionEvent::class.java)
            ?: throw IllegalArgumentException("Record Payload can't be deserialize, record: $record")

        withContext(dispatcher) {
            subscriptionFeedDistributeHandler.distributePostFeeds(
                payload = payload,
                eventId = event.eventId,
                eventAction = event.eventAction,
                eventKey = event.eventKey,
            )
        }
    }

    companion object {
        private const val GROUP_ID = "feed-event-consumer"
    }

}
