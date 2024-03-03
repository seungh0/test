package com.story.core.infrastructure.cache

import com.story.core.common.coroutine.coroutineArgs
import com.story.core.common.coroutine.proceedCoroutine
import com.story.core.common.coroutine.runCoroutine
import com.story.core.common.error.InternalServerException
import com.story.core.infrastructure.spring.SpringExpressionParser
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class CacheEvictAspect(
    private val cacheManager: CacheManager,
) {

    @Around("args(.., kotlin.coroutines.Continuation) && @annotation(CacheEvict)")
    fun evict(joinPoint: ProceedingJoinPoint): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        val cacheEvict = method.getAnnotation(CacheEvict::class.java)

        return joinPoint.runCoroutine {
            val result = joinPoint.proceedCoroutine(joinPoint.coroutineArgs)
            if (!precondition(joinPoint = joinPoint, methodSignature = methodSignature, cacheEvict = cacheEvict)) {
                return@runCoroutine result
            }

            val cacheType = cacheEvict.cacheType

            if (cacheEvict.allEntries) {
                cacheManager.evictAllCachesLayeredCache(cacheType = cacheType)
                return@runCoroutine result
            }

            val cacheKeyString = SpringExpressionParser.parseString(
                parameterNames = methodSignature.parameterNames,
                args = joinPoint.coroutineArgs,
                key = cacheEvict.key,
            )

            if (cacheKeyString.isNullOrBlank()) {
                throw InternalServerException("@CacheEvict key can't be null. [cacheType: ${cacheEvict.cacheType} parameterNames: ${methodSignature.parameterNames} args: ${joinPoint.coroutineArgs} key: ${cacheEvict.key}]")
            }

            cacheManager.evictCacheLayeredCache(
                cacheType = cacheType,
                cacheKey = cacheKeyString,
                targetCacheStrategies = cacheEvict.targetCacheStrategies.toSet()
            )

            return@runCoroutine result
        }
    }

    private fun precondition(
        joinPoint: ProceedingJoinPoint,
        methodSignature: MethodSignature,
        cacheEvict: CacheEvict,
    ): Boolean {
        val unless = SpringExpressionParser.parseBoolean(
            parameterNames = methodSignature.parameterNames,
            args = joinPoint.coroutineArgs,
            key = cacheEvict.unless,
        )

        val condition = SpringExpressionParser.parseBoolean(
            parameterNames = methodSignature.parameterNames,
            args = joinPoint.coroutineArgs,
            key = cacheEvict.condition,
        )

        return (unless == null || !unless) && (condition == null || condition)
    }

}
