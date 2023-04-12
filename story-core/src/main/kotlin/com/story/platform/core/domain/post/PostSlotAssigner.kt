package com.story.platform.core.domain.post

import com.story.platform.core.common.utils.SlotAssigner

object PostSlotAssigner {

    fun assign(
        postId: Long,
    ) = SlotAssigner.assign(
        id = postId,
        firstSlotId = FIRST_SLOT_ID,
        slotSize = SLOT_SIZE,
    )

    private const val SLOT_SIZE = 15_000
    const val FIRST_SLOT_ID = 1L

}