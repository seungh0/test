package com.story.platform.core.support.cache

import java.time.Duration

enum class CacheType(
    private val description: String,
    val key: String,
    val localCacheTtl: Duration? = null,
    val globalCacheTtl: Duration? = null,
) {

    SUBSCRIBERS_COUNT(
        description = "구독자 수",
        key = "subscribers-count:v1",
        globalCacheTtl = Duration.ofMinutes(1),
    ),
    TARGET_SUBSCRIBERS(
        description = "대상 구독자들 목록",
        key = "target-subscribers:v1",
        globalCacheTtl = Duration.ofMinutes(1),
    )
    ;

    fun enableLocalCache(): Boolean {
        return localCacheTtl != null && Duration.ZERO < localCacheTtl
    }

    fun enableGlobalCache(): Boolean {
        return globalCacheTtl != null && Duration.ZERO < globalCacheTtl
    }

    companion object {
        @JvmField
        val LOCAL_CACHE_TYPES = CacheType.values()
            .filter { cacheType -> cacheType.enableLocalCache() }
    }

}