package com.story.platform.core.common.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.story.platform.core.common.error.ErrorCode

data class ApiResponse<T>(
    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val code: String? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val message: String? = null,

    val result: T?,
) {

    companion object {
        fun <T> success(result: T): ApiResponse<T> =
            ApiResponse(result = result)

        fun <T> error(
            error: ErrorCode,
            message: String? = null,
        ): ApiResponse<T> = ApiResponse(
            code = "${error.httpStatusCode}${error.minorStatusCode}",
            message = message ?: error.errorMessage,
            result = null,
        )

        val OK: ApiResponse<String> = success(result = "OK")
    }

}
