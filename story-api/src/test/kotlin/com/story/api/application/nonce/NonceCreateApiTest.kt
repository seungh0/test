package com.story.api.application.nonce

import com.ninjasquad.springmockk.MockkBean
import com.story.api.ApiTest
import com.story.api.DocsTest
import com.story.api.FunSpecDocsTest
import com.story.api.lib.PageHeaderSnippet.Companion.pageHeaderSnippet
import com.story.api.lib.RestDocsUtils
import com.story.api.lib.RestDocsUtils.getDocumentRequest
import com.story.api.lib.RestDocsUtils.getDocumentResponse
import com.story.api.lib.WebClientUtils
import com.story.core.domain.nonce.NonceManager
import io.mockk.coEvery
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@DocsTest
@ApiTest(NonceCreateApi::class)
class NonceCreateApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val nonceManager: NonceManager,
) : FunSpecDocsTest({

    test("새로운 Nonce를 할당 받습니다") {
        // given
        coEvery {
            nonceManager.create(any())
        } returns UUID.randomUUID().toString()

        val request = NonceCreateRequest(
            expirationSeconds = 3_600,
        )

        // when
        val exchange = webTestClient.post()
            .uri("/v1/nonce")
            .headers(WebClientUtils.apiKeyHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "nonce.create",
                    getDocumentRequest(),
                    getDocumentResponse(),
                    pageHeaderSnippet(),
                    RestDocsUtils.apiKeyHeaderDocumentation,
                    requestFields(
                        fieldWithPath("expirationSeconds").type(JsonFieldType.NUMBER)
                            .description("유효 기간 (초 단위)")
                            .optional()
                            .attributes(RestDocsUtils.remarks("0보다 커야하고, 최대 3600(1시간)까지 사옹할 수 있습니다. [기본 값은 1분(60)입니다]")),
                    ),
                    responseFields(
                        fieldWithPath("ok")
                            .type(JsonFieldType.BOOLEAN).description("성공 여부"),
                        fieldWithPath("result")
                            .type(JsonFieldType.OBJECT).description("요청 결과"),
                        fieldWithPath("result.nonce")
                            .type(JsonFieldType.STRING).description("논스 토큰"),
                    )
                )
            )
    }

})
