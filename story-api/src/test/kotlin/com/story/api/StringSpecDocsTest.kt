package com.story.api

import com.ninjasquad.springmockk.MockkBean
import com.story.api.application.apikey.ApiKeyHandler
import com.story.api.application.workspace.WorkspaceRetrieveHandler
import com.story.core.domain.apikey.ApiKeyResponse
import com.story.core.domain.apikey.ApiKeyStatus
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.mockk.coEvery

abstract class StringSpecDocsTest(body: StringSpec.() -> Unit = {}) : StringSpec() {

    @MockkBean
    private lateinit var apiKeyHandler: ApiKeyHandler

    @MockkBean
    private lateinit var workspaceRetrieveHandler: WorkspaceRetrieveHandler

    override suspend fun beforeEach(testCase: TestCase) {
        coEvery { apiKeyHandler.handleApiKey(any()) } returns ApiKeyResponse(
            workspaceId = "story",
            status = ApiKeyStatus.ENABLED,
            description = "",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    init {
        body()
    }

}
