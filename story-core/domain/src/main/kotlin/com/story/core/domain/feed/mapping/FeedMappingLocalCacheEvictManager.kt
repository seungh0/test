package com.story.core.domain.feed.mapping

import com.story.core.domain.resource.ResourceId
import com.story.core.support.cache.CacheEvict
import com.story.core.support.cache.CacheStrategy
import com.story.core.support.cache.CacheType
import org.springframework.stereotype.Service

@Service
class FeedMappingLocalCacheEvictManager {

    @CacheEvict(
        cacheType = CacheType.FEED_MAPPING,
        key = "'workspaceId:' + {#workspaceId} + ':sourceResourceId:' + {#sourceResourceId} + ':sourceComponentId:' + {#sourceComponentId}",
        targetCacheStrategies = [CacheStrategy.LOCAL]
    )
    suspend fun evictFeedMapping(
        workspaceId: String,
        sourceResourceId: ResourceId,
        sourceComponentId: String,
    ) {
    }

}