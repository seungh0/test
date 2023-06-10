package com.story.platform.core.domain.post

import com.story.platform.core.common.enums.ServiceType
import com.story.platform.core.common.model.AuditingTime
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED
import org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED
import org.springframework.data.cassandra.core.mapping.CassandraType
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.BIGINT
import org.springframework.data.cassandra.core.mapping.CassandraType.Name.TEXT
import org.springframework.data.cassandra.core.mapping.Embedded
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

@Table("post_v1")
data class Post(
    @field:PrimaryKey
    val key: PostPrimaryKey,

    @field:CassandraType(type = TEXT)
    val accountId: String,

    @field:CassandraType(type = TEXT)
    var title: String,

    @field:CassandraType(type = TEXT)
    var content: String,

    @field:CassandraType(type = TEXT)
    var extraJson: String?,

    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    val auditingTime: AuditingTime,
) {

    fun isOwner(accountId: String): Boolean {
        return this.accountId == accountId
    }

    fun modify(
        title: String,
        content: String,
        extraJson: String?,
    ) {
        this.title = title
        this.content = content
        this.extraJson = extraJson
        this.auditingTime.updated()
    }

    companion object {
        fun of(
            postSpaceKey: PostSpaceKey,
            accountId: String,
            postId: Long,
            title: String,
            content: String,
            extraJson: String?,
        ) = Post(
            key = PostPrimaryKey.of(
                postSpaceKey = postSpaceKey,
                postId = postId,
            ),
            accountId = accountId,
            title = title,
            content = content,
            extraJson = extraJson,
            auditingTime = AuditingTime.newEntity(),
        )
    }

}

@PrimaryKeyClass
data class PostPrimaryKey(
    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 1)
    @field:CassandraType(type = TEXT)
    val serviceType: ServiceType,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 2)
    @field:CassandraType(type = TEXT)
    val spaceType: PostSpaceType,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 3)
    @field:CassandraType(type = TEXT)
    val spaceId: String,

    @field:PrimaryKeyColumn(type = PARTITIONED, ordinal = 4)
    @field:CassandraType(type = BIGINT)
    val slotId: Long,

    @field:PrimaryKeyColumn(type = CLUSTERED, ordering = Ordering.DESCENDING, ordinal = 5)
    @field:CassandraType(type = BIGINT)
    val postId: Long,
) {

    companion object {
        fun of(
            postSpaceKey: PostSpaceKey,
            postId: Long,
        ) = PostPrimaryKey(
            serviceType = postSpaceKey.serviceType,
            spaceType = postSpaceKey.spaceType,
            spaceId = postSpaceKey.spaceId,
            slotId = PostSlotAssigner.assign(postId),
            postId = postId,
        )
    }

}
