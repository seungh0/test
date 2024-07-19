package com.story.core.support.cache

import com.story.core.common.json.toJson
import com.story.core.common.json.toObject
import com.story.core.common.logger.LoggerExtension.log
import org.springframework.stereotype.Repository

@Repository
class GlobalCacheHandler(
    private val globalCacheRepository: GlobalCacheRepository,
) : CacheHandler {

    override fun cacheStrategy(): CacheStrategy = CacheStrategy.GLOBAL

    override suspend fun getCache(cacheType: CacheType, cacheKey: String): Any? {
        if (!cacheType.enableGlobalCache()) {
            return null
        }

        val redisCacheValueJson = globalCacheRepository.getCache(cacheType = cacheType, cacheKey = cacheKey)
        if (redisCacheValueJson.isNullOrBlank()) {
            return null
        }

        val globalCacheValue = redisCacheValueJson.toObject(cacheType.typeReference)
        log.debug { "[Cache] 글로벌 캐시로부터 데이터를 가져옵니다 [key:$cacheKey value: $globalCacheValue]" }
        return globalCacheValue
    }

    override suspend fun refresh(cacheType: CacheType, cacheKey: String, value: Any) {
        if (!cacheType.enableGlobalCache()) {
            return
        }

        globalCacheRepository.setCache(cacheType = cacheType, cacheKey = cacheKey, value = value.toJson())
        log.debug { "[Cache] 글로벌 캐시를 갱신합니다 [cacheType: $cacheType keyString: $cacheKey value: $value]" }
    }

    override suspend fun evict(cacheType: CacheType, cacheKey: String) {
        if (!cacheType.enableGlobalCache()) {
            return
        }

        globalCacheRepository.evict(cacheType = cacheType, cacheKey = cacheKey)
        log.debug { "[Cache] 글로벌 캐시를 삭제합니다 [cacheType: $cacheType keyString: $cacheKey]" }
    }

    override suspend fun evictAll(cacheType: CacheType) {
        if (!cacheType.enableGlobalCache()) {
            return
        }

        globalCacheRepository.evictAll(cacheType = cacheType)
        log.debug { "[Cache] 글로벌 캐시를 전체 삭제합니다 [cacheType: $cacheType]" }
    }

}