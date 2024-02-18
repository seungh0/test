package com.story.core.common.model.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.story.core.common.model.AuditingTime
import java.time.LocalDateTime

abstract class AuditingTimeResponse {

    @field:JsonProperty
    lateinit var createdAt: LocalDateTime

    @field:JsonProperty
    lateinit var updatedAt: LocalDateTime

    @JsonIgnore
    fun setAuditingTime(auditingTime: AuditingTime) {
        this.createdAt = auditingTime.createdAt
        this.updatedAt = auditingTime.updatedAt
    }

    @JsonIgnore
    fun setAuditingTime(auditingTimeResponse: AuditingTimeResponse) {
        this.createdAt = auditingTimeResponse.createdAt
        this.updatedAt = auditingTimeResponse.updatedAt
    }

}
