package com.github.chiriaccasian.autocompletecachingkotlinpackage

import com.github.chiriaccasian.autocompletecaching.AIModelClient
import com.github.chiriaccasian.autocompletecaching.Caching.CacheClient
import com.intellij.codeInsight.inline.completion.*
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionElement
import com.intellij.codeInsight.inline.completion.elements.InlineCompletionGrayTextElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest
import com.intellij.openapi.util.TextRange

public class SimpleInlineCompletionProvider : InlineCompletionProvider {
    private val suggestion: List<InlineCompletionElement> = listOf()

    /**
     * Unique identifier for this inline completion provider.
     * Provider should have singleton behaviour for now
     */
    override val id: InlineCompletionProviderID = InlineCompletionProviderID("SimpleInlineCompletionProvider")
    private val AIModelClientInstance: AIModelClient
    public val CacheClientInstance: CacheClient
    @Volatile
    public var cachingEnabled: Boolean
    init {
        if(instance == null){
            instance = this
        }
        AIModelClientInstance = AIModelClient()
        CacheClientInstance = CacheClient()
        cachingEnabled = true
    }
    /**
     * Provides a suggestion based on the current context given
     *
     * @param request The inline completion request
     * @return An inline completion suggestion
     */
    override suspend fun getSuggestion(request: InlineCompletionRequest): InlineCompletionSuggestion {
        val context = getCurrentWord(request)
        var suggestion = CacheClientInstance.getSuggestion(context)
        if(cachingEnabled == true){
            if(suggestion == null) {
                suggestion = AIModelClientInstance.fetchAISuggestion(context)
                CacheClientInstance.cacheSuggestion(context, suggestion) ;
                print("miss ") /// some debugging codes
            }else print("hit ")
        }else {
            suggestion = AIModelClientInstance.fetchAISuggestion(context)
            CacheClientInstance.cacheSuggestion(context, suggestion) ;
            print("Cache Disabled")
        }
        ///println(CacheClientInstance.visualizeGraph())
        var truncSuggestion = suggestion
        truncSuggestion = truncSuggestion.removePrefix(context)

        val element = InlineCompletionGrayTextElement(truncSuggestion)
        val suggestionFlow: Flow<InlineCompletionElement> = flowOf(element)
        return InlineCompletionSingleSuggestion(suggestionFlow)
    }
    override fun isEnabled(event: InlineCompletionEvent): Boolean {
        return event is InlineCompletionEvent.DocumentChange
    }

    /**
     * Extracts the current word or phrase from the document based on the request.
     * Words are defined by the regex w and . so System.out is a word
     *
     * @param request The inline completion request containing the document and offsets
     * @return The current word or phrase
     */
    fun getCurrentWord(request: InlineCompletionRequest): String {
        val document = request.document
        var startOffset = request.startOffset
        val endOffset = request.endOffset

        while (startOffset > 0 && document.getText(TextRange(startOffset - 1, startOffset)).matches(Regex("[\\w.]"))) {
            startOffset-= 1
        }

        return document.getText(TextRange(startOffset, endOffset))
    }

    /**
     * Function used in testing for checking if a context would be a cache hit or miss
     */
    fun getCompletionFromCacheClientDirectly(request: String): String? {
        return CacheClientInstance.getSuggestion(request) ;
    }
    companion object {
        private var instance: SimpleInlineCompletionProvider? = null
        /**
         * Retrieves the singleton instance of the provider
         *
         * @return The singleton instance.
         * @throws IllegalStateException if the instance has not been created yet
         */
        @JvmStatic
        fun getInstance(): SimpleInlineCompletionProvider {
            return instance ?: throw IllegalStateException("Instance not created yet!")
        }
    }
}


internal class InlineCompletionSingleSuggestion(
        override val suggestionFlow: Flow<InlineCompletionElement>
) : InlineCompletionSuggestion()
