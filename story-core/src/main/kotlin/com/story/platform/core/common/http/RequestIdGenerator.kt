package com.story.platform.core.common.http

import java.util.UUID

object RequestIdGenerator {

    private const val PREFIX = "story-"

    fun generate() = PREFIX + UUID.randomUUID().toString()

}