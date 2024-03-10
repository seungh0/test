package com.story.core.domain.post.section

import com.story.core.domain.post.PostId
import com.story.core.domain.post.PostSpaceKey
import org.springframework.stereotype.Service

@Service
class PostSectionManager(
    private val postSectionHandlerFinder: PostSectionHandlerFinder,
) {

    suspend fun makePostSections(
        postSpaceKey: PostSpaceKey,
        parentId: PostId?,
        ownerId: String,
        postNo: Long,
        requests: Collection<PostSectionContentRequest>,
    ): List<PostSection> {
        val sectionContents = mutableMapOf<PostSectionContentRequest, PostSectionContent>()
        for ((sectionType, sectionRequests) in requests.groupBy { request -> request.sectionType() }.entries) {
            val contents = postSectionHandlerFinder[sectionType].makeContents(
                workspaceId = postSpaceKey.workspaceId,
                requests = sectionRequests
            )
            sectionContents += sectionRequests.associateWith { request -> contents[request]!! }
        }

        return requests.map { request ->
            PostSection.of(
                postSpaceKey = postSpaceKey,
                parentId = parentId,
                postNo = postNo,
                content = sectionContents[request]!!,
                sectionType = request.sectionType(),
                priority = request.priority,
            )
        }
    }

    suspend fun makePostSectionContentResponse(sections: Collection<PostSection>): List<PostSectionContentResponse> {
        val sectionContents = mutableMapOf<PostSectionPrimaryKey, PostSectionContent>()
        for ((sectionType, sectionsGroupByType) in sections.groupBy { section -> section.sectionType }.entries) {
            sectionContents += sectionsGroupByType.associate { section -> section.key to sectionType.toContent(section.data) }
        }
        return transformToPostSectionContentResponse(sections.mapNotNull { section -> sectionContents[section.key] })
    }

    private suspend fun transformToPostSectionContentResponse(sections: Collection<PostSectionContent>): List<PostSectionContentResponse> {
        val sectionContents = mutableMapOf<PostSectionContent, PostSectionContentResponse>()
        for ((sectionType, sectionsGroupByType) in sections.groupBy { section -> section.sectionType() }.entries) {
            sectionContents += postSectionHandlerFinder[sectionType].makeContentResponse(sectionsGroupByType)
        }
        return sections.mapNotNull { section -> sectionContents[section] }
    }

}
