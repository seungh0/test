package com.story.api.application.post

import com.fasterxml.jackson.annotation.JsonIgnore
import com.story.core.domain.post.section.PostSectionContentRequest
import jakarta.validation.constraints.Size

data class PostModifyApiRequest(
    @field:Size(max = 100)
    val title: String?,
    val sections: List<PostSectionApiRequest>?,
    val extra: Map<String, String>?,
) {

    @JsonIgnore
    fun toSections(): List<PostSectionContentRequest>? {
        if (sections == null) {
            return null
        }
        return sections.map { section -> section.toSections() }
    }

}
