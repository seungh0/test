package com.story.core.support.cache

import com.story.core.common.coroutine.coroutineArgs
import com.story.core.common.coroutine.proceedCoroutine
import com.story.core.common.coroutine.runCoroutine
import com.story.core.common.error.InternalServerException
import com.story.core.support.spring.SpringExpressionParser
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Aspect
@Component
class CacheableAspect(
    private val cacheManager: LayeredCacheManager,
) {

    @Around("@annotation(Cacheable) && args(.., kotlin.coroutines.Continuation)")
    fun handleLayeredCache(joinPoint: ProceedingJoinPoint): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val cacheable = methodSignature.method.getAnnotation(Cacheable::class.java)

        return joinPoint.runCoroutine {
            if (!precondition(joinPoint = joinPoint, methodSignature = methodSignature, cacheable = cacheable)) {
                val result = joinPoint.proceedCoroutine()
                if (result is Mono<*>) {
                    // for spring 6.1.0 and later
                    return@runCoroutine result.awaitSingleOrNull()
                }
                return@runCoroutine result
            }

            val cacheKey = SpringExpressionParser.parseString(
                parameterNames = methodSignature.parameterNames,
                args = joinPoint.coroutineArgs,
                key = cacheable.key,
            )

            if (cacheKey.isNullOrBlank()) {
                throw InternalServerException("@Cacheable key can't be blank. [cacheType: ${cacheable.cacheType} parameterNames: ${methodSignature.parameterNames} args: ${joinPoint.coroutineArgs} key: ${cacheable.key}]")
            }

            val cacheValue = cacheManager.getCacheFromLayeredCache(
                cacheType = cacheable.cacheType,
                cacheKey = cacheKey,
            )

            if (cacheValue != null) {
                return@runCoroutine cacheValue
            }

            val result = joinPoint.proceedCoroutine()
            if (result is Mono<*>) {
                // for spring 6.1.0 and later
                val resultAwait = result.awaitSingleOrNull()
                if (resultAwait != null) {
                    cacheManager.refreshCacheLayeredCache(
                        cacheType = cacheable.cacheType,
                        cacheKey = cacheKey,
                        value = resultAwait
                    )
                }
                return@runCoroutine resultAwait
            }

            if (result != null) {
                cacheManager.refreshCacheLayeredCache(
                    cacheType = cacheable.cacheType,
                    cacheKey = cacheKey,
                    value = result
                )
            }
            return@runCoroutine result
        }
    }

    private suspend fun precondition(
        joinPoint: ProceedingJoinPoint,
        methodSignature: MethodSignature,
        cacheable: Cacheable,
    ): Boolean {
        val unless = SpringExpressionParser.parseBoolean(
            parameterNames = methodSignature.parameterNames,
            args = joinPoint.coroutineArgs,
            key = cacheable.unless,
        )

        val condition = SpringExpressionParser.parseBoolean(
            parameterNames = methodSignature.parameterNames,
            args = joinPoint.coroutineArgs,
            key = cacheable.condition,
        )

        return (unless == null || !unless) && (condition == null || condition)
    }

}