package com.github.chiriaccasian.autocompletecaching;

import com.github.chiriaccasian.autocompletecaching.Caching.CacheClient;
import com.github.chiriaccasian.autocompletecachingkotlinpackage.SimpleInlineCompletionProvider;
import com.intellij.codeInsight.inline.completion.InlineCompletionRequest;
import com.intellij.openapi.util.TextRange;
import io.github.ollama4j.exceptions.OllamaBaseException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import com.intellij.openapi.editor.Document;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tests {

    /**
     * Tests that the Ai model is properly linked to the AiModelClient with ollama4j
     */
    @Test
    public void testAiSuggestions() {
        AIModelClient aiModelClient = new AIModelClient();
        try {
            Assert.assertNotNull(aiModelClient.fetchAISuggestion("pri")); // AiSuggestions are not deterministic so we can only check if they are null or not
            Assert.assertNotNull(aiModelClient.fetchAISuggestion("Syst")); // AiSuggestions are not deterministic so we can only check if they are null or not
        } catch (OllamaBaseException | IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * @param inputs Strings typed by user in a mock environment
     * @param result a list of the words currently in the CacheTree
     * @throws Exception e
     */
    public void testCacheTreeWithMultipleInputs(List<String> inputs, String result){
        Document mockDocument = Mockito.mock(Document.class);
        int f = 1 ;
        List<InlineCompletionRequest> mockRequests = new ArrayList<>() ;
        for(String input : inputs){
            when(mockDocument.getText(new TextRange(0, f))).thenReturn(input);

            InlineCompletionRequest mockRequest = Mockito.mock(InlineCompletionRequest.class);
            when(mockRequest.getDocument()).thenReturn(mockDocument);
            when(mockRequest.getStartOffset()).thenReturn(0);
            when(mockRequest.getEndOffset()).thenReturn(f);
            mockRequests.add(mockRequest) ;
            f ++ ;
        }

        SimpleInlineCompletionProvider provider = new SimpleInlineCompletionProvider();

        for(InlineCompletionRequest mockRequest : mockRequests) {
            provider.getSuggestion(mockRequest, null);
        }

        CacheClient cacheClient = provider.getCacheClientInstance();
        String cacheTreeVisualisation = cacheClient.visualizeGraph();
        System.out.println(cacheTreeVisualisation);

        Assert.assertNotNull( cacheTreeVisualisation);
        Assert.assertEquals(result, cacheTreeVisualisation);
    }

    /**
     * Tests that words are properly added in the CacheTree
     */
    @Test
    public void testMultipleCacheTreeUpdates() {
        /// test one large phrase
        testCacheTreeWithMultipleInputs(List.of("System"), "_$System\n");
        testCacheTreeWithMultipleInputs(List.of("S"), "_$S\n");
        testCacheTreeWithMultipleInputs(List.of(""), "_$\n");
        testCacheTreeWithMultipleInputs(List.of("System Sys.ou GarbageColl pri private p System pri"), "_$System Sys.ou GarbageColl pri private p System pri\n");

        /// real world scenario test with multiple consequent (and some identical) contexts
        testCacheTreeWithMultipleInputs(List.of("System", "Sys.ou", "GarbageColl", "pri", "private", "p", "System", "pri"),
                "_$System\n_$Sys.ou\n_$GarbageColl\n_$private\n");
        testCacheTreeWithMultipleInputs(List.of("p", "r", "i","n", "p", "r","i", "p", "o"),
                "_$p\n_$r\n_$i\n_$n\n_$o\n");
    }

    /**
     *
     * @param inputs Strings typed by user in a mock environment
     * @param result a list of cache hits/misses encoded in null/""
     * @throws Exception
     */

    public void testCacheHitsAndMissesWithMultipleInputs(List<String> inputs, List<String> result) throws Exception {
        Document mockDocument = Mockito.mock(Document.class);
        int f = 1 ;
        List<InlineCompletionRequest> mockRequests = new ArrayList<>() ;
        for(String input : inputs){
            when(mockDocument.getText(new TextRange(0, f))).thenReturn(input);

            InlineCompletionRequest mockRequest = Mockito.mock(InlineCompletionRequest.class);
            when(mockRequest.getDocument()).thenReturn(mockDocument);
            when(mockRequest.getStartOffset()).thenReturn(0);
            when(mockRequest.getEndOffset()).thenReturn(f);
            mockRequests.add(mockRequest) ;
            f ++ ;
        }

        SimpleInlineCompletionProvider provider = new SimpleInlineCompletionProvider();
        List<String> testResult = new ArrayList<>() ;

        f = 0 ;
        for(InlineCompletionRequest mockRequest : mockRequests) {
            String cacheResult = provider.getCompletionFromCacheClientDirectly(inputs.get(f)) ;
            testResult.add((cacheResult == null)? null : "") ;
            provider.getSuggestion(mockRequest, null);
            f ++ ;
        }

        Assert.assertEquals(result, testResult);
    }

    /**
     * Testing for cache misses and cache hits, since cache misses and cache hits rely on previously typed data from user and not on
     * suggestion on this version of cache, the result is always deterministic thus can be tested
     * null means a cache miss and "" means a cache hit
     * @throws Exception
     */
    @Test
    public void testCacheHitsAndMisses() throws Exception {
        testCacheHitsAndMissesWithMultipleInputs(List.of("System", "Sys.ou", "GarbageColl", "pri", "private", "p", "System", "pri"),
                Arrays.asList(null, null, null, null, null, "", "", "")) ;

        testCacheHitsAndMissesWithMultipleInputs(List.of("System", "Sys.ou", "GarbageColl", "pri", "private", "p", "System", "pri", "G", "g", "g.", "GarbageColl", "", "P", "PSyste", "Syste"),
                Arrays.asList(null, null, null, null, null, "", "", "", "", null, null, "", null, null, null, "")) ;
    }
}