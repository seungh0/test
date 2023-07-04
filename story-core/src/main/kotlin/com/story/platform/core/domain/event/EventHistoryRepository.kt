package com.story.platform.core.domain.event

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EventHistoryRepository : CoroutineCrudRepository<EventHistory, EventHistoryPrimaryKey>
