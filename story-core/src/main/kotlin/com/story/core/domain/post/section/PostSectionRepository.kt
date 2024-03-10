package com.story.core.domain.post.section

import com.story.core.infrastructure.cassandra.CassandraBasicRepository
import kotlinx.coroutines.flow.Flow

interface PostSectionRepository : CassandraBasicRepository<PostSection, PostSectionPrimaryKey> {

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNo(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
        postNo: Long,
    ): Flow<PostSection>

    fun findAllByKeyWorkspaceIdAndKeyComponentIdAndKeySpaceIdAndKeyParentIdAndKeySlotIdAndKeyPostNoIn(
        workspaceId: String,
        componentId: String,
        spaceId: String,
        parentId: String,
        slotId: Long,
        postNos: Collection<Long>,
    ): Flow<PostSection>

}
