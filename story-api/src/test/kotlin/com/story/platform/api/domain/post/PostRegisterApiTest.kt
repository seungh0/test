package com.story.platform.api.domain.post

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.config.auth.AuthContextMethodArgumentResolver
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.component.ComponentHandler
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.post.PostRegisterHandler
import com.story.platform.core.domain.post.PostSpaceKey
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(
    PostRegisterApi::class,
    AuthContextMethodArgumentResolver::class,
)
class PostRegisterApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val postRegisterHandler: PostRegisterHandler,

    @MockkBean
    private val authenticationHandler: AuthenticationHandler,

    @MockkBean
    private val componentHandler: ComponentHandler,
) : FunSpec({

    beforeEach {
        coEvery { authenticationHandler.handleAuthentication(any()) } returns AuthenticationResponse(
            workspaceId = "twitter",
            authenticationKey = "api-key",
            status = AuthenticationKeyStatus.ENABLED,
        )

        coEvery { componentHandler.validateComponent(any(), any(), any()) } returns Unit
    }

    test("새로운 포스트를 등록한다") {
        // given
        val componentId = "post"
        val spaceId = "계정의 ID"

        val request = PostRegisterApiRequest(
            accountId = spaceId,
            title = "토끼가 너무 좋아요",
            content = """
                    내가 만든 쿠키~
                    너를 위해 구워찌이
            """.trimIndent()
        )

        coEvery {
            postRegisterHandler.register(
                postSpaceKey = PostSpaceKey(
                    workspaceId = "twitter",
                    componentId = componentId,
                    spaceId = spaceId,
                ),
                accountId = spaceId,
                title = request.title,
                content = request.content,
                extraJson = request.extraJson,
            )
        } returns 1

        // when
        val exchange = webTestClient.post()
            .uri("/v1/posts/components/{componentId}/spaces/{spaceId}/posts", componentId, spaceId)
            .headers(WebClientUtils.commonHeaders)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.result.postId").isEqualTo("1")
    }

})
