package com.story.core.domain.reaction

import com.story.core.support.cassandra.CassandraCounterRepository
import org.springframework.data.cassandra.repository.Query

interface ReactionCountCassandraRepository : CassandraCounterRepository<ReactionCountEntity, ReactionCountPrimaryKey> {

    @Query(
        """
			update reaction_count_v1 set count = count + :count
			where workspace_id = :#{#key.workspaceId}
			and component_id = :#{#key.componentId}
		    and space_id = :#{#key.spaceId}
			and emotion_id = :#{#key.emotionId}
		"""
    )
    suspend fun increase(key: ReactionCountPrimaryKey, count: Long = 1L)

    @Query(
        """
			update reaction_count_v1 set count = count - :count
			where workspace_id = :#{#key.workspaceId}
			and component_id = :#{#key.componentId}
			and space_id = :#{#key.spaceId}
			and emotion_id = :#{#key.emotionId}
		"""
    )
    suspend fun decrease(key: ReactionCountPrimaryKey, count: Long = 1L)

    suspend fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceId(
        workspaceId: String,
        componentId: String,
        spaceId: String,
    ): List<ReactionCountEntity>

}