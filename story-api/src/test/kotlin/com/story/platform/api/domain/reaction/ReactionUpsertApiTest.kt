package com.story.platform.api.domain.reaction

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.ApiTest
import com.story.platform.api.DocsTest
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.workspace.WorkspaceRetrieveHandler
import com.story.platform.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.platform.api.lib.RestDocsUtils.getDocumentRequest
import com.story.platform.api.lib.RestDocsUtils.getDocumentResponse
import com.story.platform.api.lib.RestDocsUtils.remarks
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.authentication.AuthenticationStatus
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient

@DocsTest
@ApiTest(ReactionUpsertApi::class)
class ReactionUpsertApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val reactionUpsertHandler: ReactionUpsertHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,

    @MockkBean
    private val workspaceRetrieveHandler: WorkspaceRetrieveHandler,
) : FunSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "twitter",
            authenticationKey = "api-key",
            status = AuthenticationStatus.ENABLED,
            description = "",
        )
        coEvery { workspaceRetrieveHandler.validateEnabledWorkspace(any()) } returns Unit
    }

    test("대상에 리액션을 등록한다") {
        // given
        val componentId = "post-like"
        val accountId = "accountId"
        val targetId = "post-id"
        val workspaceId = "twitter"

        val request = ReactionUpsertApiRequest(
            accountId = accountId,
            emotions = setOf(
                ReactionEmotionUpsertApiRequest(
                    emotionId = "1",
                ),
                ReactionEmotionUpsertApiRequest(
                    emotionId = "2",
                )
            )
        )

        coEvery {
            reactionUpsertHandler.upsert(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                request = request,
            )
        } returns Unit

        // when
        val exchange = webTestClient.put()
            .uri("/v1/reactions/components/{componentId}/targets/{targetId}", componentId, targetId)
            .headers(WebClientUtils.authenticationHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "REACTION-UPSERT-API",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    pathParameters(
                        parameterWithName("componentId").description("Component Id"),
                        parameterWithName("targetId").description("Target Id")
                    ),
                    requestFields(
                        fieldWithPath("accountId").type(JsonFieldType.STRING)
                            .description("Reactor Id")
                            .attributes(remarks("must be within 100 characters")),
                        fieldWithPath("emotions").type(JsonFieldType.ARRAY)
                            .description("Reaction Emotions")
                            .attributes(remarks("must be within 20 elements")),
                        fieldWithPath("emotions[].emotionId").type(JsonFieldType.STRING)
                            .description("Reaction Emotion id")
                            .attributes(remarks("must be within 100 characters")),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("ok"),
                    )
                )
            )
    }

})
