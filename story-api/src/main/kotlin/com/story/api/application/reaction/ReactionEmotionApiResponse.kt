package com.story.api.application.reaction

import com.story.api.application.emotion.EmotionApiResponse
import com.story.core.domain.emotion.EmotionResponse
import com.story.core.domain.reaction.ReactionEmotionResponse

data class ReactionEmotionApiResponse(
    val emotion: EmotionApiResponse,
    val count: Long,
    val reactedByMe: Boolean,
) {

    companion object {
        fun of(
            reactionEmotion: ReactionEmotionResponse,
            emotion: EmotionResponse,
        ) = ReactionEmotionApiResponse(
            count = reactionEmotion.count,
            reactedByMe = reactionEmotion.reactedByMe,
            emotion = EmotionApiResponse.of(emotion = emotion)
        )
    }

}