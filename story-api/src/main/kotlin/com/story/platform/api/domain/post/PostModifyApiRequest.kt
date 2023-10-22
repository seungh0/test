package com.story.platform.api.domain.post

import com.story.platform.core.common.error.InvalidArgumentsException
import jakarta.validation.constraints.Size

data class PostModifyApiRequest(
    @field:Size(max = 100)
    val title: String?,

    @field:Size(max = 500)
    val content: String?,
) {

    init {
        if (title != null && title.isBlank()) {
            throw InvalidArgumentsException(
                message = "Post title($title)가 빈 값일 수 없습니다",
                reasons = listOf("title can't be blank"),
            )
        }

        if (title == null && content == null) {
            throw InvalidArgumentsException(
                message = "Post를 변경하기 위한 최소한의 한개 필드가 존재해야 합니다",
                reasons = listOf("all parameter can't be null"),
            )
        }
    }

}
