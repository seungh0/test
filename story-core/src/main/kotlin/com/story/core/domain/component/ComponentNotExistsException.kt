package com.story.core.domain.component

import com.story.core.common.error.ErrorCode
import com.story.core.common.error.StoryBaseException

data class ComponentNotExistsException(
    override val message: String,
    override val cause: Throwable? = null,
) : StoryBaseException(
    message = message,
    errorCode = ErrorCode.E404_NOT_EXISTS_COMPONENT,
    cause = cause,
)