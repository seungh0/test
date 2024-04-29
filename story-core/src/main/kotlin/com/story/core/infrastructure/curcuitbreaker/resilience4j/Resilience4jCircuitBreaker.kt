package com.story.core.infrastructure.curcuitbreaker.resilience4j

import com.story.core.infrastructure.curcuitbreaker.CircuitBreaker
import com.story.core.infrastructure.curcuitbreaker.CircuitBreakerType
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory
import org.springframework.stereotype.Component

@Component
class Resilience4jCircuitBreaker(
    private val factory: ReactiveCircuitBreakerFactory<*, *>,
) : CircuitBreaker {

    override suspend fun <T> run(circuitBreakerType: CircuitBreakerType, block: suspend () -> T): Result<T> =
        runCatching {
            factory.create(circuitBreakerType.name)
                .run(mono { block.invoke() }) { exception -> throw exception.turnToOpenExceptionIfOpen() }.awaitSingle()
        }

}