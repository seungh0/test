package com.story.platform.api.domain.post

import com.ninjasquad.springmockk.MockkBean
import com.story.platform.api.config.auth.AuthContextMethodArgumentResolver
import com.story.platform.api.domain.authentication.AuthenticationHandler
import com.story.platform.api.domain.component.ComponentHandler
import com.story.platform.api.lib.WebClientUtils
import com.story.platform.core.common.model.ApiResponse
import com.story.platform.core.domain.authentication.AuthenticationKeyStatus
import com.story.platform.core.domain.authentication.AuthenticationResponse
import com.story.platform.core.domain.post.PostRemoveHandler
import com.story.platform.core.domain.post.PostSpaceKey
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(
    PostRemoveApi::class,
    AuthContextMethodArgumentResolver::class,
)
class PostRemoveApiTest(
    private val webTestClient: WebTestClient,

    @MockkBean
    private val postRemoveHandler: PostRemoveHandler,

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

    test("기존에 등록된 포스트를 삭제한다") {
        // given
        val postSpaceKey = PostSpaceKey(
            workspaceId = "twitter",
            componentId = "post",
            spaceId = "게시글 공간 ID"
        )

        val accountId = "작성자 ID"
        val postId = 30000L

        coEvery {
            postRemoveHandler.remove(
                postSpaceKey = postSpaceKey,
                accountId = accountId,
                postId = postId,
            )
        } returns Unit

        // when
        val exchange = webTestClient.delete()
            .uri(
                "/v1/posts/components/{componentId}/spaces/{spaceId}/posts/{postId}?accountId={accountId}",
                postSpaceKey.componentId,
                postSpaceKey.spaceId,
                postId,
                accountId
            )
            .headers(WebClientUtils.commonHeaders)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()

        // then
        exchange.expectStatus().isOk
            .expectBody()
            .jsonPath("$.result").isEqualTo(ApiResponse.OK.result!!)
    }

})
