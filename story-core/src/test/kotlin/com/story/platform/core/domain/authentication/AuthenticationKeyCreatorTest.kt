package com.story.platform.core.domain.authentication

import com.story.platform.core.IntegrationTest
import com.story.platform.core.lib.TestCleaner
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
class AuthenticationKeyCreatorTest(
    private val authenticationKeyRepository: AuthenticationKeyRepository,
    private val authenticationKeyCreator: AuthenticationKeyCreator,
    private val testCleaner: TestCleaner,
) : FunSpec({

    afterEach {
        testCleaner.cleanUp()
    }

    context("신규 서비스 인증 키를 등록한다") {
        test("새로운 인증 키를 등록한다") {
            // given
            val workspaceId = "twitter"
            val apiKey = "api-key"
            val description = "트위터 피드 API Key"

            // when
            authenticationKeyCreator.createAuthenticationKey(
                workspaceId = workspaceId,
                authenticationKey = apiKey,
                description = description,
            )

            // then
            val authenticationKeys = authenticationKeyRepository.findAll().toList()
            authenticationKeys shouldHaveSize 1
            authenticationKeys[0].also {
                it.key.workspaceId shouldBe workspaceId
                it.key.authenticationKey shouldBe apiKey
                it.description shouldBe description
                it.status shouldBe AuthenticationKeyStatus.ENABLED
                it.auditingTime.createdAt shouldNotBe null
                it.auditingTime.updatedAt shouldBe it.auditingTime.createdAt
            }
        }

        test("사용하는 서비스에 이미 등록되어 있는 API-Key인 경우, 중복 등록할 수 없다") {
            // given
            val authenticationKey = AuthenticationKeyFixture.create()
            authenticationKeyRepository.save(authenticationKey)

            // when & then
            shouldThrowExactly<AuthenticationKeyAlreadyExistsException> {
                authenticationKeyCreator.createAuthenticationKey(
                    workspaceId = authenticationKey.key.workspaceId,
                    authenticationKey = authenticationKey.key.authenticationKey,
                    description = "",
                )
            }
        }
    }

})