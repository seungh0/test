package com.story.core.domain.subscription

import com.story.core.FunSpecIntegrationTest
import com.story.core.IntegrationTest
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.toList

@IntegrationTest
internal class SubscriptionSubscriberTest(
    private val subscriptionSubscriber: SubscriptionSubscriber,
    private val subscriberRepository: SubscriberRepository,
    private val subscriptionRepository: SubscriptionRepository,
) : FunSpecIntegrationTest({

    context("구독을 추가한다") {
        test("새로운 구독 대상을 추가합니다") {
            // given
            val workspaceId = "story"
            val componentId = "follow"
            val targetId = "10000"
            val subscriberId = "2000"
            val alarm = true

            // when
            subscriptionSubscriber.upsertSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
                alarm = alarm,
            )

            // then
            val subscribers = subscriberRepository.findAll().toList()
            subscribers shouldHaveSize 1
            subscribers[0].also {
                it.key.workspaceId shouldBe workspaceId
                it.key.componentId shouldBe componentId
                it.key.subscriberId shouldBe subscriberId
                it.key.slotId shouldBe 1L
                it.key.targetId shouldBe targetId
                it.alarmEnabled shouldBe alarm
            }

            val subscriptions = subscriptionRepository.findAll().toList()
            subscriptions shouldHaveSize 1
            subscriptions[0].also {
                it.key.workspaceId shouldBe workspaceId
                it.key.componentId shouldBe componentId
                it.key.distributionKey shouldBe SubscriptionDistributionKey.makeKey(subscriberId)
                it.key.subscriberId shouldBe subscriberId
                it.key.targetId shouldBe targetId
                it.slotId shouldBe 1L
                it.status shouldBe SubscriptionStatus.ACTIVE
                it.alarmEnabled shouldBe alarm
            }
        }

        test("기존에 등록한 구독의 알람 설정을 변경한다") {
            // given
            val workspaceId = "story"
            val componentId = "follow"
            val targetId = "10000"
            val subscriberId = "2000"
            val alarm = true

            subscriberRepository.save(
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = targetId,
                    slotId = 1L,
                    alarmEnabled = false,
                )
            )

            subscriptionRepository.save(
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = targetId,
                    slotId = 1L,
                    alarmEnabled = false,
                )
            )

            // when
            subscriptionSubscriber.upsertSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
                alarm = alarm,
            )

            // then
            val subscribers: List<SubscriberEntity> = subscriberRepository.findAll().toList()

            subscribers shouldHaveSize 1
            subscribers[0].also {
                it.key.workspaceId shouldBe workspaceId
                it.key.componentId shouldBe componentId
                it.key.subscriberId shouldBe subscriberId
                it.key.slotId shouldBe 1L
                it.key.targetId shouldBe targetId
                it.alarmEnabled shouldBe alarm
            }

            val subscriptions = subscriptionRepository.findAll().toList()
            subscriptions shouldHaveSize 1
            subscriptions[0].also {
                it.key.workspaceId shouldBe workspaceId
                it.key.componentId shouldBe componentId
                it.key.distributionKey shouldBe SubscriptionDistributionKey.makeKey(subscriberId)
                it.key.subscriberId shouldBe subscriberId
                it.key.targetId shouldBe targetId
                it.slotId shouldBe 1L
                it.status shouldBe SubscriptionStatus.ACTIVE
                it.alarmEnabled shouldBe alarm
            }
        }

        test("구독 등록시, 이미 구독한 대상인 경우, 멱등성을 보장한다") {
            // given
            val workspaceId = "story"
            val componentId = "follow"
            val targetId = "10000"
            val subscriberId = "2000"
            val alarm = true

            subscriberRepository.save(
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = targetId,
                    slotId = 1L,
                )
            )

            subscriptionRepository.save(
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = targetId,
                    slotId = 1L,
                )
            )

            // when
            subscriptionSubscriber.upsertSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
                alarm = alarm,
            )

            // then
            val subscribers: List<SubscriberEntity> = subscriberRepository.findAll().toList()

            subscribers shouldHaveSize 1
            subscribers[0].also {
                it.key.workspaceId shouldBe workspaceId
                it.key.componentId shouldBe componentId
                it.key.subscriberId shouldBe subscriberId
                it.key.slotId shouldBe 1L
                it.key.targetId shouldBe targetId
                it.alarmEnabled shouldBe alarm
            }

            val subscriptions = subscriptionRepository.findAll().toList()
            subscriptions shouldHaveSize 1
            subscriptions[0].also {
                it.key.workspaceId shouldBe workspaceId
                it.key.componentId shouldBe componentId
                it.key.distributionKey shouldBe SubscriptionDistributionKey.makeKey(subscriberId)
                it.key.subscriberId shouldBe subscriberId
                it.key.targetId shouldBe targetId
                it.slotId shouldBe 1L
                it.status shouldBe SubscriptionStatus.ACTIVE
                it.alarmEnabled shouldBe alarm
            }
        }

        test("구독 등록시, 기존에 구독 취소 이력이 있다면, 기존과 동일한 슬롯에 추가한다") {
            // given
            val workspaceId = "story"
            val componentId = "follow"
            val targetId = "10000"
            val subscriberId = "2000"
            val slotId = 500L
            val alarm = true

            subscriberRepository.save(
                SubscriberFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = targetId,
                    slotId = slotId,
                )
            )

            subscriptionRepository.save(
                SubscriptionFixture.create(
                    workspaceId = workspaceId,
                    componentId = componentId,
                    subscriberId = subscriberId,
                    targetId = targetId,
                    slotId = slotId,
                    status = SubscriptionStatus.DELETED,
                )
            )

            // when
            subscriptionSubscriber.upsertSubscription(
                workspaceId = workspaceId,
                componentId = componentId,
                targetId = targetId,
                subscriberId = subscriberId,
                alarm = alarm,
            )

            // then
            val subscribers: List<SubscriberEntity> = subscriberRepository.findAll().toList()

            subscribers shouldHaveSize 1
            subscribers[0].also {
                it.key.workspaceId shouldBe workspaceId
                it.key.componentId shouldBe componentId
                it.key.subscriberId shouldBe subscriberId
                it.key.slotId shouldBe slotId
                it.key.targetId shouldBe targetId
                it.alarmEnabled shouldBe alarm
            }

            val subscriptions = subscriptionRepository.findAll().toList()
            subscriptions shouldHaveSize 1
            subscriptions[0].also {
                it.key.workspaceId shouldBe workspaceId
                it.key.componentId shouldBe componentId
                it.key.distributionKey shouldBe SubscriptionDistributionKey.makeKey(subscriberId)
                it.key.subscriberId shouldBe subscriberId
                it.key.targetId shouldBe targetId
                it.slotId shouldBe slotId
                it.status shouldBe SubscriptionStatus.ACTIVE
                it.alarmEnabled shouldBe alarm
            }
        }
    }

})