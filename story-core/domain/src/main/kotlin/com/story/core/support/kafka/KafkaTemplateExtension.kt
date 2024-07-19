package com.story.core.support.kafka

import com.story.core.common.logger.LoggerExtension.log
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import java.util.concurrent.CompletableFuture

fun <K : Any, V> KafkaTemplate<K, V>.send(kafkaTopic: KafkaTopic, key: K, data: V): CompletableFuture<SendResult<K, V>> {
    val sendResult = this.send(KafkaTopicFinder.getTopicName(kafkaTopic), key, data)
    sendResult.handleExceptionAsync()
    return sendResult
}

fun <K : Any, V> KafkaTemplate<K, V>.send(kafkaTopic: KafkaTopic, data: V): CompletableFuture<SendResult<K, V>> {
    val sendResult = this.send(KafkaTopicFinder.getTopicName(kafkaTopic), data)
    sendResult.handleExceptionAsync()
    return sendResult
}

private fun <K : Any, V> CompletableFuture<SendResult<K, V>>.handleExceptionAsync() {
    this.handleAsync { result, throwable ->
        if (throwable == null) {
            return@handleAsync
        }
        log.error(throwable) {
            """
                Kafka Produce failed : ${throwable.message}
                record key: ${result.producerRecord.key()}
                record value: ${result.producerRecord.value()}
                record headers: ${result.producerRecord.headers()}
                record partition: ${result.producerRecord.partition()}
                record timestamp: ${result.producerRecord.timestamp()}
                record metadata:${result.recordMetadata}
            """.trimIndent()
        }
    }
}