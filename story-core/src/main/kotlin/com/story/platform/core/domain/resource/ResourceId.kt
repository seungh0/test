package com.story.platform.core.domain.resource

/**
 * 매퍼 추가
 */
enum class ResourceId(
    val code: String,
    val description: String,
) {

    SUBSCRIPTIONS(code = "subscriptions", description = "구독"),
    POSTS(code = "posts", description = "포스팅"),
    ;

    companion object {
        private val cachedResourceIdMap = mutableMapOf<String, ResourceId>()

        init {
            values().forEach { resourceId -> cachedResourceIdMap[resourceId.code.lowercase()] = resourceId }
        }

        fun findByCode(code: String): ResourceId {
            return cachedResourceIdMap[code.lowercase()]
                ?: throw ResourceNotExistsException(message = "해당하는 리소스($code)는 존재하지 않습니다")
        }
    }

}
